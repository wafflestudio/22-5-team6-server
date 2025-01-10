package com.example.toyTeam6Airbnb

import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
@AutoConfigureMockMvc
class RoomConcurrencyTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var roomRepository: RoomRepository

    @Autowired
    private lateinit var dataGenerator: DataGenerator

    @BeforeEach
    fun setUp() {
        dataGenerator.clearAll()
    }

    @Test
    fun `동일한 요청이 2개들어오면 방 하나만 만들어야함(멱등성)`() {
        val (user, token) = dataGenerator.generateUserAndToken()

        val latch = CountDownLatch(2)
        val executor = Executors.newFixedThreadPool(2)

        val requestBody = """
        {
            "name": "Cozy Apartment in Seoul",
            "description": "A beautiful and cozy apartment located in the heart of Seoul. Perfect for travelers!",
            "type": "APARTMENT",
            "address": {
                "sido": "Seoul",
                "sigungu": "Jongno-gu",
                "street": "123 Hanok Street",
                "detail": "Apartment 5B"
            },
            "roomDetails": {
                "wifi": true,
                "selfCheckin": false,
                "luggage": false,
                "TV": true
            },
            "price": 75000.0,
            "maxOccupancy": 4
        }
        """.trimIndent()

        executor.submit {
            try {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer $token")
                )
                    .andDo(MockMvcResultHandlers.print()) // Log request/response
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn()
            } finally {
                latch.countDown()
            }
        }

        executor.submit {
            try {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer $token")
                )
                    .andDo(MockMvcResultHandlers.print()) // Log request/response
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn()
            } finally {
                latch.countDown()
            }
        }

        latch.await()

        val rooms = roomRepository.findAll()
        assertEquals(1, rooms.size)
    }

    // requestBody를 위와 같은 형태로 하여 서로 다른 유저가 같은 방을 만드는 동시성 상황에 대한 테스트 케이스 형성
    @Test
    fun `동일한 요청이 서로 다른 유저로부터 들어오면 방이 각각 만들어져야함`() {
        val (user1, token1) = dataGenerator.generateUserAndToken()
        val (user2, token2) = dataGenerator.generateUserAndToken()

        val latch = CountDownLatch(2)
        val executor = Executors.newFixedThreadPool(2)

        val requestBody = """
    {
        "name": "Cozy Apartment in Seoul",
        "description": "A beautiful and cozy apartment located in the heart of Seoul. Perfect for travelers!",
        "type": "APARTMENT",
        "address": {
            "sido": "Seoul",
            "sigungu": "Jongno-gu",
            "street": "123 Hanok Street",
            "detail": "Apartment 5B"
        },
        "roomDetails": {
            "wifi": true,
            "selfCheckin": false,
            "luggage": false,
            "TV": true
        },
        "price": 75000.0,
        "maxOccupancy": 4
    }
        """.trimIndent()

        executor.submit {
            try {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer $token1")
                )
                    .andDo(MockMvcResultHandlers.print()) // Log request/response
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn()
            } finally {
                latch.countDown()
            }
        }

        executor.submit {
            try {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer $token2")
                )
                    .andDo(MockMvcResultHandlers.print()) // Log request/response
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn()
            } finally {
                latch.countDown()
            }
        }

        latch.await()

        val rooms = roomRepository.findAll()
        assertEquals(2, rooms.size)
    }
}
