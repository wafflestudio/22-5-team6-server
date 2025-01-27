package com.example.toyTeam6Airbnb

import com.example.toyTeam6Airbnb.review.persistence.ReviewRepository
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
class ReviewRatingTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var reviewRepository: ReviewRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var roomRepository: RoomRepository

    @Autowired
    private lateinit var dataGenerator: DataGenerator

    @BeforeEach
    fun setUp() {
        dataGenerator.clearAll()
    }

    @Test
    fun `should return average rating 0 when no review exists`() {
        // given
        val room = dataGenerator.generateRoom(maxOccupancy = 100)

        // when
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/${room.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        // then
        val objectMapper = ObjectMapper()
        val responseJson: JsonNode = objectMapper.readTree(response.response.contentAsString)
        assertEquals(0.0, responseJson["averageRating"].asDouble())
    }

    @Test
    fun `should return correct average rating after review creation`() {
        // given
        val (user, token) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 100)
        val reservation = dataGenerator.generateReservation(user, room)

        val requestBody = """
        {
            "roomId": ${room.id},
            "reservationId": ${reservation.id},
            "content": "Great place!",
            "rating": 5
        }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/reviews")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isCreated)

        // when
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/${room.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        // then
        val objectMapper = ObjectMapper()
        val responseJson: JsonNode = objectMapper.readTree(response.response.contentAsString)
        assertEquals(5.0, responseJson["averageRating"].asDouble())
    }

    @Test
    fun `should return correct average rating after multiple creation, update, deletion`() {
        // given
        val (user, token) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 100)
        val reservation = dataGenerator.generateReservation(user, room)

        val requestBody = """
        {
            "roomId": ${room.id},
            "reservationId": ${reservation.id},
            "content": "Great place!",
            "rating": 5
        }
        """.trimIndent()

        var response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/reviews")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn()

        // get review id
        var objectMapper = ObjectMapper()
        var responseJson: JsonNode = objectMapper.readTree(response.response.contentAsString)
        val reviewId = responseJson["reviewId"].asLong()

        // when
        response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/${room.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        // then
        responseJson = objectMapper.readTree(response.response.contentAsString)
        assertEquals(5.0, responseJson["averageRating"].asDouble())

        // given
        val requestBody2 = """
        {
            "content": "Good place!",
            "rating": 4
        }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/reviews/$reviewId")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody2)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)

        // when
        response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/${room.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        // then
        objectMapper = ObjectMapper()
        responseJson = objectMapper.readTree(response.response.contentAsString)
        assertEquals(4.0, responseJson["averageRating"].asDouble())

        // given
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/reviews/$reviewId")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody2)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNoContent)

        // when
        response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/${room.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        // then
        objectMapper = ObjectMapper()
        responseJson = objectMapper.readTree(response.response.contentAsString)
        assertEquals(0.0, responseJson["averageRating"].asDouble())
    }

    @Test
    fun `should return correct average rating after concurrent review creation`() {
        // given
        val (user1, token1) = dataGenerator.generateUserAndToken()
        val (user2, token2) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 100)
        val reservation1 = dataGenerator.generateReservation(user1, room, startDate = LocalDate.of(2026, 11, 20), endDate = LocalDate.of(2026, 11, 25))
        val reservation2 = dataGenerator.generateReservation(user2, room, startDate = LocalDate.of(2026, 11, 25), endDate = LocalDate.of(2026, 11, 30))

        val requestBody1 = """
        {
            "roomId": ${room.id},
            "reservationId": ${reservation1.id},
            "content": "Great place!",
            "rating": 5
        }
        """.trimIndent()

        val requestBody2 = """
        {
            "roomId": ${room.id},
            "reservationId": ${reservation2.id},
            "content": "Good place!",
            "rating": 4
        }
        """.trimIndent()

        val latch = CountDownLatch(2)
        val executor = Executors.newFixedThreadPool(2)

        val results = mutableListOf<Int>()

        executor.submit {
            try {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody1)
                        .header("Authorization", "Bearer $token1")
                )
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn()
                results.add(result.response.status)
            } finally {
                latch.countDown()
            }
        }

        executor.submit {
            try {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2)
                        .header("Authorization", "Bearer $token2")
                )
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn()
                results.add(result.response.status)
            } finally {
                latch.countDown()
            }
        }

        latch.await()

        assertEquals(2, results.count { it == HttpStatus.CREATED.value() })

        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/rooms/main/${room.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        // then
        val objectMapper = ObjectMapper()
        val responseJson: JsonNode = objectMapper.readTree(response.response.contentAsString)
        assertEquals(4.5, responseJson["averageRating"].asDouble())
    }
}
