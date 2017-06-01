package com.talentica.androidkotlin.videostreaming

import android.app.ProgressDialog
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import kotlin.concurrent.fixedRateTimer

/**
 * Created by suyashg on 01/06/17.
 */


class MainActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener,
        View.OnClickListener {

    private val HLS_STREAMING_SAMPLE = "rtsp://mpv.cdn3.bigCDN.com:554/bigCDN/_definst_/mp4:bigbuckbunnyiphone_400.mp4"
    private var sampleVideoView: VideoView? = null
    private var progressDialog: ProgressDialog? = null
    private var seekBar: SeekBar? = null
    private var playPauseButton: ImageView? = null
    private var stopButton: ImageView? = null
    private var runningTime: TextView? = null
    private var currentPosition: Int = 0
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sampleVideoView = findViewById(R.id.videoView) as VideoView
        sampleVideoView?.setVideoURI(Uri.parse(HLS_STREAMING_SAMPLE))

        playPauseButton = findViewById(R.id.playPauseButton) as ImageView
        playPauseButton?.setOnClickListener(this)

        stopButton = findViewById(R.id.stopButton) as ImageView
        stopButton?.setOnClickListener(this)

        seekBar = findViewById(R.id.seekBar) as SeekBar
        seekBar?.setOnSeekBarChangeListener(this)

        runningTime = findViewById(R.id.runningTime) as TextView
        runningTime?.setText("00:00")

        progressDialog = ProgressDialog(this)
        progressDialog?.setTitle("Preparing Video")
        progressDialog?.setMessage("Buffering...")
        progressDialog?.show()

        //Add the listeners
        sampleVideoView?.setOnCompletionListener(this)
        sampleVideoView?.setOnErrorListener(this)
        sampleVideoView?.setOnPreparedListener(this)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Toast.makeText(baseContext, "Play finished", Toast.LENGTH_LONG).show()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.e("video", "setOnErrorListener ")
        return true
    }

    override fun onPrepared(mp: MediaPlayer?) {
        progressDialog?.dismiss()
        seekBar?.setMax(sampleVideoView?.getDuration()!!)
        sampleVideoView?.start()

        val fixedRateTimer = fixedRateTimer(name = "hello-timer",
                initialDelay = 0, period = 1000) {
            refreshSeek()
        }

        playPauseButton?.setImageResource(R.mipmap.pause_button)
    }

    fun refreshSeek() {
        seekBar?.setProgress(sampleVideoView?.getCurrentPosition()!!);

        if (sampleVideoView?.isPlaying()!! == false) {
            return
        }

        var time = sampleVideoView?.getCurrentPosition()!! / 1000;
        var minute = time / 60;
        var second = time % 60;

        runOnUiThread {
            runningTime?.setText(minute.toString() + ":" + second.toString());
        }
    }

    var refreshTime = Runnable() {
        fun run() {

        }
    };

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        //do nothing
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        //do nothing
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        sampleVideoView?.seekTo(seekBar?.getProgress()!!)
    }

    override fun onClick(v: View?) {
        if (v?.getId() == R.id.playPauseButton) {
            //Play video
            if (!isRunning) {
                isRunning = true
                sampleVideoView?.resume()
                sampleVideoView?.seekTo(currentPosition)
                playPauseButton?.setImageResource(R.mipmap.pause_button)
            } else { //Pause video
                isRunning = false
                sampleVideoView?.pause()
                currentPosition = sampleVideoView?.getCurrentPosition()!!
                playPauseButton?.setImageResource(R.mipmap.play_button)
            }
        } else if (v?.getId() == R.id.stopButton) {
            playPauseButton?.setImageResource(R.mipmap.play_button)
            sampleVideoView?.stopPlayback()
            currentPosition = 0
        }
    }
}