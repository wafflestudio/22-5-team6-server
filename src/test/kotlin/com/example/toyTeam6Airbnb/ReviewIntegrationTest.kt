package com.example.toyTeam6Airbnb

import com.example.toyTeam6Airbnb.review.persistence.ReviewRepository
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import kotlin.test.assertEquals

// Review CRUD 테스트에 대한 테스트코드를 작성해줘
// DataGenerator 사용해
// ReviewRepository, UserRepository, RoomRepository를 사용해서 작성해줘
@SpringBootTest
@AutoConfigureMockMvc
class ReviewIntegrationTest {

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
    fun `should create a review`() {
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

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", "Bearer $token")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn()

        val reviews = reviewRepository.findAll()
        assertEquals(1, reviews.size)
        assertEquals("Great place!", reviews[0].content)
    }

    @Test
    fun `should update a review`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom()
        val reservation = dataGenerator.generateReservation(user, room)
        val review = dataGenerator.generateReview(reservation, "Great place!", 5)

        val requestBody = """
        {
            "content": "Good place!",
            "rating": 4
        }
        """.trimIndent()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/reviews/${review.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val updatedReview = review.id?.let { reviewRepository.findById(it).get() }
        assertEquals(4, updatedReview?.rating)
        assertEquals("Good place!", updatedReview?.content)
    }

    @Test
    fun `should delete a review`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom()
        val reservation = dataGenerator.generateReservation(user, room)
        val review = dataGenerator.generateReview(reservation)

        val result = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/reviews/${review.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andReturn()

        val reviews = reviewRepository.findAll()
        assertEquals(0, reviews.size)
    }

    @Test
    fun `should get a review`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom()
        val reservation = dataGenerator.generateReservation(user, room)
        val review = dataGenerator.generateReview(reservation)

        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/reviews/${review.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val responseContent = result.response.contentAsString
        println(responseContent)
    }
}
