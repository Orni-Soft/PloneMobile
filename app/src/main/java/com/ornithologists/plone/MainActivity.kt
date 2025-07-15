package com.ornithologists.plone

import com.ornithologists.plone.functions.FetchSiteData
import com.ornithologists.plone.models.PloneSiteInfo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    private lateinit var fetchSiteData: FetchSiteData
    private var siteInfo: PloneSiteInfo? = null

    private lateinit var textView: TextView
    private lateinit var retryButton: Button
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchSiteData = FetchSiteData(this)

        textView = findViewById(R.id.textView)
        textView.text = "Загрузка данных..."
        retryButton = findViewById(R.id.retryButton)
        retryButton.text = "Обновить"
        nextButton = findViewById(R.id.nextButton)
        nextButton.text = "Далее"

        retryButton.setOnClickListener {
            textView.text = "Загрузка данных..."
            fetchData()
        }

        nextButton.setOnClickListener {
            if (siteInfo == null) {
                Toast.makeText(this, "Данные еще не загружены", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, SecondActivity::class.java).apply {
                putExtra("IMAGE_URL", siteInfo?.image?.download)
                putExtra("VIDEO_URL", siteInfo?.video?.download)
                putExtra("SITE_TITLE", siteInfo?.title)
            }
            startActivity(intent)
        }
        fetchData()
    }

    private fun fetchData() {
        val baseUrl = "http://5.129.204.33:8080/Plone/++api++"
        val pageUrl = "kak-ustanovit-plone-na-openbsd"

        fetchSiteData.fetchData(baseUrl, pageUrl) { siteInfo ->
            runOnUiThread {
                this@MainActivity.siteInfo = siteInfo
                if (siteInfo != null) {
                    updateUI(siteInfo)
                } else {
                    findViewById<TextView>(R.id.textView).text = "Данные не загружены"
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(siteInfo: PloneSiteInfo) {
        findViewById<TextView>(R.id.mainText).text = siteInfo.text?.data
        findViewById<TextView>(R.id.headerText).text = siteInfo.title
        findViewById<TextView>(R.id.textView).visibility = View.GONE

        val imageUrl = siteInfo.image?.download
        if (!imageUrl.isNullOrEmpty()) {
            findViewById<ImageView>(R.id.articleImage).visibility = View.VISIBLE
            Glide.with(this@MainActivity)
                .load(imageUrl)
                .into(findViewById(R.id.articleImage))
        } else {
            Toast.makeText(this, "Изображение не загружено", Toast.LENGTH_SHORT).show()
        }
    }
}