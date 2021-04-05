package com.philocoder.philocoder_api.model.request

interface ContentRequest {
    val title: String?
    val text: String
    val date: String?
    val publishOrderInDay: String
    val tags: String
    val refs: String?
    val password: String
}