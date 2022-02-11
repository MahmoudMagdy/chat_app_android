package com.android.chatapp.core.data.util

import kotlinx.serialization.Serializable

@Serializable
data class PaginationResult<out T>(
    val next: Int?,
    val previous: Int?,
    val results: List<T>
)

const val BASE_PAGE_SIZE = 10
const val EMPTY_LATEST_ITEM_ID = -1L

inline fun <reified T> Resource<PaginationResult<T>, Any>.items(): List<T>? = when (this) {
    is Resource.Error -> data?.results
    is Resource.Failure -> data?.results
    is Resource.Loading -> data?.results
    is Resource.Success -> data.results
}