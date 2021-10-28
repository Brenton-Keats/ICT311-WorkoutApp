package com.example.ict311_workoutapp

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.format.DateTimeFormatter
import java.util.*

private const val TAG = "WorkListFragment"
private const val KEY_SELECTED = "KEY_SELECTED"

class WorkListFragment : Fragment() {

    interface Callbacks {
        fun onEntrySelected(entryID: UUID?)
    }

    private var callbacks: Callbacks? = null
    private lateinit var workRecyclerView: RecyclerView
    private var adapter: WorkAdapter? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("E dd MMM yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    private var selectedEntries = ArrayList<UUID>()

    private val workListViewModel: WorkListViewModel by lazy {
        ViewModelProviders.of(this).get(WorkListViewModel::class.java)
    }
    private val entryDetailViewModel: EntryDetailViewModel by lazy {
        ViewModelProviders.of(this).get(EntryDetailViewModel::class.java)
    }

    companion object {
        fun newInstance() = WorkListFragment()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_delete).isVisible = selectedEntries.isNotEmpty()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d(TAG, "onCreateOptionsMenu: called")
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.actionbar_actions, menu)
    }

    private fun confirmAndDeleteEntries() {
        val dialogClickListener = DialogInterface.OnClickListener { _, selected ->
            when(selected) {
                DialogInterface.BUTTON_POSITIVE -> {
                    for (id in selectedEntries) {
                        entryDetailViewModel.removeEntry(id)
                    }
                    selectedEntries.clear()
                    requireActivity().invalidateOptionsMenu()
                }
                else -> return@OnClickListener
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (selectedEntries.size > 1) R.string.dialog_delete_title_many else R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_yes, dialogClickListener)
            .setNegativeButton(R.string.dialog_no, dialogClickListener)
            .create()
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected: called")
        if (item.itemId == R.id.action_delete) {
            confirmAndDeleteEntries()
        } else if (item.itemId == R.id.action_create) {
            (context as Callbacks).onEntrySelected(null)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach called.")
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach called.")
        super.onDetach()
        callbacks = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: called")
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView called.")
        val view = inflater.inflate(R.layout.work_list_fragment, container, false)
        workRecyclerView = view.findViewById(R.id.work_recycler_view)
        workRecyclerView.layoutManager = LinearLayoutManager(context)

        workListViewModel.worksListLiveData.observe(
            viewLifecycleOwner,
            {
                entries: List<WorkEntry>? ->
                    entries?.let{
                        Log.i(TAG,"Total number of entries: ${entries.size}")
                        updateUI(entries)
                    }
            }
        )

        return view
    }

    private fun updateUI(entries: List<WorkEntry>) {
        Log.d(TAG, "updateUI called.")
        adapter = WorkAdapter(entries)
        workRecyclerView.adapter = adapter
    }

    @SuppressWarnings("unchecked")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated called.")
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            if (!savedInstanceState.isEmpty) {
                Log.d(TAG, "onViewCreated: Collecting selectedEntries from savedInstanceState")
                selectedEntries = savedInstanceState.getSerializable(KEY_SELECTED) as ArrayList<UUID>
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(KEY_SELECTED, selectedEntries)
    }

    private inner class WorkHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var entry: WorkEntry

        private val titleTextView: TextView = itemView.findViewById(R.id.work_entry_title)
        private val placeTextView: TextView = itemView.findViewById(R.id.work_entry_place)
        private val dateTextView: TextView = itemView.findViewById(R.id.work_entry_date)
        private val groupTextView: TextView = itemView.findViewById(R.id.work_entry_group)
        private val startTextView: TextView = itemView.findViewById(R.id.work_entry_start)
        private val endTextView: TextView = itemView.findViewById(R.id.work_entry_end)
        private val checkBox: CheckBox = itemView.findViewById(R.id.work_entry_selected)

        init {
            itemView.setOnClickListener(this)
            checkBox.setOnClickListener {
                if (checkBox.isChecked) {
                    selectedEntries.add(entry.id)
                } else {
                    selectedEntries.remove(entry.id)
                }
                Log.d(TAG, "CheckBox: Activity.invalidateOptionsMenu: called")
                requireActivity().invalidateOptionsMenu()
            }
        }

        override fun onClick(v: View?) {
            Toast.makeText(context,"${entry.title} pressed!", Toast.LENGTH_SHORT).show()
            (context as Callbacks).onEntrySelected(entry.id)
        }

        fun bind(entry: WorkEntry) {
            this.entry = entry
            titleTextView.text = entry.title
            placeTextView.text = entry.place
            dateTextView.text = entry.date.format(dateFormatter)
            groupTextView.setText(if (entry.isGroup) R.string.label_entry_group else R.string.label_entry_individual)
            startTextView.text = entry.startTime.format(timeFormatter)
            endTextView.text = entry.endTime.format(timeFormatter)
            checkBox.isChecked = entry.id in selectedEntries
        }
    }

    private inner class WorkAdapter(var entries: List<WorkEntry>?) : RecyclerView.Adapter<WorkHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkHolder {
            val view = layoutInflater.inflate(R.layout.work_list_item, parent, false)
            return WorkHolder(view)
        }

        override fun getItemCount() = entries?.size ?: 0

        override fun onBindViewHolder(holder: WorkHolder, position: Int) {
            val entry = entries?.get(position)
            if (entry != null) {
                holder.bind(entry)
            }
        }
    }
}