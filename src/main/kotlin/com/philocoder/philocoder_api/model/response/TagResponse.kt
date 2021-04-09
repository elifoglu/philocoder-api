package com.philocoder.philocoder_api.model.response

import com.philocoder.philocoder_api.model.entity.Tag
import com.philocoder.philocoder_api.repository.ContentRepository

data class TagResponse(
    val tagId: String,
    val name: String,
    val contentSortStrategy: String,
    val showAsTag: Boolean,
    val contentRenderType: String,
    val showContentCount: Boolean,
    val showInHeader: Boolean,
    val contentCount: Int,
    val infoContentId: Int?
) {

    companion object {
        fun createWith(tag: Tag, repo: ContentRepository): TagResponse =
            TagResponse(
                tagId = tag.tagId,
                name = tag.name,
                contentSortStrategy = tag.contentSortStrategy,
                showAsTag = tag.showAsTag,
                contentRenderType = tag.contentRenderType,
                showContentCount = tag.showContentCount,
                showInHeader = tag.showInHeader,
                contentCount = repo.getContentCount(tag.name),
                infoContentId = tag.infoContentId
            )
    }
}