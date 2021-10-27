package com.example.ict311_workoutapp

import android.app.Application
import android.util.Log

private const val TAG = "WorkoutApplication"

class WorkoutApplication : Application() {
    override fun onCreate() {
        Log.d(TAG, "onCreate called.")
        super.onCreate()
        WorkRepository.initialize(this)
    }
}