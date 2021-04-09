package com.philocoder.philocoder_api.model.request

data class CreateTagRequest(
    val tagId: String,
    val name: String,
    val contentSortStrategy: String,
    val showAsTag: Boolean,
    val contentRenderType: String,
    val showContentCount: Boolean,
    val showInHeader: Boolean,
    val password: String
)