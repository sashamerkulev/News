package ru.merkulyevsasha.network.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class ArticleResponse(
    @field:SerializedName("ArticleId") val articleId: Int,
    @field:SerializedName("SourceName") val sourceName: String,
    @field:SerializedName("Title") val title: String,
    @field:SerializedName("Link") val link: String,
    @field:SerializedName("Description") val description: String?,
    @field:SerializedName("PubDate") val pubDate: Date,
    @field:SerializedName("Category") val category: String,
    @field:SerializedName("PictureUrl") val pictureUrl: String?,
    @field:SerializedName("Likes") val likes: Int,
    @field:SerializedName("Dislikes") val dislikes: Int,
    @field:SerializedName("Comments") val comments: Int,
    @field:SerializedName("Like") val like: Int,
    @field:SerializedName("Dislike") val dislike: Int,
    @field:SerializedName("Comment") val comment: Int
)
