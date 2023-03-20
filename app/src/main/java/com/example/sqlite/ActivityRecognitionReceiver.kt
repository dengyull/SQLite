package com.example.sqlite

import android.Manifest.permission.ACTIVITY_RECOGNITION
import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.TextView
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import com.google.android.material.snackbar.Snackbar

class ActivityRecognitionReceiver : BroadcastReceiver() {

    var listener: SecondFragment.NotificationListener? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("ActivityRecognition", "nothing")
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = intent?.let { ActivityTransitionResult.extractResult(it) }!!
            for (event in result.transitionEvents) {
                // chronological sequence of events....
                Log.d("ActivityRecognition", "transitionEvents")
            }
            Log.d("ActivityRecognition", "something")
            val activityRecognitionResult =
                intent?.let { ActivityRecognitionResult.extractResult(it) }
            val probableActivities = activityRecognitionResult?.probableActivities
            if (probableActivities != null && probableActivities.isNotEmpty()) {
                // Get the most likely activity and its confidence value
                val mostLikelyActivity = probableActivities[0].type
                val confidence = probableActivities[0].confidence
                val dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder.setView(R.layout.dialog_layout)
                dialogBuilder.setTitle("Dialog title")
                dialogBuilder.setPositiveButton("OK") { dialog, which ->
                    // Handle OK button click
                }
                dialogBuilder.setNegativeButton("Cancel") { dialog, which ->
                    // Handle cancel button click
                }
                val textView = (context as Activity).findViewById<TextView>(R.id.textView_test)
                // Process the activity data (e.g., update UI, log data to database, etc.)
                when (mostLikelyActivity) {
                    DetectedActivity.WALKING -> {
                        Log.d("ActivityRecognition", "User is walking with confidence $confidence")
                        // Update UI or log data to database for walking activity
                        textView.text = "walking"
                    }
                    DetectedActivity.RUNNING -> {
                        Log.d("ActivityRecognition", "User is running with confidence $confidence")
                        // Update UI or log data to database for running activity
                        textView.text = "RUNNING"
                        listener?.play()

                    }
                    DetectedActivity.IN_VEHICLE -> {
                        Log.d(
                            "ActivityRecognition",
                            "User is in vehicle with confidence $confidence"
                        )
                        // Update UI or log data to database for in vehicle activity
                        textView.text = "VEAHICale"
                    }
                    DetectedActivity.STILL -> {
                        Log.d("ActivityRecognition", "User is still with confidence $confidence")
                        // Update UI or log data to database for still activity
                        textView.text = "STILL"
                    }
                    else -> {
                        Log.d("ActivityRecognition", "Unknown activity with confidence $confidence")
                        textView.text = "unknown"
                    }

                }
                dialogBuilder.setMessage(textView.text)
                val dialog = dialogBuilder.create()
                dialog.show()
            }
        } else {
            Log.d("ActivityRecognition", "no Result")

        }
        if (intent != null) {
            val result = ActivityRecognitionResult.extractResult(intent)
            Log.d("ActivityRecognition", "no null")
            val activity = result?.mostProbableActivity
            if (activity != null) {
                Log.d("ActivityRecognition", "Detected activity: ${activity.type}")
            }
        }
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = intent?.let { ActivityRecognitionResult.extractResult(it) }
            val activity = result?.mostProbableActivity
            if (activity != null) {
                Log.d("ActivityRecognition", "Detected activity: ${activity.type}")
            }
        }
    }
}

