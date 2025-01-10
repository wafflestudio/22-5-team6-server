package com.example.toyTeam6Airbnb

import com.example.toyTeam6Airbnb.reservation.persistence.ReservationRepository
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
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
import java.time.LocalDate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
@AutoConfigureMockMvc
class ReservationIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var roomRepository: RoomRepository

    @Autowired
    private lateinit var reservationRepository: ReservationRepository

    @Autowired
    private lateinit var dataGenerator: DataGenerator

    private val logger = KotlinLogging.logger {}

    @BeforeEach
    fun setUp() {
        dataGenerator.clearAll()
    }

    @Test
    fun `should create only one reservation when the same request is made twice`() {
        val (user, token) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 2)

        val startDate = LocalDate.of(2023, 12, 1)
        val endDate = LocalDate.of(2023, 12, 10)

        val latch = CountDownLatch(2)
        val executor = Executors.newFixedThreadPool(2)

        val requestBody = """
            {
                "roomId": ${room.id},
                "startDate": "$startDate",
                "endDate": "$endDate",
                "numberOfGuests": 1
            }
        """.trimIndent()

        executor.submit {
            try {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer $token")
                )
                    .andDo(MockMvcResultHandlers.print()) // 요청/응답 로깅
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn()
            } finally {
                latch.countDown()
            }
        }

        executor.submit {
            try {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer $token")
                )
                    .andDo(MockMvcResultHandlers.print()) // 요청/응답 로깅
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn()
            } finally {
                latch.countDown()
            }
        }

        latch.await()

        val reservations = reservationRepository.findAll()
        assertEquals(1, reservations.size)
    }

    @Test
    fun `should allow only one reservation for the same room and dates`() {
        val (user1, token1) = dataGenerator.generateUserAndToken()
        val (user2, token2) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 2)

        val startDate = LocalDate.of(2023, 12, 1)
        val endDate = LocalDate.of(2023, 12, 10)

        val latch = CountDownLatch(2)
        val executor = Executors.newFixedThreadPool(2)

        val requestBody = """
            {
                "roomId": ${room.id},
                "startDate": "$startDate",
                "endDate": "$endDate",
                "numberOfGuests": 1
            }
        """.trimIndent()

        executor.submit {
            try {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer $token1")
                )
                    .andDo(MockMvcResultHandlers.print()) // 요청/응답 로깅
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn()
            } finally {
                latch.countDown()
            }
        }

        executor.submit {
            try {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer $token2")
                )
                    .andDo(MockMvcResultHandlers.print()) // 요청/응답 로깅
                    .andExpect(MockMvcResultMatchers.status().isBadRequest)
                    .andReturn()
            } finally {
                latch.countDown()
            }
        }

        latch.await()

        val reservations = reservationRepository.findAll()
        assertEquals(1, reservations.size)
    }

    @Test
    fun `should allow two reservations for the same room with non-overlapping dates`() {
        val (user1, token1) = dataGenerator.generateUserAndToken()
        val (user2, token2) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 2)

        val startDate1 = LocalDate.of(2023, 12, 1)
        val endDate1 = LocalDate.of(2023, 12, 5)
        val startDate2 = LocalDate.of(2023, 12, 6)
        val endDate2 = LocalDate.of(2023, 12, 10)

        val latch = CountDownLatch(2)
        val executor = Executors.newFixedThreadPool(2)

        val requestBody1 = """
            {
                "roomId": ${room.id},
                "startDate": "$startDate1",
                "endDate": "$endDate1",
                "numberOfGuests": 1
            }
        """.trimIndent()

        val requestBody2 = """
            {
                "roomId": ${room.id},
                "startDate": "$startDate2",
                "endDate": "$endDate2",
                "numberOfGuests": 1
            }
        """.trimIndent()

        executor.submit {
            try {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody1)
                        .header("Authorization", "Bearer $token1")
                )
                    .andDo(MockMvcResultHandlers.print()) // 요청/응답 로깅
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn()
            } finally {
                latch.countDown()
            }
        }

        executor.submit {
            try {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2)
                        .header("Authorization", "Bearer $token2")
                )
                    .andDo(MockMvcResultHandlers.print()) // 요청/응답 로깅
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn()
            } finally {
                latch.countDown()
            }
        }

        latch.await()

        val reservations = reservationRepository.findAll()
        assertEquals(2, reservations.size)
    }

    // 기간이 5.10 ~ 5.15  5.12 ~ 5.17로 이렇게 겹치는 경우에 대해서도 테스트코드 작성해줘
    // 5.10 ~ 5.15  5.12 ~ 5.17로 겹치는 경우에 대해서는 5.10~5.15 건만 예약 성공해야함
    @Test
    fun `should not allow overlapping reservations`() {
        val (user1, token1) = dataGenerator.generateUserAndToken()
        val (user2, token2) = dataGenerator.generateUserAndToken()
        val room = dataGenerator.generateRoom(maxOccupancy = 2)

        val startDate1 = LocalDate.of(2023, 5, 10)
        val endDate1 = LocalDate.of(2023, 5, 15)
        val startDate2 = LocalDate.of(2023, 5, 12)
        val endDate2 = LocalDate.of(2023, 5, 17)

        val latch = CountDownLatch(2)
        val executor = Executors.newFixedThreadPool(2)

        val requestBody1 = """
        {
            "roomId": ${room.id},
            "startDate": "$startDate1",
            "endDate": "$endDate1",
            "numberOfGuests": 1
        }
    """.trimIndent()

        val requestBody2 = """
        {
            "roomId": ${room.id},
            "startDate": "$startDate2",
            "endDate": "$endDate2",
            "numberOfGuests": 1
        }
    """.trimIndent()

        executor.submit {
            try {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody1)
                        .header("Authorization", "Bearer $token1")
                )
                    .andDo(MockMvcResultHandlers.print()) // 요청/응답 로깅
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn()
            } finally {
                latch.countDown()
            }
        }

        executor.submit {
            try {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2)
                        .header("Authorization", "Bearer $token2")
                )
                    .andDo(MockMvcResultHandlers.print()) // 요청/응답 로깅
                    .andExpect(MockMvcResultMatchers.status().isBadRequest)
                    .andReturn()
            } finally {
                latch.countDown()
            }
        }

        latch.await()

        val reservations = reservationRepository.findAll()
        assertEquals(1, reservations.size)
    }
}
