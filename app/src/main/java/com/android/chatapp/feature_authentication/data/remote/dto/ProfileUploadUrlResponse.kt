package com.android.chatapp.feature_authentication.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ProfileUploadUrlResponse(
    val url: String,
    val fields: JsonObject,
    @SerialName("cloud_path")
    val cloudPath:String
)

