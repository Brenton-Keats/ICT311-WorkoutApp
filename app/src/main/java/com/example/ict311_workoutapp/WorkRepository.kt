package com.example.ict311_workoutapp

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.ict311_workoutapp.database.WorkDatabase
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "work-database"
private const val TAG = "WorkRepository"

class WorkRepository private constructor(context: Context) {

    private val database: WorkDatabase = Room.databaseBuilder(
        context.applicationContext,
        WorkDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val workDao = database.workDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getEntries(): LiveData<List<WorkEntry>> {
        Log.d(TAG, "getEntries called.")
        return workDao.getEntries()
    }

    fun getEntry(id: UUID): LiveData<WorkEntry?> {
        Log.d(TAG, "getEntry called.")
        return workDao.getEntry(id)
    }

    fun updateEntry(entry: WorkEntry) {
        Log.d(TAG, "updateEntry called.")
        executor.execute {
            workDao.updateEntry(entry)
        }
    }

    fun addEntry(entry: WorkEntry) {
        Log.d(TAG, "addEntry called.")
        executor.execute {
            workDao.addEntry(entry)
        }
    }

    fun removeEntry(id: UUID) {
        Log.d(TAG, "deleteEntry: called")
        executor.execute {
            workDao.removeEntry(id)
        }
    }

    companion object {
        private var INSTANCE: WorkRepository? = null

        fun initialize(context: Context) {
            Log.d(TAG, "companion initialize called.")
            if (INSTANCE == null) {
                INSTANCE = WorkRepository(context)
            }
        }

        fun get(): WorkRepository {
            Log.d(TAG, "companion get called.")
            return INSTANCE ?: throw IllegalStateException("WorkRepository must be initialized.")
        }
    }
}