package com.philocoder.philocoder_api.model.request

data class ContentsOfTagRequest(
    val tagId: String,
    val page: Int = 1,
    val size: Int = 10,
    val blogMode: Boolean
)