package com.philocoder.philocoder_api.model

import com.philocoder.philocoder_api.model.entity.Content
import com.philocoder.philocoder_api.model.entity.Tag

data class All(
    val allContents: List<Content>,
    val allTags: List<Tag>
)