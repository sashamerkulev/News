package ru.merkulyevsasha.network.models

import com.google.gson.annotations.SerializedName

data class RssSourceResponse(
    @SerializedName("name") val name: String,
    @SerializedName("title") val title: String
)