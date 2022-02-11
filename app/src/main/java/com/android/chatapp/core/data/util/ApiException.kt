package com.android.chatapp.core.data.util

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ApiException(
    val code: String, override val message: String,
    val fields: List<String> = listOf(),
    @SerialName("status_code") val statusCode: Int = 400
) : Exception() {

    override fun equals(other: Any?): Boolean =
        if (other is ApiException)
            this.hashCode() == other.hashCode()
        else false

    override fun hashCode(): Int {
        var result = code.hashCode()
        result = 31 * result + statusCode
        return result
    }
}
