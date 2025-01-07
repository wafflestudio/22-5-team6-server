package com.example.toyTeam6Airbnb

import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
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
    private lateinit var roomRepository: RoomRepository

    private val bearerToken =
        "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTczNjAxOTMwMiwiZXhwIjoxNzM2MTA1NzAyfQ.AiF42xPV2DXe5Dn3Tn1ShBuy6miCtZ87NfmwYY8ef71o28d-IhcLdu8SAJbYLRlE4p5qXJ7FqziGwDksNhishg"

    @Test
    fun `should register a new user`() {
        val requestBody = """
            {
                "username": "newuser",
                "password": "password123"
            }
        """.trimIndent()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val responseContent = result.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)
    }

    @Test
    fun `should authenticate a user`() {
        val requestBody = "username=newuser&password=password123"

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isFound) // Expecting 302 status
            .andReturn()

        val redirectedUrl = result.response.getHeader("Location")
        println("Redirected to: $redirectedUrl")

        // Follow the redirect
        val finalResult = mockMvc.perform(
            MockMvcRequestBuilders.get(redirectedUrl!!)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val responseContent = finalResult.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)
    }

    @Test
    fun `should create a new room`() {
        val requestBody = """
        {
            "name": "Sample Room",
            "description": "A nice place to stay",
            "type": "Apartment",
            "address": "123 Main St, Anytown, USA",
            "price": 100.0,
            "maxOccupancy": 4
        }
        """.trimIndent()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", bearerToken)
        )
            .andExpect(MockMvcResultMatchers.status().isFound) // Expecting 302 status
            .andReturn()

        val redirectedUrl = result.response.getHeader("Location")
        println("Redirected to: $redirectedUrl")

        // Follow the redirect
        val finalResult = mockMvc.perform(
            MockMvcRequestBuilders.get(redirectedUrl!!)
                .header("Authorization", bearerToken)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn()

        val responseContent = finalResult.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)
    }

    @Test
    fun `should get a specific room`() {
        val roomId = 1L // Assume this room exists in the database

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/$roomId")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", bearerToken)
        )
            .andExpect(MockMvcResultMatchers.status().isFound) // Expecting 302 status
            .andReturn()

        val redirectedUrl = result.response.getHeader("Location")
        println("Redirected to: $redirectedUrl")

        // Follow the redirect
        val finalResult = mockMvc.perform(
            MockMvcRequestBuilders.get(redirectedUrl!!)
                .header("Authorization", bearerToken)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val responseContent = finalResult.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)
    }
}
