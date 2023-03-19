package com.example.sqlite

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.sqlite.databinding.ActivityMainBinding
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), SecondFragment.NotificationListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

    }


    override fun onNotificationReceived(notification: Intent?) {
        // Handle the notification in the Activity
        playMusic( Uri.parse(notification?.data.toString()))
    }
    private fun playMusic(uri: Uri) {
        // Release any previous media player instance
        mediaPlayer?.release()

        // Create a new media player instance for the selected URI
        mediaPlayer = MediaPlayer.create(this, uri)

        // Start the media player
        mediaPlayer?.start()
        mediaPlayer?.isLooping = true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


    private fun activityRecognitionPermissionApproved(): Boolean {

        // TODO: Review permission check for 29+.
        return if (runningQOrLater) {
            PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        } else {
            true
        }
    }
}