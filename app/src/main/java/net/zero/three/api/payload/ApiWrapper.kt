package net.zero.three.api.payload

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ApiWrapper<T>(
    @SerializedName("status")
    val status: String,

    @SerializedName("error")
    val error: List<ApiError>?,

    @SerializedName("data")
    val data: T?,
): Serializable