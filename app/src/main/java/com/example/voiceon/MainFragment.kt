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
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
    private lateinit var recordingText: TextView
    private lateinit var echoBtn: ImageView
    private lateinit var cancelBtn: ImageView
    private lateinit var listBtn: ImageView

    private var isRecording: Boolean = false

    enum class RecordButtonState {
        RECORD, RECORD_STOP, PLAY, PLAY_STOP
    }
    private var recordBtnState: RecordButtonState = RecordButtonState.RECORD

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
        waveLoadingView.borderColor = Color.GRAY
        waveLoadingView.setOnClickListener {
            when (recordBtnState){
                RecordButtonState.RECORD -> startRecording()
                RecordButtonState.RECORD_STOP -> stopRecording()
                RecordButtonState.PLAY -> playRecorded()
                RecordButtonState.PLAY_STOP -> stopPlaying()
            }
        }

        recordingBtn = view.findViewById(R.id.recording_btn)
        recordingBtn.bringToFront()
        recordingText = view.findViewById(R.id.recording_text)
        recordingText.visibility = View.INVISIBLE
        echoBtn = view.findViewById(R.id.echo)
        echoBtn.setOnClickListener {
            if (echoBtn.alpha != 1f) {
                return@setOnClickListener
            }
            recordBtnState = RecordButtonState.RECORD
            recordingBtn.setImageResource(R.drawable.mic)
            hideRecordResultElement()
        }

        cancelBtn = view.findViewById(R.id.cancel_record)
        cancelBtn.setOnClickListener {
            if (cancelBtn.alpha != 1f) {
                return@setOnClickListener
            }
            recordBtnState = RecordButtonState.RECORD
            recordingBtn.setImageResource(R.drawable.mic)
            hideRecordResultElement()
        }

        listBtn = view.findViewById(R.id.list_btn)
        listBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_listFragment)
        }
    }

    private fun playRecorded() {
        recordBtnState = RecordButtonState.PLAY_STOP
        recordingBtn.setImageResource(R.drawable.ic_stop_record)
    }

    private fun stopPlaying() {
        recordBtnState = RecordButtonState.PLAY
        recordingBtn.setImageResource(R.drawable.ic_hearing_24px)
    }

    private fun hideRecordResultElement() {
        echoBtn.animate().alpha(0f).duration = 600
        cancelBtn.animate().alpha(0f).duration = 600
    }

    private fun stopRecording() {
        isRecording = false
        recordBtnState = RecordButtonState.PLAY
        recordingBtn.setImageResource(R.drawable.ic_hearing_24px)
        recordingText.text = getString(R.string.recording_complete)
        echoBtn.animate().alpha(1f).duration = 600
        cancelBtn.animate().alpha(1f).duration = 600

        timer(period = 2000, initialDelay = 2000) {
            activity?.runOnUiThread{
                recordingText.visibility = View.INVISIBLE
                cancel()
            }
        }

        waveLoadingView.borderColor = Color.GRAY
        waveLoadingView.borderWidth = resources.displayMetrics.density * 2

        timer(period = 10, initialDelay = 0) {
            activity?.runOnUiThread{
                --waveLoadingView.progressValue
            }
            if (waveLoadingView.progressValue <= 0) {
                cancel()
            }
        }
    }

    private val maximumRecordSec : Int = 1000 * 20
    var borderWidthCount = 0
    var direction = 1

    private fun startRecording() {
        isRecording = true;
        recordBtnState = RecordButtonState.RECORD_STOP
        recordingBtn.setImageResource(R.drawable.ic_stop_record)
        recordingText.text = getString(R.string.recording)
        recordingText.visibility = View.VISIBLE
        waveLoadingView.borderColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)

        //Toast.makeText(context, R.string.record_start, Toast.LENGTH_SHORT).show()

        borderWidthCount = 0
        direction = 1

        timer(period = (maximumRecordSec / 100).toLong(), initialDelay = 0) {
            if (!isRecording) {
                waveLoadingView.centerTitle = ""
                cancel()
            }

            activity?.runOnUiThread{
                if (!isRecording) cancel()

                ++waveLoadingView.progressValue

                borderWidthCount += direction
                waveLoadingView.borderWidth += direction;
                if (borderWidthCount % 5 == 0) {
                    direction = -direction;
                }

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