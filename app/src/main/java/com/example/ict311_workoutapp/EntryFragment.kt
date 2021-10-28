package com.example.ict311_workoutapp

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

private const val TAG = "EntryFragment"
private const val PARAM_ENTRY_ID = "ENTRY_ID"
private const val KEY_TITLE = "KEY_TITLE"
private const val KEY_PLACE = "KEY_PLACE"
private const val KEY_DATE = "KEY_DATE"
private const val KEY_TIME_START = "KEY_TIME_START"
private const val KEY_TIME_END = "KEY_TIME_END"
private const val KEY_IS_GROUP = "KEY_IS_GROUP"

/**
 * A simple [Fragment] subclass.
 * Use the [EntryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EntryFragment : Fragment() {

    private lateinit var mcontext: Context

    private lateinit var entry: WorkEntry
    private lateinit var workingEntry: WorkEntry
    private lateinit var titleField: EditText
    private lateinit var placeField: EditText
    private lateinit var dateField: EditText
    private lateinit var dateButton: Button
    private lateinit var startTimePicker: TimePicker
    private lateinit var endTimePicker: TimePicker
    private lateinit var isGroupSwitch: SwitchCompat
    private lateinit var isGroupLabel: TextView
    private lateinit var calendar: Calendar
    private lateinit var deleteButton: Button

    private val dateFormatter = DateTimeFormatter.ofPattern("E dd MMM yyyy")
    private val inputDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private var isNewEntry: Boolean = false
    private var entryDeleted: Boolean = false
    private val entryDetailViewModel: EntryDetailViewModel by lazy {
        ViewModelProviders.of(this).get(EntryDetailViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        // Save a reference to the context for later (used for displaying Toasts after detach
        this.mcontext = context
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: called")
        super.onCreate(savedInstanceState)

        entry = WorkEntry()
        workingEntry = WorkEntry()
        val entryID = arguments?.getSerializable(PARAM_ENTRY_ID) as UUID?
        if (entryID != null) {
            entryDetailViewModel.loadEntry(entryID)
        } else {
            isNewEntry = true
        }
        calendar = Calendar.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: called")
        val view = inflater.inflate(R.layout.fragment_entry, container, false)
        
        titleField = view.findViewById(R.id.entry_title)
        placeField = view.findViewById(R.id.entry_place)
        dateField = view.findViewById(R.id.entry_date_text)
        dateButton = view.findViewById(R.id.entry_date_button)
        startTimePicker = view.findViewById(R.id.entry_time_start)
        endTimePicker = view.findViewById(R.id.entry_time_end)
        isGroupSwitch = view.findViewById(R.id.isGroup)
        isGroupLabel = view.findViewById(R.id.label_is_group)
        deleteButton = view.findViewById(R.id.entry_delete_button)

        dateField.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                try {
                    dateButton.text =
                        LocalDate.parse(dateField.text, inputDateFormatter).format(dateFormatter)
                } catch (ex: DateTimeParseException) {
                    Toast.makeText(mcontext, R.string.toast_invalid_date, Toast.LENGTH_SHORT).show()
                }
            }
        }

        dateButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext(), {
                    _, year, month, day ->
                    dateButton.text = LocalDate.of(year, month + 1, day).format(dateFormatter)
            },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }

        isGroupSwitch.setOnClickListener {
            isGroupLabel.setText(if (isGroupSwitch.isChecked) R.string.label_entry_group else R.string.label_entry_individual)
        }
''
        deleteButton.setOnClickListener {
            val dialogClickListener = DialogInterface.OnClickListener { _, selected ->
                if (selected == DialogInterface.BUTTON_POSITIVE) {
                        entryDetailViewModel.removeEntry(entry.id)
                        entryDeleted = true
                        Toast.makeText(
                            mcontext,
                            R.string.toast_deleted,
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().onBackPressed()
                    }
                }
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_delete_title)
                .setPositiveButton(R.string.dialog_yes, dialogClickListener)
                .setNegativeButton(R.string.dialog_no, dialogClickListener)
                .create()
                .show()
        }

        return view
    }

    private fun updateUI(entry: WorkEntry) {
        Log.d(TAG, "updateUI: called")
        titleField.setText(entry.title)
        placeField.setText(entry.place)
        if (entry.date == LocalDate.ofEpochDay(0)) {
            dateButton.setText(R.string.button_entry_date)
            dateField.setText("")
        } else {
            dateButton.text = entry.date.format(dateFormatter)
            dateField.setText(entry.date.format(inputDateFormatter))
        }
        startTimePicker.hour = entry.startTime.hour
        startTimePicker.minute = entry.startTime.minute
        endTimePicker.hour = entry.endTime.hour
        endTimePicker.minute = entry.endTime.minute
        isGroupSwitch.isChecked = entry.isGroup
        isGroupLabel.setText(if (isGroupSwitch.isChecked) R.string.label_entry_group else R.string.label_entry_individual)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "onSaveInstanceState: called")
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TITLE, titleField.text.toString())
        outState.putString(KEY_PLACE, placeField.text.toString())
        try {
            outState.putLong(KEY_DATE, LocalDate.parse(dateButton.text, dateFormatter).toEpochDay())
        } catch (ex: DateTimeParseException) {
            outState.putLong(KEY_DATE, 0)
        }
        outState.putInt(KEY_TIME_START, LocalTime.of(startTimePicker.hour, startTimePicker.minute).toSecondOfDay())
        outState.putInt(KEY_TIME_END, LocalTime.of(endTimePicker.hour, endTimePicker.minute).toSecondOfDay())
        outState.putBoolean(KEY_IS_GROUP, isGroupSwitch.isChecked)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: called")
        super.onViewCreated(view, savedInstanceState)

        entryDetailViewModel.workLiveData.observe(
            viewLifecycleOwner,
            { entry ->
                entry?.let {
                    this.entry = entry
                    if (savedInstanceState == null) {
                        updateUI(entry)
                    }
                }
            }
        )

        if (savedInstanceState != null) {
            Log.d(TAG, "onViewCreated: savedInstanceState not null")
            if (!savedInstanceState.isEmpty) {
                Log.d(TAG, "onViewCreated: savedInstanceState not empty")
                workingEntry.title = savedInstanceState.getString(KEY_TITLE).toString()
                workingEntry.place = savedInstanceState.getString(KEY_PLACE).toString()
                workingEntry.date = LocalDate.ofEpochDay(savedInstanceState.getLong(KEY_DATE))
                workingEntry.startTime = LocalTime.ofSecondOfDay(savedInstanceState.getInt(KEY_TIME_START).toLong())
                workingEntry.endTime = LocalTime.ofSecondOfDay(savedInstanceState.getInt(KEY_TIME_END).toLong())
                workingEntry.isGroup = savedInstanceState.getBoolean(KEY_IS_GROUP)
                updateUI(workingEntry)
            }
        } else {
            Log.d(TAG, "onViewCreated: savedInstanceState is null")
            updateUI(entry)
        }
    }

    private fun saveEntry(): Boolean {
        /**
         * Saves the entry if necessary. Returns true if not required, or successfully saved.
         */
        Log.d(TAG, "saveEntry: called")
        if (entryDeleted) {
            return true
        }
        when (validateFieldsAreNewAndSaveable()) {
            true -> {
                Log.d(TAG, "saveEntry: Changes made")

                entry.title = workingEntry.title
                entry.place = workingEntry.place
                entry.date = workingEntry.date
                entry.startTime = workingEntry.startTime
                entry.endTime = workingEntry.endTime
                entry.isGroup = workingEntry.isGroup

                if (activity?.supportFragmentManager?.fragments?.get(0) != this) {
                    if (isNewEntry) {
                        Log.d(TAG, "saveEntry: Saving new entry")
                        entryDetailViewModel.createEntry(entry)
                        Toast.makeText(mcontext, R.string.toast_saved, Toast.LENGTH_SHORT).show()
                    } else {
                        val dialogClickListener = DialogInterface.OnClickListener { _, selected ->
                            Log.d(TAG, "saveEntry: Prompting before updating entry")
                            when (selected) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    Log.d(TAG, "saveEntry: User chose to save")
                                    entryDetailViewModel.saveEntry(entry)
                                    Toast.makeText(
                                        mcontext,
                                        R.string.toast_updated,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else -> {
                                    Log.d(TAG, "saveEntry: User chose not to save")
                                    Toast.makeText(
                                        mcontext,
                                        R.string.toast_no_changes,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        AlertDialog.Builder(requireContext())
                            .setTitle(R.string.dialog_save_entry)
                            .setPositiveButton(R.string.dialog_yes, dialogClickListener)
                            .setNegativeButton(R.string.dialog_no, dialogClickListener)
                            .create()
                            .show()
                    }
                }
                return true
            }
            null -> {
                Log.d(TAG, "saveEntry: No changes were made")
                Toast.makeText(context, R.string.toast_no_changes, Toast.LENGTH_SHORT).show()
                return true
            }
            else -> {
                Toast.makeText(context, R.string.toast_invalid, Toast.LENGTH_SHORT).show()
                return false
            }
        }
    }

    /** Returns false if fields cannot be saved (i.e. empty/invalid data)
      * Returns true if fields can be saved and are new
      * Returns null if fields can be saved, but haven't changed
      */
    private fun validateFieldsAreNewAndSaveable(): Boolean? {
        workingEntry.title = titleField.text.toString()
        workingEntry.place = placeField.text.toString()
        workingEntry.startTime = LocalTime.of(startTimePicker.hour, startTimePicker.minute)
        workingEntry.endTime = LocalTime.of(endTimePicker.hour, endTimePicker.minute)
        workingEntry.isGroup = isGroupSwitch.isChecked
        try {
            workingEntry.date = LocalDate.parse(dateButton.text, dateFormatter)
        } catch (_: DateTimeParseException) {
            Log.d(TAG, "validateFieldsAreNewAndSaveable: Date invalid.")
            workingEntry.date = LocalDate.ofEpochDay(0)
            return false
        }
        if (workingEntry.title == "" || workingEntry.place == "") {
            Log.d(TAG, "validateFieldsAreNewAndSaveable: Empty title or place.")
            return false
        }

        return if (!(entry.title == workingEntry.title
                    && entry.place == workingEntry.place
                    && entry.date == workingEntry.date
                    && entry.startTime == workingEntry.startTime
                    && entry.endTime == workingEntry.endTime
                    && entry.isGroup == workingEntry.isGroup
                    )) {
            Log.d(TAG, "validateFieldsAreNewAndSaveable: Fields are valid and updated")
            true

        } else {
            Log.d(TAG, "validateFieldsAreNewAndSaveable: Fields are valid but not updated")
            null
        }
    }

    override fun onStop() {
        Log.d(TAG, "onStop: called")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: called")
        saveEntry()
        super.onDestroy()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param entryID ID of the entry to load.
         * @return A new instance of fragment EntryFragment.
         */
        @JvmStatic
        fun newInstance(entryID: UUID?) =
            EntryFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PARAM_ENTRY_ID, entryID)
                }
            }
    }
}
