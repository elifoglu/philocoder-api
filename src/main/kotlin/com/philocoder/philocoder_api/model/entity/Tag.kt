package com.philocoder.philocoder_api.model.entity

data class Tag(
    val tagId: String,
    val name: String,
    val contentSortStrategy: String,
    val showAsTag: Boolean,
    val contentRenderType: String,
    val showContentCount: Boolean,
    val showInHeader: Boolean
)