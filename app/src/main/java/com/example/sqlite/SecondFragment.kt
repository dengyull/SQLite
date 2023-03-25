package com.example.sqlite

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sqlite.databinding.FragmentSecondBinding
import java.time.Duration
import java.time.LocalDateTime

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment(), AdapterView.OnItemClickListener {

    private var _binding: FragmentSecondBinding? = null
    lateinit var dd:LocalDateTime

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    var fileLists = ArrayList<String>()
    var musiclists = ArrayList<Music>()
    lateinit var fileAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.isVisible = false
        binding.buttonSecond.setOnClickListener {
            //findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
        binding.buttonShow.setOnClickListener {
            //val durations: Duration = Duration.between(dd, now)
            //logActivity(now.toString(),durations.seconds,activity)
            qu()

        }
        binding.buttonMusic.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "audio/*"
            resultLauncher.launch(intent);

        }

        val MusicDbHelper = MusicDbHelper(this.requireContext())
        try{
            musiclists = MusicDbHelper.getMusic()
            for (music in musiclists){
                fileLists.add(music.title)

            }

        } catch (e: Exception){

        }

        val listView = binding.fileList
        fileAdapter = ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_list_item_1, fileLists)
        listView.adapter = fileAdapter
        listView.onItemClickListener = this

        fileAdapter.notifyDataSetChanged()
    }

    fun changev(str: String){
        val tv = TextView(context)
        tv.text = str;
        //binding.scrollView2.removeAllViews()
        val xx = binding.scrollView2.get(0) as LinearLayout
        xx.removeAllViews()
        xx.addView(tv)
        //binding.scrollView2.addView(tv)
        binding.scrollView2.post { binding.scrollView2.fullScroll(View.FOCUS_DOWN) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun logActivity(now:String, activityDuration:Long,activity:String){
        val databaseHelper = ActivityLogDatabaseHelper(this.requireContext())

        // Log activity data to the database
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put("start_time", now.toString())
            put("duration", activityDuration)
            put("activity", activity)
        }
        db.insert("activity_log", null, values)
        db.close()
    }

    fun qu(){
        val databaseHelper = ActivityLogDatabaseHelper(this.requireContext())
        val db = databaseHelper.writableDatabase
        val projection = arrayOf(
            ActivityLogDatabaseHelper.COLUMN_ID,
            ActivityLogDatabaseHelper.COLUMN_START_TIME,
            ActivityLogDatabaseHelper.COLUMN_DURATION,
            ActivityLogDatabaseHelper.COLUMN_ACTIVITY
        )
        val sortOrder = "${ActivityLogDatabaseHelper.COLUMN_START_TIME} DESC"

        // Update the latest activity text view
        val latestActivityCursor = db.query(
            ActivityLogDatabaseHelper.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            sortOrder
        )
        var texts = ""
        with(latestActivityCursor){
            while (moveToNext()) {
                val latestStartTime = getString(getColumnIndexOrThrow(ActivityLogDatabaseHelper.COLUMN_START_TIME))
                val latestDuration = getInt(getColumnIndexOrThrow(ActivityLogDatabaseHelper.COLUMN_DURATION))
                val latestActivity = getString(getColumnIndexOrThrow(ActivityLogDatabaseHelper.COLUMN_ACTIVITY))
                val roundedSeconds = latestDuration / 60 * 60
                val minutes = roundedSeconds / 60
                texts = texts + "\nLatest Activity: $latestActivity ($minutes min ($latestDuration s)) on $latestStartTime"

            }
        }
        //changev(texts)


        binding.textviewSecond.text = texts
        latestActivityCursor.close()
        db.close()
    }



    interface NotificationListener {

        fun onNotificationReceived(notification: String)
        fun play()
    }
    private var listener: NotificationListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NotificationListener
        if (listener == null) {
            throw ClassCastException("$context must implement NotificationListener")
        }
    }
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val us = result.data?.data
            Log.v("URL", us.toString())
            listener?.onNotificationReceived(data?.data.toString())
            val contentResolver = context?.contentResolver
            val MusicDbHelper = MusicDbHelper(this.requireContext())
            val db =MusicDbHelper.writableDatabase

            val cursor = us?.let { contentResolver?.query(it, null, null, null, null) }
            if (cursor != null && cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val fileName = cursor.getString(nameIndex)
                cursor.close()
                fileLists.add(fileName)
                MusicDbHelper.insertMusic(fileName, data?.data.toString())
                musiclists.add(Music(fileName,data?.data.toString()))
                fileAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


        val fileName = musiclists[position]
        Toast.makeText(context, "Selected file: $fileName", Toast.LENGTH_SHORT).show()
        var sharedPref : SharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        sharedPref.edit().putString("song", fileName.filePath).apply()
        Log.d("music url", fileName.filePath)
        listener?.onNotificationReceived(fileName.filePath)
    }
}