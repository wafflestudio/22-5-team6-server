package com.example.toyTeam6Airbnb.room.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface RoomRepository : JpaRepository<RoomEntity, String> {

}
