package com.example.toyTeam6Airbnb

import com.example.toyTeam6Airbnb.room.persistence.Address
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
            "name": "Sample Room",
            "description": "A nice place to stay",
            "type": "APARTMENT",
            "address": {
                "sido": "Seoul",
                "sigungu": "Gangnam",
                "street": "Gangnam Daero",
                "detail": "12345"
            },
            "roomDetails": {
                "wifi": true,
                "selfCheckin": true,
                "luggage": true,
                "TV": true
            },
            "price": 10000,
            "maxOccupancy": 4
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
                "name": "Sample Room_2",
                "description": "A nice place to stay",
                "type": "VILLA",
                "address": {
                    "sido": "Seoul",
                    "sigungu": "Gangnam",
                    "street": "Gangnam Daero",
                    "detail": "34567"
                },
                "roomDetails": {
                    "wifi": true,
                    "selfCheckin": true,
                    "luggage": true,
                    "TV": true
                },
                "price": 100000,
                "maxOccupancy": 4
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
        val roomId = responseBody.split("\"id\":")[1].split(",")[0].toLong()

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
        val room4 = dataGenerator.generateRoom()
        val room5 = dataGenerator.generateRoom()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main?page=0&size=3")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString

        // check result length
        Assertions.assertEquals(getContentLength(result), 3)

        // check if all rooms are in the result
        Assertions.assertEquals(getNthContentId(result, 0), room1.id)
        Assertions.assertEquals(getNthContentId(result, 1), room2.id)
        Assertions.assertEquals(getNthContentId(result, 2), room3.id)

        // Add assertions to verify the response content if needed
        println(result)
    }

    fun getNthContentId(jsonString: String, n: Int): Long? {
        val objectMapper = ObjectMapper()
        val rootNode: JsonNode = objectMapper.readTree(jsonString)
        val contentNode: JsonNode = rootNode.path("content")
        return if (contentNode.isArray && contentNode.size() > n) {
            contentNode[n].path("id").asLong()
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

    @Test
    fun `should be able to update room info, only for owner`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val (user2, token2) = dataGenerator.generateUserAndToken()

        val requestBody = """
            {
                "name": "Sample Room",
                "description": "A nice place to stay",
                "type": "APARTMENT",
                "address": {
                    "sido": "Seoul",
                    "sigungu": "Gangnam",
                    "street": "Gangnam Daero",
                    "detail": "12345"
                },
                "roomDetails": {
                    "wifi": true,
                    "selfCheckin": true,
                    "luggage": true,
                    "TV": true
                },
                "price": 10000,
                "maxOccupancy": 4
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

        val roomId = responseContent.split("\"id\":")[1].split(",")[0].toLong()

        val updateRequestBody = """
            {
                "name": "Sample Room2",
                "description": "Samlpe Description",
                "type": "VILLA",
                "address": {
                    "sido": "Seoul",
                    "sigungu": "Gangnam",
                    "street": "Gangnam Daero",
                    "detail": "12345"
                },
                "roomDetails": {
                    "wifi": true,
                    "selfCheckin": true,
                    "luggage": true,
                    "TV": true
                },
                "price": 10000,
                "maxOccupancy": 4
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
                "name": "Sample Room",
                "description": "A nice place to stay",
                "type": "APARTMENT",
                "address": {
                    "sido": "Seoul",
                    "sigungu": "Gangnam",
                    "street": "Gangnam Daero",
                    "detail": "12345"
                },
                "roomDetails": {
                    "wifi": true,
                    "selfCheckin": true,
                    "luggage": true,
                    "TV": true
                },
                "price": 10000,
                "maxOccupancy": 4
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

        val roomId = responseContent.split("\"id\":")[1].split(",")[0].toLong()

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
        val room3 = dataGenerator.generateRoom(name = "Room3")
        val room4 = dataGenerator.generateRoom(name = "Room4")
        val room5 = dataGenerator.generateRoom(name = "Room5")

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/search")
                .param("name", "Room1")
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
        val room2 = dataGenerator.generateRoom(name = "Room2")
        val room3 = dataGenerator.generateRoom(name = "Room3", address = address)
        val room4 = dataGenerator.generateRoom(name = "Room4")
        val room5 = dataGenerator.generateRoom(name = "Room5")

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
        Assertions.assertEquals(getNthContentId(result, 0), room3.id)

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
        val room7 = dataGenerator.generateRoom(name = "Room7", type = RoomType.APARTMENT, address = address7)

        val address8 = Address(sido = "sido3", sigungu = "sigungu3", street = "street2", detail = "detail8")
        val room8 = dataGenerator.generateRoom(name = "Room8", type = RoomType.VILLA, address = address8)

        val address9 = Address(sido = "sido3", sigungu = "sigungu3", street = "street3", detail = "detail9")
        val room9 = dataGenerator.generateRoom(name = "Room9", type = RoomType.HOTEL, address = address9)

        val address10 = Address(sido = "sido4", sigungu = "sigungu4", street = "street1", detail = "detail10")
        val room10 = dataGenerator.generateRoom(name = "Room10", type = RoomType.APARTMENT, address = address10)

        val address11 = Address(sido = "sido4", sigungu = "sigungu4", street = "street2", detail = "detail11")
        val room11 = dataGenerator.generateRoom(name = "Room11", type = RoomType.VILLA, address = address11)

        val address12 = Address(sido = "sido4", sigungu = "sigungu4", street = "street3", detail = "detail12")
        val room12 = dataGenerator.generateRoom(name = "Room12", type = RoomType.HOTEL, address = address12)

        val result1 = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/search")
                .param("type", RoomType.APARTMENT.name)
                .param("sigungu", "sigungu1")
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString

        // check result length
        Assertions.assertEquals(getContentLength(result1), 1)

        // check if all rooms are in the result
        Assertions.assertEquals(getNthContentId(result1, 0), room1.id)

        // Add assertions to verify the response content if needed
        println(result1)

        val result2 = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/search")
                .param("sigungu", "sigungu1")
                .param("street", "street2")
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString

        // check result length
        Assertions.assertEquals(getContentLength(result2), 2)

        // check if all rooms are in the result
        Assertions.assertEquals(getNthContentId(result2, 0), room2.id)
        Assertions.assertEquals(getNthContentId(result2, 1), room3.id)

        // Add assertions to verify the response content if needed
        println(result2)
    }

    @BeforeEach
    fun setUp() {
        dataGenerator.clearAll()
    }
}
