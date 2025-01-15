package com.example.toyTeam6Airbnb

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
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
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserControllerTest
@Autowired
constructor(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper
) {

    @Test
    @Order(1)
    fun `should register a new user`() {
        val requestBody = """
            {
                "username": "testuser",
                "password": "password123",
                "nickname": "testuser",
                "bio": "Hello, I'm a test user!",
                "showMyReviews": true,
                "showMyReservations": true
            }
        """.trimIndent()

        val result = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val response = result.response
        // 상태 코드
        println("Response Status: ${response.status}")

        // 모든 헤더 출력
        println("Response Headers:")
        response.headerNames.forEach { headerName ->
            println("$headerName: ${response.getHeader(headerName)}")
        }

        // 응답 본문
        println("Response Body: ${response.contentAsString}")

        // 요약 정보
        println("Response Summary: $response")
    }

    @Test
    @Order(2)
    fun `should authenticate a user`() {
        val requestBody = "username=testuser&password=password123"

        val result = mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(requestBody)
                .accept(MediaType.ALL)
        )
            .andExpect(status().isFound) // expect 200 status
            .andReturn()

        val response = result.response

        // 상태 코드
        println("Response Status: ${response.status}")

        // 모든 헤더 출력
        println("Response Headers:")
        response.headerNames.forEach { headerName ->
            println("$headerName: ${response.getHeader(headerName)}")
        }

        // 응답 본문
        println("Response Body: ${response.contentAsString}")

        // 요약 정보
        println("Response Summary: $response")
    }
}
