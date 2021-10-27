package com.example.ict311_workoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), WorkListFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called.")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = WorkListFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onEntrySelected(entryID: UUID?) {
        Log.d(TAG, "onEntrySelected: called. Selecting entry: $entryID")
        val fragment = EntryFragment.newInstance(entryID)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}