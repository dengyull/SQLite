package com.example.sqlite

import android.Manifest
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.sqlite.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity(), SecondFragment.NotificationListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    private lateinit var activityRecognitionClient: ActivityRecognitionClient
    private lateinit var activityTransitionList: ArrayList<ActivityTransition>
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var myActivity: MyActivity
    private lateinit var img:ImageView
    lateinit var dd: LocalDateTime
    private lateinit var sensorManager: SensorManager

    private lateinit var accelerometer: Sensor
    private lateinit var gyroscope: Sensor

    private lateinit var us: Uri
    private var lest = 0;


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dd = LocalDateTime.now()
        myActivity = MyActivity.STILL
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        img = findViewById(R.id.imageView2)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        var sharedPref : SharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        us = Uri.parse(sharedPref.getString("song",""))
        Log.d("music url", us.toString())
        if(us.equals("")){
            mediaPlayer = MediaPlayer.create(this, R.raw.music)
            mediaPlayer?.start()
            mediaPlayer?.isLooping = true
        } else{
            mediaPlayer = MediaPlayer.create(this, R.raw.music)
            mediaPlayer?.start()
            mediaPlayer?.isLooping = true


        }










    }


    override fun onNotificationReceived(notification: Intent?) {
        // Handle the notification in the Activity
        us = Uri.parse(notification?.data.toString())
        playMusic( Uri.parse(notification?.data.toString()))
    }

    override fun play() {

        mediaPlayer?.release()

        // Create a new media player instance for the selected URI
        mediaPlayer = MediaPlayer.create(this, us)

        // Start the media player
        mediaPlayer?.start()
        mediaPlayer?.isLooping = true
    }

    fun stop(){
        mediaPlayer?.release()
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

    override fun onResume() {
        super.onResume()
        mediaPlayer?.start()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        //handler.removeCallbacks(runnable)
        //locationManager.removeUpdates(locationListener)
        mediaPlayer?.pause()
        //logActivity(LocalDateTime.now().toString(),Duration.between(dd, LocalDateTime.now()).seconds,myActivity.toString())
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

