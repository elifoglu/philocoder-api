package com.philocoder.philocoder_api.model.entity

import com.philocoder.philocoder_api.model.ContentDate
import com.philocoder.philocoder_api.model.ContentID

data class Content(
    val title: String?,
    val date: ContentDate,
    val contentId: ContentID,
    val content: String?,
    val tags: List<String>,
    val refs: List<ContentID>?
)