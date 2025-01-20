package com.example.toyTeam6Airbnb.user.service

import com.example.toyTeam6Airbnb.room.controller.Room
import com.example.toyTeam6Airbnb.user.controller.RegisterRequest
import com.example.toyTeam6Airbnb.user.controller.User

interface UserService {
    fun register(
        request: RegisterRequest
    ): Pair<User?, String>

    // register 결과로 <User, Url> 페어를 반환하도록 수정
    fun hasProfile(
        username: String
    ): Boolean

    // 사용자가 좋아요 누른 방 리스트 조회
    fun getLikedRooms(
        userId: Long
    ): List<Room>
}
