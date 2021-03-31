package com.philocoder.philocoder_api.model.request

data class TagsRequest(
    val page: Int = 1,
    val size: Int = 10,
    val onlyHeaderTags: Boolean = false
)