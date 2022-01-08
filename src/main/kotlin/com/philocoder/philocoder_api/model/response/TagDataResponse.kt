package com.philocoder.philocoder_api.model.response

data class TagDataResponse(
    val allTags: List<TagResponse>,
    val blogModeTags: List<TagResponse>
)