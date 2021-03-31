package com.philocoder.philocoder_api.model.response

data class ContentsResponse(
    val totalPageCount: Int,
    val contents: List<ContentResponse>
)