package com.example.ict311_workoutapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

private const val TAG = "EntryDetailViewModel"

class EntryDetailViewModel : ViewModel() {

    private val workRepository = WorkRepository.get()
    private val workIDLiveData = MutableLiveData<UUID>()

    // Fetch the live data for the id in val workIDLiveData
    var workLiveData: LiveData<WorkEntry?> =
        Transformations.switchMap(workIDLiveData) {
            entryID -> workRepository.getEntry(entryID)
        }

    fun createEntry(entry: WorkEntry) {
        Log.d(TAG, "createEntry: called")
        workRepository.addEntry(entry)
    }

    // Save the data for the entry
    fun saveEntry(entry: WorkEntry) {
        Log.d(TAG, "saveEntry: called")
        workRepository.updateEntry(entry)
    }

    // Update the id in val workIDLiveData to fetch the data
    fun loadEntry(id: UUID) {
        workIDLiveData.value = id
    }

    fun removeEntry(id: UUID) {
        workRepository.removeEntry(id)
    }
}