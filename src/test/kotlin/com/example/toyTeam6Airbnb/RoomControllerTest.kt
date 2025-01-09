package com.example.toyTeam6Airbnb

import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
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
    fun `should get all rooms from rooms main`() {
        val room1 = dataGenerator.generateRoom()
        val room2 = dataGenerator.generateRoom()
        val room3 = dataGenerator.generateRoom()
        val room4 = dataGenerator.generateRoom()
        val room5 = dataGenerator.generateRoom()

        val requstBody = """
            {
              "page": 0,
              "size": 10,
              "sort": [
                "string"
              ]
            }
        """.trimIndent()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString

        // check result length
        Assertions.assertEquals(getContentLength(result), 5)

        // check if all rooms are in the result
        Assertions.assertEquals(getNthContentId(result, 0), room1.id)
        Assertions.assertEquals(getNthContentId(result, 1), room2.id)
        Assertions.assertEquals(getNthContentId(result, 2), room3.id)
        Assertions.assertEquals(getNthContentId(result, 3), room4.id)
        Assertions.assertEquals(getNthContentId(result, 4), room5.id)

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
            MockMvcRequestBuilders.get("/api/v1/rooms/main/search?name=Room1")
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

    @BeforeEach
    fun setUp() {
        dataGenerator.clearAll()
    }
}
