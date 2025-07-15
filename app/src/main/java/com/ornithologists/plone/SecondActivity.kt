package com.ornithologists.plone

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.ornithologists.plone.functions.FetchSiteData
import com.ornithologists.plone.models.PloneSiteInfo

class SecondActivity : AppCompatActivity() {

    private lateinit var fetchSiteData: FetchSiteData
    private var siteInfo: PloneSiteInfo? = null

    private lateinit var textView: TextView
    private lateinit var backButton: Button
    private lateinit var retryButton: Button

    var videoViewErrorListener
            : MediaPlayer.OnErrorListener = object : MediaPlayer.OnErrorListener {
        override fun onError(arg0: MediaPlayer?, arg1: Int, arg2: Int): Boolean {
            Toast.makeText(
                applicationContext,
                "Error!!!",
                Toast.LENGTH_LONG
            ).show()
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        fetchSiteData = FetchSiteData(this)

        backButton = findViewById(R.id.backButton)
        retryButton = findViewById(R.id.retryButton)
        textView = findViewById<TextView>(R.id.headerText)

        val imageUrl = intent.getStringExtra("IMAGE_URL")
        val videoUrl = intent.getStringExtra("VIDEO_URL")
        val title = intent.getStringExtra("SITE_TITLE")
        val imageView = findViewById<ImageView>(R.id.imageView)
        val videoView = findViewById<View?>(R.id.videoView) as VideoView

        retryButton.setOnClickListener {
            textView.text = "Загрузка данных..."
            fetchData()
        }

        backButton.setOnClickListener {
            finish()
        }

        textView.text = title

        if (!imageUrl.isNullOrEmpty()) {
            imageView.visibility = View.VISIBLE
            Glide.with(this)
                .load(imageUrl)
                .into(imageView)
        }

        if (!videoUrl.isNullOrEmpty()) {videoView.visibility = View.VISIBLE}

        videoView.setVideoURI(videoUrl?.toUri())
        videoView.setMediaController(MediaController(this@SecondActivity))

        videoView.requestFocus()
        videoView.start()
        videoView.pause()

        videoView.setOnErrorListener(videoViewErrorListener)
        videoView.setOnErrorListener { mp, what, extra ->
            Log.e("VideoError", "Code: $what, Extra: $extra")
            true
        }
    }

    private fun fetchData() {
        val baseUrl = "http://5.129.204.33:8080/Plone/++api++"
        val pageUrl = "kak-ustanovit-plone-na-openbsd"

        fetchSiteData.fetchData(baseUrl, pageUrl) { siteInfo ->
            runOnUiThread {
                this@SecondActivity.siteInfo = siteInfo
                if (siteInfo != null) {
                    updateUI(siteInfo)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(siteInfo: PloneSiteInfo) {

        val videoView = findViewById<View?>(R.id.videoView) as VideoView
        val imageUrl = siteInfo.image?.download
        val videoUrl = siteInfo.video?.download

        findViewById<TextView>(R.id.headerText).text = siteInfo.title

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this@SecondActivity)
                .load(imageUrl)
                .into(findViewById<ImageView>(R.id.imageView))
        }

        videoView.setVideoURI(videoUrl?.toUri())
        videoView.setMediaController(MediaController(this@SecondActivity))

        videoView.requestFocus()
        videoView.start()

        videoView.setOnErrorListener(videoViewErrorListener)
        videoView.setOnErrorListener { mp, what, extra ->
            Log.e("VideoError", "Code: $what, Extra: $extra")
            true
        }
    }
}

