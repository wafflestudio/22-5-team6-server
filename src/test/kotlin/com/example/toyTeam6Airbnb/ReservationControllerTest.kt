package com.example.toyTeam6Airbnb

import com.example.toyTeam6Airbnb.reservation.persistence.ReservationRepository
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
class ReservationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var reservationRepository: ReservationRepository

    @Test
    fun `should create a new reservation`() {
        val requestBody = """
            {
                "roomId": 1,
                "startDate": "2023-12-01",
                "endDate": "2023-12-10"
            }
        """.trimIndent()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/reservations")
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
    fun `should get a specific reservation`() {
        val reservationId = 1L // Assume this reservation exists in the database

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/reservations/$reservationId")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val responseContent = result.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)
    }
}