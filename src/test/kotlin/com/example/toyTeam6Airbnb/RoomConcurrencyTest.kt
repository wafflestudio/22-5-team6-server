package com.example.toyTeam6Airbnb

import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
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
    fun `should throw DuplicateRoomException when two identical requests are made concurrently`() {
        val (user, token) = dataGenerator.generateUserAndToken()

        val latch = CountDownLatch(2)
        val executor = Executors.newFixedThreadPool(2)

        val requestBody = """
        {
            "roomName": "Cozy Apartment in Seoul",
            "description": "A beautiful and cozy apartment located in the heart of Seoul. Perfect for travelers!",
            "roomType": "APARTMENT",
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
                "tv": true,
                "bedroom": 1,
                "bathroom": 1,
                "bed": 1
            },
            "price": {
                "perNight": 5000,
                "cleaningFee": 5000,
                "charge": 5000
            },
            "maxOccupancy": 1
        }
        """.trimIndent()

        val results = mutableListOf<Int>()

        executor.submit {
            try {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer $token")
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
                    MockMvcRequestBuilders.post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer $token")
                )
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn()
                results.add(result.response.status)
            } finally {
                latch.countDown()
            }
        }

        latch.await()

        assertEquals(1, results.count { it == HttpStatus.CREATED.value() })
        assertEquals(1, results.count { it == HttpStatus.CONFLICT.value() })

        val rooms = roomRepository.findAll()
        assertEquals(1, rooms.size)
    }

    @Test
    fun `should throw DuplicateRoomException when identical requests are made by different users concurrently`() {
        val (user1, token1) = dataGenerator.generateUserAndToken()
        val (user2, token2) = dataGenerator.generateUserAndToken()

        val latch = CountDownLatch(2)
        val executor = Executors.newFixedThreadPool(2)

        val requestBody = """
        {
            "roomName": "Cozy Apartment in Seoul",
            "description": "A beautiful and cozy apartment located in the heart of Seoul. Perfect for travelers!",
            "roomType": "APARTMENT",
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
                "tv": true,
                "bedroom": 1,
                "bathroom": 1,
                "bed": 1
            },
            "price": {
                "perNight": 50000,
                "cleaningFee": 20000,
                "charge": 5000
            },
            "maxOccupancy": 4
        }
        """.trimIndent()

        val results = mutableListOf<Int>()

        executor.submit {
            try {
                val result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
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
                    MockMvcRequestBuilders.post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
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

        assertEquals(1, results.count { it == HttpStatus.CREATED.value() })
        assertEquals(1, results.count { it == HttpStatus.CONFLICT.value() })

        val rooms = roomRepository.findAll()
        assertEquals(1, rooms.size)
    }
}