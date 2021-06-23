package net.zero.three.api.payload

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ApiError(
    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String?
) : Serializable