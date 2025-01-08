package com.example.toyTeam6Airbnb

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest
@Autowired
constructor(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper
) {

    @Test
    fun `should register a new user`() {
        val requestBody = """
            {
                "username": "testuser",
                "password": "password123"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andReturn()
    }

    @Test
    fun `should authenticate a user`() {
        val result = mockMvc.perform(
            post("/api/auth/login")
                .content(
                    "username=newuser&password=password123"
                )
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isFound) // Expecting 302 status
            .andReturn()

        val redirectedUrl = result.response.getHeader("Location")
        val token = result.response.getHeader("Authorization")
        println("---------------------------------------------------------------------")
        println("Redirected to: $redirectedUrl")
        println("JWT Header: $token")
        println("---------------------------------------------------------------------")

        // Follow the redirect
        val finalResult = mockMvc.perform(
            get(redirectedUrl!!)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()
    }
}
