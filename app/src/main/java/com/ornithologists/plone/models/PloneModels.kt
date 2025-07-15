package com.ornithologists.plone.models

import com.squareup.moshi.Json
import android.os.Parcel
import android.os.Parcelable

data class PloneSiteInfo(
    @Json(name = "@id") val id: String?,
    val title: String?,
    val description: String?,
    val text: BodyContent?,
    val image: ImageData?,
    var video: VideoData?
)
data class BodyContent(
    var data: String?,
)

data class ImageData(
    val download: String?,
    val width: Int?,
    val height: Int?,
    @Json(name = "content-type") val contentType: String?
)

data class VideoData(
    val download: String?,
    val width: Int?,
    val height: Int?,
    @Json(name = "content-type") val contentType: String?
)