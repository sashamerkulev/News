package ru.merkulyevsasha.network.models

import com.google.gson.annotations.SerializedName

data class RssSourceResponse(
    @SerializedName("Name") val sourceId: String,
    @SerializedName("Title") val sourceName: String
)