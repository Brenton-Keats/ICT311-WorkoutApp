package com.example.ict311_workoutapp.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class WorkTypeConverters {
    @TypeConverter
    fun fromDate(date: LocalDate?) : Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun toDate(daysSinceEpoch: Long) : LocalDate {
        return LocalDate.ofEpochDay(daysSinceEpoch)
    }

    @TypeConverter
    fun fromTime(time: LocalTime?) : Int? {
        return time?.toSecondOfDay()
    }

    @TypeConverter
    fun toTime(secondOfDay: Int) : LocalTime {
        return LocalTime.ofSecondOfDay(secondOfDay.toLong())
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?) : String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String) : UUID {
        return UUID.fromString(uuid)
    }
}