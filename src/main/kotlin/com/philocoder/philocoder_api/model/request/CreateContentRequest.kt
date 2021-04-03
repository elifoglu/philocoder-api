package com.philocoder.philocoder_api.model.request

data class CreateContentRequest(
    val id: String,
    val title: String?,
    val text: String,
    val date: String?,
    val publishOrderInDay: String,
    val tags: String,
    val refs: String?,
    val password: String
)