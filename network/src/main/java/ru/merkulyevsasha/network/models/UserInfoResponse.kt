package ru.merkulyevsasha.network.models

import com.google.gson.annotations.SerializedName

data class UserInfoResponse(
    @SerializedName("UserId") val id: Int,
    @SerializedName("Name") val name: String?,
    @SerializedName("Phone") val phone: String?,
    @SerializedName("Token") val token: String?
)
