package ru.merkulyevsasha.network.models

import com.google.gson.annotations.SerializedName

data class RssSourceResponse(
    @SerializedName("Name") val name: String,
    @SerializedName("Title") val title: String
)