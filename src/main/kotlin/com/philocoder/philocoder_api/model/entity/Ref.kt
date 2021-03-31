package com.philocoder.philocoder_api.model.entity

import com.philocoder.philocoder_api.util.ConversionUtils.a

data class Ref(
    val text: String,
    val id: String
) {
    companion object {
        fun createWith(content: Content): Ref {
            val contentId = content.contentId.toString()
            val text: String = content.title.a.fold({contentId}, { it })
            return Ref(text, contentId)
        }
    }
}