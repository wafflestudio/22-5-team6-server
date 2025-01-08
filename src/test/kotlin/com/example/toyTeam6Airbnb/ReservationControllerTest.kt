package com.example.toyTeam6Airbnb

import com.example.toyTeam6Airbnb.reservation.persistence.ReservationRepository
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ReservationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var reservationRepository: ReservationRepository


    @Test
    @Order(1)
    fun `should register a new user`() {
        val requestBody = """
            {
                "username": "testuser",
                "password": "password123"
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
        println("Response Summary: ${response.toString()}")
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
            .andExpect(status().isOk) // expect 200 status
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
        println("Response Summary: ${response.toString()}")
    }

    @Test
    @Order(3)
    fun `should create a new reservation`() {
        val requestBody = """
            {
                "roomId": 1,
                "startDate": "2023-12-01",
                "endDate": "2023-12-10",
                "numberOfGuests": 2
            }
        """.trimIndent()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(MockMvcResultMatchers.status().isOk) // expect 200 status
            .andReturn()

        val responseContent = result.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)
    }

    @Test
    @Order(4)
    fun `should get a specific reservation`() {
        val reservationId = 1L // Assume this reservation exists in the database

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/reservations/$reservationId")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk) // expect 200 status
            .andReturn()

        val responseContent = result.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)
    }

    @Test
    @Order(5)
    fun `예약 수정하였을때 응답으로 200이 나와야한다`(){
        val reservationId = 1L // Assume this reservation exists in the database
        val requestBody = """
            {
                "roomId": 1,
                "startDate": "2023-12-01",
                "endDate": "2024-01-05",
                "numberOfGuests": 2
            }
        """.trimIndent()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/reservations/$reservationId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(MockMvcResultMatchers.status().isOk) // expect 200 status
            .andReturn()

        val responseContent = result.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)
    }

    @Test


}
