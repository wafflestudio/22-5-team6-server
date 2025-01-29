package com.example.toyTeam6Airbnb

import com.example.toyTeam6Airbnb.room.persistence.Address
import com.example.toyTeam6Airbnb.room.persistence.RoomDetails
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.room.persistence.RoomType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
class RoomControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var dataGenerator: DataGenerator

    @Autowired
    private lateinit var roomRepository: RoomRepository

    @Test
    fun `should create a new room`() {
        val (user, token) = dataGenerator.generateUserAndToken()

        val requestBody = """
        {
            "roomName": "Sample Room",
            "description": "A beautiful and cozy apartment located in the heart of Seoul. Perfect for travelers!",
            "roomType": "APARTMENT",
            "address": {
                "sido": "Seoul",
                "sigungu": "Jongno-gu",
                "street": "123 Hanok Street",
                "detail": "Apartment 5B"
            },
            "roomDetails": {
                "wifi": true,
                "selfCheckin": false,
                "luggage": false,
                "tv": true,
                "bedroom": 1,
                "bathroom": 1,
                "bed": 1
            },
            "price": {
                "perNight": 5000,
                "cleaningFee": 10000,
                "charge": 500,
                "total": 0
            },
            "maxOccupancy": 1
        }
        """.trimIndent()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/rooms")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn()

        val responseContent = result.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)
    }

    @Test
    fun `created room should be accessible`() {
        val (user, token) = dataGenerator.generateUserAndToken()

        // create a room with mockmvc
        val requestBody = """
        {
            "roomName": "Sample Room_2",
            "description": "A nice place to stay",
            "roomType": "VILLA",
            "address": {
                "sido": "Seoul",
                "sigungu": "Gangnam",
                "street": "Gangnam Daero",
                "detail": "34567"
            },
            "roomDetails": {
                "wifi": true,
                "selfCheckin": false,
                "luggage": false,
                "tv": true,
                "bedroom": 1,
                "bathroom": 1,
                "bed": 1
            },
            "price": {
                "perNight": 5000,
                "cleaningFee": 10000,
                "charge": 500,
                "total": 0
            },
            "maxOccupancy": 1
        }
        """.trimIndent()

        val create_result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/rooms")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn()
            .response

        // extract room id from create_result's body
        val responseBody = create_result.contentAsString
        val roomId = responseBody.split("\"roomId\":")[1].split(",")[0].toLong()

        // room should be accessible without authentication
        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/$roomId")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val responseContent = result.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)
    }

    @Test
    fun `should get 3 rooms from rooms main`() {
        val room1 = dataGenerator.generateRoom()
        val room2 = dataGenerator.generateRoom()
        val room3 = dataGenerator.generateRoom()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main?page=0&size=3&sort=id,desc")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString

        // check result length
        Assertions.assertEquals(getContentLength(result), 3)

        // check if all rooms are in the result
        Assertions.assertEquals(getNthContentId(result, 0), room3.id)
        Assertions.assertEquals(getNthContentId(result, 1), room2.id)
        Assertions.assertEquals(getNthContentId(result, 2), room1.id)

        // Add assertions to verify the response content if needed
        println(result)
    }

    fun getNthContentId(jsonString: String, n: Int): Long? {
        val objectMapper = ObjectMapper()
        val rootNode: JsonNode = objectMapper.readTree(jsonString)
        val contentNode: JsonNode = rootNode.path("content")
        return if (contentNode.isArray && contentNode.size() > n) {
            contentNode[n].path("roomId").asLong()
        } else {
            null
        }
    }

    fun getContentLength(jsonString: String): Int {
        val objectMapper = ObjectMapper()
        val rootNode: JsonNode = objectMapper.readTree(jsonString)
        val contentNode: JsonNode = rootNode.path("content")
        return if (contentNode.isArray) contentNode.size() else 0
    }

    fun getContent(jsonString: String): JsonNode {
        val objectMapper = ObjectMapper()
        val rootNode = objectMapper.readTree(jsonString)
        return rootNode.path("content")
    }

    @Test
    fun `should be able to update room info, only for owner`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val (user2, token2) = dataGenerator.generateUserAndToken()

        val requestBody = """
        {
            "roomName": "Sample Room",
            "description": "A beautiful and cozy apartment located in the heart of Seoul. Perfect for travelers!",
            "roomType": "APARTMENT",
            "address": {
                "sido": "Seoul",
                "sigungu": "Jongno-gu",
                "street": "123 Hanok Street",
                "detail": "Apartment 5B"
            },
            "roomDetails": {
                "wifi": true,
                "selfCheckin": false,
                "luggage": false,
                "tv": true,
                "bedroom": 1,
                "bathroom": 1,
                "bed": 1
            },
            "price": {
                "perNight": 5000,
                "cleaningFee": 10000,
                "charge": 500,
                "total": 0
            },
            "maxOccupancy": 1
        }
        """.trimIndent()
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/rooms")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn()

        val responseContent = result.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)

        val roomId = responseContent.split("\"roomId\":")[1].split(",")[0].toLong()

        val updateRequestBody = """
            {
            "roomName": "Sample Room_2",
            "description": "A nice place to stay",
            "roomType": "VILLA",
            "address": {
                "sido": "Seoul",
                "sigungu": "Gangnam",
                "street": "Gangnam Daero",
                "detail": "34567"
            },
            "roomDetails": {
                "wifi": true,
                "selfCheckin": false,
                "luggage": false,
                "tv": true,
                "bedroom": 1,
                "bathroom": 1,
                "bed": 1
            },
            "price": {
                "perNight": 5000,
                "cleaningFee": 10000,
                "charge": 500,
                "total": 0
            },
            "maxOccupancy": 1
        }
        """.trimIndent()

        val updateResult = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/rooms/$roomId")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestBody)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val updateResponseContent = updateResult.response.contentAsString
        // Add assertions to verify the response content if needed
        println(updateResponseContent)

        // verify 403 forbidden for user 2
        val updateResult2 = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/rooms/$roomId")
                .header("Authorization", "Bearer $token2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestBody)
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
            .andReturn()
    }

    @Test
    fun `should be able to delete room, only for owner`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val (user2, token2) = dataGenerator.generateUserAndToken()

        val requestBody = """
        {
            "roomName": "Sample Room",
            "description": "A beautiful and cozy apartment located in the heart of Seoul. Perfect for travelers!",
            "roomType": "APARTMENT",
            "address": {
                "sido": "Seoul",
                "sigungu": "Jongno-gu",
                "street": "123 Hanok Street",
                "detail": "Apartment 5B"
            },
            "roomDetails": {
                "wifi": true,
                "selfCheckin": false,
                "luggage": false,
                "tv": true,
                "bedroom": 1,
                "bathroom": 1,
                "bed": 1
            },
            "price": {
                "perNight": 5000,
                "cleaningFee": 10000,
                "charge": 500,
                "total": 0
            },
            "maxOccupancy": 1
        }
        """.trimIndent()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/rooms")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn()

        val responseContent = result.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)

        val roomId = responseContent.split("\"roomId\":")[1].split(",")[0].toLong()

        // verify 403 forbidden for user 2
        val deleteResult2 = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/rooms/$roomId")
                .header("Authorization", "Bearer $token2")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
            .andReturn()

        val deleteResult = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/rooms/$roomId")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andReturn()

        val deleteResponseContent = deleteResult.response.contentAsString
        // Add assertions to verify the response content if needed
        println(deleteResponseContent)

        // verify 404 not found
        val getAfterDelete = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/$roomId")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andReturn()
    }

    @Test
    fun `should search rooms by name`() {
        val room1 = dataGenerator.generateRoom(name = "Room1")
        val room2 = dataGenerator.generateRoom(name = "Room2")

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/search")
                .param("roomName", "Room1")
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString

        // check result length
        Assertions.assertEquals(getContentLength(result), 1)

        // check if all rooms are in the result
        Assertions.assertEquals(getNthContentId(result, 0), room1.id)

        // Add assertions to verify the response content if needed
        println(result)
    }

    @Test
    fun `should search rooms by address`() {
        val address = Address("Seoul", "ad", "ad", "ad")
        val room1 = dataGenerator.generateRoom(name = "Room1")
        val room2 = dataGenerator.generateRoom(name = "Room2", address = address)
        val room3 = dataGenerator.generateRoom(name = "Room3")

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/search")
                .param("sido", "Seoul")
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString

        // check result length
        Assertions.assertEquals(getContentLength(result), 1)

        // check if all rooms are in the result
        Assertions.assertEquals(getNthContentId(result, 0), room2.id)

        // Add assertions to verify the response content if needed
        println(result)
    }

    @Test
    fun `should search rooms by multiple conditions`() {
        val address1 = Address(sido = "sido1", sigungu = "sigungu1", street = "street1", detail = "detail1")
        val room1 = dataGenerator.generateRoom(name = "Room1", type = RoomType.APARTMENT, address = address1)

        val address2 = Address(sido = "sido1", sigungu = "sigungu1", street = "street2", detail = "detail2")
        val room2 = dataGenerator.generateRoom(name = "Room2", type = RoomType.VILLA, address = address2)

        val address3 = Address(sido = "sido1", sigungu = "sigungu1", street = "street2", detail = "detail3")
        val room3 = dataGenerator.generateRoom(name = "Room3", type = RoomType.HOTEL, address = address3)

        val address4 = Address(sido = "sido2", sigungu = "sigungu2", street = "street1", detail = "detail4")
        val room4 = dataGenerator.generateRoom(name = "Room4", type = RoomType.APARTMENT, address = address4)

        val address5 = Address(sido = "sido2", sigungu = "sigungu2", street = "street2", detail = "detail5")
        val room5 = dataGenerator.generateRoom(name = "Room5", type = RoomType.VILLA, address = address5)

        val address6 = Address(sido = "sido2", sigungu = "sigungu2", street = "street3", detail = "detail6")
        val room6 = dataGenerator.generateRoom(name = "Room6", type = RoomType.HOTEL, address = address6)

        val address7 = Address(sido = "sido3", sigungu = "sigungu3", street = "street1", detail = "detail7")
        val roomDetails7 = RoomDetails(wifi = true, selfCheckin = false, luggage = false, tv = true, bedroom = 1, bathroom = 2, bed = 2)
        dataGenerator.generateRoom(name = "Room7", type = RoomType.APARTMENT, address = address7, roomDetails = roomDetails7)

        val address8 = Address(sido = "sido3", sigungu = "sigungu3", street = "street2", detail = "detail8")
        val room8 = dataGenerator.generateRoom(name = "Room8", type = RoomType.VILLA, address = address8)

        val address9 = Address(sido = "sido3", sigungu = "sigungu3", street = "street3", detail = "detail9")
        dataGenerator.generateRoom(name = "Room9", type = RoomType.HOTEL, address = address9)

        val address10 = Address(sido = "sido4", sigungu = "sigungu4", street = "street1", detail = "detail10")
        dataGenerator.generateRoom(name = "Room10", type = RoomType.APARTMENT, address = address10)

        val address11 = Address(sido = "sido4", sigungu = "sigungu4", street = "street2", detail = "detail11")
        val room11 = dataGenerator.generateRoom(name = "Room11", type = RoomType.VILLA, address = address11)

        val address12 = Address(sido = "sido4", sigungu = "sigungu4", street = "street3", detail = "detail12")
        dataGenerator.generateRoom(name = "Room12", type = RoomType.HOTEL, address = address12)

        // RoomType + Address
        val result1 = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/search")
                .param("roomType", RoomType.APARTMENT.name)
                .param("sigungu", "sigungu1")
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString

        Assertions.assertEquals(getContentLength(result1), 1)
        Assertions.assertEquals(getNthContentId(result1, 0), room1.id)
        println(result1)

        // Address (sido + sigungu), price.perNight,desc sort
        val result2 = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/search")
                .param("sido", "sido2")
                .param("sigungu", "sigungu2")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "price.perNight,desc")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString

        Assertions.assertEquals(getContentLength(result2), 3)

        val contentNode = getContent(result2)
        val prices = contentNode.map { it.path("price").path("perNight").asDouble() }
        for (i in 0 until prices.size - 1) {
            Assertions.assertTrue(prices[i] >= prices[i + 1], "Prices are not sorted in descending order")
        }
        println(result2)

        // RoomType, createdAt,desc sort
        val result3 = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/search")
                .param("roomType", RoomType.VILLA.name)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createdAt,desc")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString

        Assertions.assertEquals(getContentLength(result3), 4)
        Assertions.assertEquals(getNthContentId(result3, 0), room11.id)
        Assertions.assertEquals(getNthContentId(result3, 1), room8.id)
        Assertions.assertEquals(getNthContentId(result3, 2), room5.id)
        Assertions.assertEquals(getNthContentId(result3, 3), room2.id)
        println(result2)

        // RoomDetails, name,desc sort
        val result4 = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/search")
                .param("wifi", "true")
                .param("selfCheckin", "false")
                .param("luggage", "false")
                .param("tv", "true")
                .param("bedroom", "1")
                .param("bathroom", "2")
                .param("bed", "2")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "name,desc")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString

        println(result4)

        // startDate, endDate, id,desc
        val startDate = LocalDate.of(2025, 12, 1)
        val endDate = startDate.plusDays(3)
        val rooms = listOf(room1, room2, room3, room4, room5, room6)

        rooms.forEach { room ->
            dataGenerator.generateReservation(room = room, startDate = startDate, endDate = endDate)
        }

        val result5 = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/search")
                .param("startDate", "2025-12-01")
                .param("endDate", "2026-12-02")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id,desc")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString

        Assertions.assertEquals(getContentLength(result5), 6)

        // startDate
        val startDate2 = LocalDate.of(2026, 1, 1)
        val endDate2 = LocalDate.of(2026, 1, 5)
        val rooms2 = listOf(room1, room2, room3, room4, room5, room6)

        rooms2.forEach { room ->
            dataGenerator.generateReservation(room = room, startDate = startDate2, endDate = endDate2)
        }

        val result6 = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/search")
                .param("startDate", "2026-01-05")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id,desc")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString

        Assertions.assertEquals(getContentLength(result6), 10)

        val result7 = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/search")
                .param("endDate", "2026-01-05")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id,desc")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString

        Assertions.assertEquals(getContentLength(result7), 6)

        // rating, name, desc sort
        val (user8, token8) = dataGenerator.generateUserAndToken()
        val reservation8 = dataGenerator.generateReservation(user = user8, room = room1)
        dataGenerator.generateReview(reservation = reservation8, rating = 4, content = "good")
        room1.ratingStatistics.incrementRating(4)
        roomRepository.save(room1)

        val result8 = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/search")
                .param("rating", "4")
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString

        Assertions.assertEquals(getContentLength(result8), 1)
        val contentNode8 = getContent(result8)
        val ratings = contentNode8.map { it.path("averageRating").asDouble() }
        for (i in 0 until ratings.size - 1) {
            Assertions.assertTrue(ratings[i] >= 4)
        }
        println(result8)
    }

    @BeforeEach
    fun setUp() {
        dataGenerator.clearAll()
    }
}
