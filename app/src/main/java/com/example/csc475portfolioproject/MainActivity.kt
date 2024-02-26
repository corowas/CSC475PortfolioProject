package com.example.csc475portfolioproject

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var addToListButton: ImageButton
    private lateinit var startTimerButton: ImageButton
    private lateinit var timerTextView: TextView
    private lateinit var listView: ListView
    private lateinit var timer: CountDownTimer
    private lateinit var adapter: ArrayAdapter<String>
    private var timeLeftInMillis: Long = 0
    private var timerRunning = false
    private val itemList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        addToListButton = findViewById(R.id.addToListButton)
        startTimerButton = findViewById(R.id.startTimerButton)
        timerTextView = findViewById(R.id.timerTextView)
        listView = findViewById(R.id.listView)

        // Initialize adapter
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemList)
        listView.adapter = adapter

        // Add to list button click listener
        addToListButton.setOnClickListener {
            // Show dialog to prompt user for input
            showAddItemDialog()
        }

        // Start timer button click listener
        startTimerButton.setOnClickListener {
            // Show timer input dialog
            showTimerInputDialog()
        }
    }

    private fun showAddItemDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null)
        val editText = dialogView.findViewById<EditText>(R.id.editText)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Add Item")
            .setPositiveButton("Add") { dialog, _ ->
                val newItem = editText.text.toString().trim()
                if (newItem.isNotEmpty()) {
                    val currentTime = getCurrentTime()
                    val itemWithTime = "$newItem - $currentTime\n"
                    itemList.add(itemWithTime)
                    adapter.notifyDataSetChanged() // Notify adapter of data change
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showTimerInputDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_timer_input, null)
        val hoursInput = dialogView.findViewById<EditText>(R.id.hoursInput)
        val minutesInput = dialogView.findViewById<EditText>(R.id.minutesInput)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Set Timer Duration")
            .setPositiveButton("Start Timer") { dialog, _ ->
                val hours = hoursInput.text.toString().toIntOrNull() ?: 0
                val minutes = minutesInput.text.toString().toIntOrNull() ?: 0
                val durationInMillis = (hours * 3600000L) + (minutes * 60000L)
                startTimer(durationInMillis)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun getCurrentTime(): String {
        // Get the current time in the desired format
        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        return sdf.format(currentTime)
    }

    private fun startTimer(durationInMillis: Long) {
        timeLeftInMillis = durationInMillis

        // Start the countdown timer with the specified duration
        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerUI()
            }

            override fun onFinish() {
                // Perform actions when the timer finishes
                // For example, show a notification or perform some action
            }
        }
        timer.start() // Start the timer
        timerRunning = true
    }

    private fun updateTimerUI() {
        val hours = (timeLeftInMillis / 3600000).toInt()
        val minutes = ((timeLeftInMillis % 3600000) / 60000).toInt()
        val seconds = ((timeLeftInMillis % 60000) / 1000).toInt()
        val timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        timerTextView.text = "$timeLeftFormatted"
    }

    override fun onStop() {
        super.onStop()
        if (timerRunning) {
            timer.cancel()
            timerRunning = false
        }
    }
}
