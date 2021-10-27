package com.example.ict311_workoutapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ict311_workoutapp.WorkEntry


@Database(entities = [ WorkEntry::class ], version=1)
@TypeConverters(WorkTypeConverters::class)

abstract class WorkDatabase : RoomDatabase() {
    abstract fun workDao(): WorkDao
}