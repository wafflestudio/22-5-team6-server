package com.example.toyTeam6Airbnb

import com.example.toyTeam6Airbnb.reservation.persistence.ReservationRepository
import org.json.JSONObject
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
class ReservationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var dataGenerator: DataGenerator

    @Autowired
    private lateinit var reservationRepository: ReservationRepository

    @Test
    fun `새로운 예약 생성시 201 응답 반환`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 10)
        val roomId = room.id
        val requestBody = """
            {
                "roomId": $roomId,
                "startDate": "2023-12-01",
                "endDate": "2023-12-10",
                "numberOfGuests": 2
            }
        """.trimIndent()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated) // expect 201 status
            .andReturn()

        val responseContent = result.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)
    }

    @Test
    fun `특정 ID의 예약 조회 성공시 200 응답 반환`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 10)
        val roomId = room.id
        val requestBody = """
            {
                "roomId": $roomId,
                "startDate": "2023-12-01",
                "endDate": "2023-12-10",
                "numberOfGuests": 2
            }
        """.trimIndent()

        val reservation = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated) // expect 201 status
            .andReturn()

        val reservationId = JSONObject(reservation.response.contentAsString).getLong("id")

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/reservations/$reservationId")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk) // expect 200 status
            .andReturn()

        val responseContent = result.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)
    }

    @Test
    fun `예약 수정하였을때 응답으로 200이 나와야한다`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 10)
        val roomId = room.id
        val requestBody = """
            {
                "roomId": $roomId,
                "startDate": "2023-12-01",
                "endDate": "2023-12-10",
                "numberOfGuests": 2
            }
        """.trimIndent()

        val reservation = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated) // expect 201 status
            .andReturn()

        val reservationId = JSONObject(reservation.response.contentAsString).getLong("id")

        val updateRequestBody = """
            {
                "roomId": $roomId,
                "startDate": "2023-12-01",
                "endDate": "2024-01-05",
                "numberOfGuests": 2
            }
        """.trimIndent()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/reservations/$reservationId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestBody)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk) // expect 200 status
            .andReturn()

        val responseContent = result.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)
    }

    @Test
    fun `예약을 삭제한 경우 204 응답을 반환한다`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 10)
        val roomId = room.id
        val requestBody = """
            {
                "roomId": $roomId,
                "startDate": "2023-12-01",
                "endDate": "2023-12-10",
                "numberOfGuests": 2
            }
        """.trimIndent()

        val reservation = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated) // expect 201 status
            .andReturn()

        val reservationId = JSONObject(reservation.response.contentAsString).getLong("id")

        val result = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/reservations/$reservationId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent) // expect 204 status
            .andReturn()
    }

    @Test
    fun `전체 예약 조회시 200 응답을 반환한다`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 10)
        val roomId = room.id
        val requestBody = """
            {
                "roomId": $roomId,
                "startDate": "2023-12-01",
                "endDate": "2023-12-10",
                "numberOfGuests": 2
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated) // expect 201 status
            .andReturn()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "roomId": $roomId,
                        "startDate": "2024-12-01",
                        "endDate": "2024-12-10",
                        "numberOfGuests": 2
                    }
                    """.trimIndent()
                )
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated) // expect 201 status
            .andReturn()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/reservations")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk) // expect 200 status
            .andReturn()

        val responseContent = result.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)
    }

    @Test
    fun `예약에대한 Availablity를 200을 반환한다`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 10)
        val roomId = room.id

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
            {
                "roomId": $roomId,
                "startDate": "2025-01-05",
                "endDate": "2025-01-10",
                "numberOfGuests": 2
            }
                    """.trimIndent()
                )
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated) // expect 201 status
            .andReturn()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "roomId": $roomId,
                        "startDate": "2025-01-15",
                        "endDate": "2025-01-20",
                        "numberOfGuests": 2
                    }
                    """.trimIndent()
                )
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated) // expect 201 status
            .andReturn()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/reservations/availability/$roomId")
                .param("year", "2025")
                .param("month", "1")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk) // expect 200 status
            .andReturn()

        val responseContent = result.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)
    }

    @BeforeEach
    fun setUp() {
        dataGenerator.clearAll()
    }
}
