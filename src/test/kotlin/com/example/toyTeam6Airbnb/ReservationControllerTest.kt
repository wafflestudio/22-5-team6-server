package com.example.toyTeam6Airbnb

import com.example.toyTeam6Airbnb.reservation.persistence.ReservationRepository
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
        val reservation = dataGenerator.generateReservation(user, room, startDate = LocalDate.of(2023, 12, 1), endDate = LocalDate.of(2023, 12, 10), numberOfGuests = 2)

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/reservations/${reservation.id}")
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
        val reservation = dataGenerator.generateReservation(user, room, startDate = LocalDate.of(2023, 12, 1), endDate = LocalDate.of(2023, 12, 10), numberOfGuests = 2)

        val updateRequestBody = """
            {
                "roomId": ${room.id},
                "startDate": "2023-12-01",
                "endDate": "2024-01-05",
                "numberOfGuests": 2
            }
        """.trimIndent()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/reservations/${reservation.id}")
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
    fun `예약 수정은 본인만 가능하다`() {
        val (user1, token1) = dataGenerator.generateUserAndToken()
        val (user2, token2) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 10)
        val reservation = dataGenerator.generateReservation(user1, room, startDate = LocalDate.of(2023, 12, 1), endDate = LocalDate.of(2023, 12, 10), numberOfGuests = 2)

        // 다른 유저가 예약 수정 시 403 반환
        val updateRequestBody = """
            {
                "roomId": ${room.id},
                "startDate": "2023-12-01",
                "endDate": "2024-01-05",
                "numberOfGuests": 2
            }
        """.trimIndent()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/reservations/${reservation.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestBody)
                .header("Authorization", "Bearer $token2")
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden) // expect 403 status
            .andReturn()

        val responseContent = result.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)
    }

    @Test
    fun `예약을 삭제한 경우 204 응답을 반환한다 + 본인만 삭제 가능`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val (user2, token2) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 10)
        val reservation = dataGenerator.generateReservation(user, room, startDate = LocalDate.of(2023, 12, 1), endDate = LocalDate.of(2023, 12, 10), numberOfGuests = 2)

        val result = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/reservations/${reservation.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent) // expect 204 status
            .andReturn()
        println(result.response.contentAsString)

        val reservation2 = dataGenerator.generateReservation(user, room, startDate = LocalDate.of(2023, 12, 1), endDate = LocalDate.of(2023, 12, 10), numberOfGuests = 2)
        val reservationId2 = reservation2.id

        // 다른 유저(user2)가 삭제시 403 반환
        val result2 = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/reservations/$reservationId2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $token2")
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden) // expect 403 status
            .andReturn()
        println(result2.response.contentAsString)
    }

    @Test
    fun `예약에대한 Availablity를 200을 반환한다`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 10)
        val roomId = room.id
        val reservation1 = dataGenerator.generateReservation(user, room, startDate = LocalDate.of(2025, 1, 5), endDate = LocalDate.of(2025, 1, 10), numberOfGuests = 2)
        val reservation2 = dataGenerator.generateReservation(user, room, startDate = LocalDate.of(2025, 1, 15), endDate = LocalDate.of(2025, 1, 20), numberOfGuests = 2)

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

    // 예약 생성을 2개할때, 중복이 되면 exception 처리가 되는지 확인하는 테스트코드를 작성
    @Test
    fun `예약 생성시 기간이 겹치면 409 응답을 반환한다`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 10)
        val roomId = room.id

        // 1. 날짜가 아예 똑같은 2개
        val reservation1 = dataGenerator.generateReservation(user, room, startDate = LocalDate.of(2025, 1, 5), endDate = LocalDate.of(2025, 1, 10), numberOfGuests = 2)
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
            .andExpect(MockMvcResultMatchers.status().isConflict) // expect 409 status
            .andReturn()

        // 2. 날짜가 포함되도록 새로운 예약을 만들때
        val reservation2 = dataGenerator.generateReservation(user, room, startDate = LocalDate.of(2025, 5, 10), endDate = LocalDate.of(2025, 5, 15), numberOfGuests = 2)
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "roomId": $roomId,
                        "startDate": "2025-05-01",
                        "endDate": "2025-05-20",
                        "numberOfGuests": 2
                    }
                    """.trimIndent()
                )
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isConflict) // expect 409 status
            .andReturn()

        val responseContent = result.response.contentAsString
        // Add assertions to verify the response content if needed
        println(responseContent)
    }

    @Test
    fun `예약 생성시 인원이 0명일 경우 400 응답을 반환한다`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 10)
        val roomId = room.id
        val requestBody = """
        {
            "roomId": $roomId,
            "startDate": "2023-12-01",
            "endDate": "2023-12-10",
            "numberOfGuests": 0
        }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest) // expect 400 status
            .andReturn()
    }

    @Test
    fun `should get reservations by user`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom()
        val reservation1 = dataGenerator.generateReservation(user, room)
        val reservation2 = dataGenerator.generateReservation(user, room)

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/reservations/user/${user.id}?page=0&size=3")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .contentAsString

        println(result)
        // check result length
        Assertions.assertEquals(getContentLength(result), 2)

        // check if all reviews are in the result
        Assertions.assertEquals(getNthContentId(result, 0), reservation1.id)
        Assertions.assertEquals(getNthContentId(result, 1), reservation2.id)

        // another user should not be able to see the reservations
        val (user2, token2) = dataGenerator.generateUserAndToken()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/reservations/user/${user.id}?page=0&size=3")
                .header("Authorization", "Bearer $token2")
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
            .andReturn()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/reservations/user/${user.id}?page=0&size=3")
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
            .andReturn()

        // update user1's profile
        val updateRequestBody = """
            {
                "nickname": "newNickname",
                "bio": "newBio",
                "showMyReviews": true,
                "showMyReservations": true
            }
        """.trimIndent()
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestBody)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        // user2 should be able to see the reservations now
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/reservations/user/${user.id}?page=0&size=3")
                .header("Authorization", "Bearer $token2")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/reservations/user/${user.id}?page=0&size=3")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
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

    @BeforeEach
    fun setUp() {
        dataGenerator.clearAll()
    }
}
