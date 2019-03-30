package ru.merkulyevsasha.network.models

import com.google.gson.annotations.SerializedName

class TokenResponse(@SerializedName("token") val token: String)