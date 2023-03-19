package com.example.sqlite

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.example.sqlite.databinding.FragmentFirstBinding
import java.lang.Long.getLong
import java.lang.reflect.Array.getInt
import java.text.SimpleDateFormat
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            // Get current time and create activity data
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        handler.postDelayed(runnable, 0);

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

}