package com.example.ict311_workoutapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class WorkListViewModel : ViewModel() {
    private val workRepository: WorkRepository = WorkRepository.get()
    val worksListLiveData: LiveData<List<WorkEntry>> = workRepository.getEntries()
}