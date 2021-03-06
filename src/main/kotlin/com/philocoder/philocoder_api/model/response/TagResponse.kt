package com.philocoder.philocoder_api.model.response

import com.philocoder.philocoder_api.model.entity.Tag
import com.philocoder.philocoder_api.model.request.TagsRequest
import com.philocoder.philocoder_api.repository.ContentRepository

data class TagResponse(
    val tagId: String,
    val name: String,
    val contentSortStrategy: String,
    val showAsTag: Boolean,
    val contentRenderType: String,
    val showContentCount: Boolean,
    val headerIndex: Int?,
    val contentCount: Int,
    val infoContentId: Int?
) {

    companion object {
        fun create(tag: Tag, blogMode: Boolean, repo: ContentRepository): TagResponse =
            TagResponse(
                tagId = tag.tagId,
                name = tag.name,
                contentSortStrategy = tag.contentSortStrategy,
                showAsTag = tag.showAsTag,
                contentRenderType = tag.contentRenderType,
                showContentCount = tag.showContentCount,
                headerIndex = tag.headerIndex,
                contentCount = repo.getContentCount(tag.name, blogMode),
                infoContentId = tag.infoContentId
            )

        fun createForAllContentsMode(tag: Tag, repo: ContentRepository): TagResponse =
            create(tag, false, repo)

        fun createForBlogMode(tag: Tag, repo: ContentRepository): TagResponse =
            create(tag, true, repo)
    }
}