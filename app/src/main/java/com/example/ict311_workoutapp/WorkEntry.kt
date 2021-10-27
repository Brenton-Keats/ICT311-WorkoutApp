package com.example.ict311_workoutapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@Entity
data class WorkEntry (
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: LocalDate = LocalDate.ofEpochDay(0),
    var place: String = "",
    var startTime: LocalTime = LocalTime.now(),
    var endTime: LocalTime = LocalTime.now(),
    var isGroup: Boolean = false
)