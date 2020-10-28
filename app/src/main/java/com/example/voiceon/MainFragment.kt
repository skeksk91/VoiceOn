package com.example.voiceon

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import me.itangqi.waveloadingview.WaveLoadingView
import kotlin.concurrent.timer


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var waveLoadingView: WaveLoadingView
    private lateinit var recordingBtn: ImageView

    private var isRecording: Boolean = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        waveLoadingView = view.findViewById(R.id.wave)
        waveLoadingView.setOnClickListener {
            when {
                !isRecording -> startRecording()
                isRecording -> stopRecording()
            }
        }

        recordingBtn = view.findViewById(R.id.recording_btn)
    }

    private fun stopRecording() {
        isRecording = false
        Toast.makeText(context, R.string.record_finish, Toast.LENGTH_SHORT).show()
        recordingBtn.setImageResource(R.drawable.mic)

        timer(period = 1, initialDelay = 0) {
            AssertionError(waveLoadingView.progressValue >= 0)

            activity?.runOnUiThread{
                waveLoadingView.progressValue--
            }
            if (waveLoadingView.progressValue <= 0) {
                cancel()
            }
        }
    }

    val maximumRecordSec : Int = 1000 * 20
    private fun startRecording() {
        isRecording = true;
        Toast.makeText(context, R.string.record_start, Toast.LENGTH_SHORT).show()

        recordingBtn.setImageResource(R.drawable.ic_stop_record)

        timer(period = 200, initialDelay = 0) {
            if (!isRecording) {
                cancel()
            }

            activity?.runOnUiThread{
                ++waveLoadingView.progressValue
                if (waveLoadingView.progressValue >= 100) {
                    stopRecording()
                    cancel()
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}