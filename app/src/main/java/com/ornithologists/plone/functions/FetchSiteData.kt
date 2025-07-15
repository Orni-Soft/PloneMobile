package com.ornithologists.plone.functions

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.core.text.HtmlCompat
import com.ornithologists.plone.models.ImageData
import com.ornithologists.plone.models.PloneSiteInfo
import com.ornithologists.plone.models.VideoData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import java.io.IOException

class FetchSiteData(private val context: Context) : AppCompatActivity() {

    private val client = OkHttpClient()
    private val htmlSafelist = Safelist()
        .addTags("p", "br", "b", "strong", "i", "em", "ul", "ol", "li", "code")
        .addAttributes("a", "href")
        .addProtocols("a", "href", "http", "https")
    val moshi: Moshi? = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    fun fetchData(baseUrl: String,
                  pageUrl: String,
                  callback: (PloneSiteInfo?) -> Unit)
    {
        val request = Request.Builder()
            .url("$baseUrl/$pageUrl")
            .addHeader("Accept", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) return

                    val json = response.body?.string()

                    val adapter = moshi?.adapter(PloneSiteInfo::class.java)
                    val siteInfo = adapter?.fromJson(json!!)
                    val videoUrl = extractVideoUrl(json.toString()).toString().replace("\\","").replace("\"","")

                    siteInfo?.video = VideoData(
                        download = videoUrl,
                        width = null,
                        height = null,
                        contentType = null
                    )

                    siteInfo?.text?.data = parseHtmlContent(siteInfo.text.data).toString().trim()

                    callback(siteInfo)
                    Log.d("FETCH_DATA", json.toString())
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(null)
            }
        })
    }

    fun parseHtmlContent(html: String?): Spanned {
        if (html.isNullOrEmpty()) return SpannableString("")

        val cleaned = Jsoup.clean(html, htmlSafelist)
        return HtmlCompat.fromHtml(cleaned, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun extractVideoUrl(htmlContent: String): String? {
        val doc = Jsoup.parse(htmlContent)
        return doc.select("source").first()?.attr("src")
    }

}
