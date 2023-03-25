package com.example.sqlite

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.sqlite.databinding.FragmentFirstBinding
import java.lang.Long.getLong
import java.lang.reflect.Array.getInt
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val handler: Handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    private lateinit var txtActivity: TextView
    private lateinit var myActivity: MyActivity
    private lateinit var img: ImageView
    lateinit var dd: LocalDateTime
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var us: Uri
    private var lest = 0;
    private lateinit var handlers: Handler
    private lateinit var runnables: Runnable
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.buttonFirst.setOnClickListener {
            // Get current time and create activity data
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        handler.postDelayed(runnable, 0);

        dd = LocalDateTime.now()
        myActivity = MyActivity.STILL
        txtActivity = binding.textViewTest
        img = binding.imageView2
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("WALKING")
        builder.setMessage("WALKING!! Keep up the good work!")
        builder.setPositiveButton("ok") { dialog, which ->
            // Handle positive button click
        }
        val dialog = builder.create()
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationChanged(location: Location) {
                // Get the speed in meters/second
                val speed = location.speed
                // Convert speed to km/h
                val speedKMH = speed * 3.6
                // Do something with the speed value
                Log.d("Speed", "Speed: $speedKMH km/h")
                val now = LocalDateTime.now()
                val durations: Duration = Duration.between(dd, now)
                var cur = checkActivity(speed,durations.seconds)
                txtActivity.text = cur.toString()// +" "+ speed.toString() + "m/s"
                if (myActivity==cur){
                    lest = 0

                } else {
                    lest++
                    if (lest>=5){

                        dialog.dismiss()
                        dd = now

                        logActivity(now.plusMinutes(-5).toString(),durations.seconds,myActivity.toString())
                        when (cur) {
                            MyActivity.WALKING -> {
                                img.setImageResource(R.drawable.walk_icon)
                                txtActivity.text = "WALKING "// + speed.toString() + "m/s"

                                dialog.show()

                            }
                            MyActivity.RUNNING -> {
                                txtActivity.text = "RUNNING "// + speed.toString() + "m/s"
                                img.setImageResource(R.drawable.run_icon)
                            }
                            MyActivity.IN_VEHICLE -> {
                                txtActivity.text = "IN_VEHICLE "// + speed.toString() + "m/s"
                                img.setImageResource(R.drawable.car_icon)
                            }
                            MyActivity.STILL -> {
                                img.setImageResource(R.drawable.still_icon)
                                txtActivity.text = "STILL "// + speed.toString() + "m/s"

                            }
                        }
                        val message = "You have just  ${myActivity.toString()} for ${durations.seconds} seconds"
                        myActivity = cur
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                    }

                }

            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}
        }


        handlers = Handler(Looper.getMainLooper())
        runnables = object : Runnable {
            override fun run() {
                if (!isAdded) {
                    return
                }
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    val permissionArray = arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    val requestCode = 123 // Replace with your desired request code
                    ActivityCompat.requestPermissions(requireActivity(), permissionArray, requestCode)
                    return
                }
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    locationListener
                )
                handlers.postDelayed(this, 1000) // Update every 1 second
            }
        }
        handlers.post(runnables)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnable);
        _binding = null
    }


    private val runnable: Runnable = object : Runnable {
        override fun run() {
            binding.textviewFirst.setText("welcome ,\n now is "+getCurrentDateTime())
            binding.textviewFirst.gravity
            handler.postDelayed(this, 1000) // update every second
        }
    }

    private fun getCurrentDateTime(): String? {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    fun logActivity(now:String, activityDuration:Long,activity:String){
        if (activityDuration>=30) {
            val databaseHelper = com.example.sqlite.ActivityLogDatabaseHelper(requireContext())

            // Log activity data to the database
            val db = databaseHelper.writableDatabase
            val values = android.content.ContentValues().apply {
                put("start_time", now.toString())
                put("duration", activityDuration)
                put("activity", activity)
            }
            db.insert("activity_log", null, values)
            db.close()
        }
    }
    fun checkActivity(value: Float, durations: Long): MyActivity {
        if (durations>5) {

            return when {
                value < 0.6 -> MyActivity.STILL
                value >= 0.6 && value <= 2.5 -> MyActivity.WALKING
                value > 2.5 && value <= 10 -> MyActivity.RUNNING
                value > 10 -> MyActivity.IN_VEHICLE
                else -> throw IllegalArgumentException("Invalid value: $value")
            }

        } else {
            return myActivity
        }
    }
}