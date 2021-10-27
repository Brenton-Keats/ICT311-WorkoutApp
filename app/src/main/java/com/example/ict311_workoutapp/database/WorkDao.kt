package com.example.ict311_workoutapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.*
import java.util.UUID

import com.example.ict311_workoutapp.WorkEntry;

@Dao
interface WorkDao {
    @Query("SELECT * FROM WorkEntry ORDER BY date DESC")
    fun getEntries(): LiveData<List<WorkEntry>>

    @Query("SELECT * FROM WorkEntry WHERE id=(:id)")
    fun getEntry(id: UUID): LiveData<WorkEntry?>

    @Update
    fun updateEntry(entry: WorkEntry)

    @Insert
    fun addEntry(entry: WorkEntry)

    @Query("DELETE FROM WorkEntry WHERE id=(:id)")
    fun removeEntry(id: UUID)
}
