package com.philocoder.philocoder_api.model.entity

import arrow.core.Tuple2
import arrow.core.extensions.list.foldable.exists
import com.fasterxml.jackson.annotation.JsonIgnore
import com.philocoder.philocoder_api.model.request.CreateTagRequest
import com.philocoder.philocoder_api.model.request.UpdateTagRequest
import com.philocoder.philocoder_api.repository.TagRepository
import com.philocoder.philocoder_api.util.Encryptor
import com.philocoder.philocoder_api.util.RootUserConfig
import org.elasticsearch.search.sort.SortOrder

data class Tag(
    val tagId: String,
    val name: String,
    val contentSortStrategy: String,
    val showAsTag: Boolean,
    val contentRenderType: String,
    val showContentCount: Boolean,
    val headerIndex: Int?,
    val infoContentId: Int?
) {

    @JsonIgnore
    val contentSorter: Tuple2<String, SortOrder> = Tuple2(
        "dateAsTimestamp",
        if (contentSortStrategy == "DateASC") SortOrder.ASC else SortOrder.DESC
    )

    companion object {
        fun createIfValidForCreation(
            req: CreateTagRequest,
            repository: TagRepository
        ): Tag? {
            if (Encryptor.encrypt(req.password) != RootUserConfig.encryptedPassword
                || req.tagId.isEmpty()
                || req.name.isEmpty()
                || req.contentSortStrategy.isEmpty()
                || req.contentRenderType.isEmpty()
            ) {
                return null
            }

            //check if tag with specified id already exists
            val allTags = repository.getEntities()
            if (
                allTags.exists { it.tagId == req.tagId }
            ) {
                return null
            }

            return Tag(
                tagId = req.tagId,
                name = req.name,
                contentSortStrategy = req.contentSortStrategy,
                showAsTag = req.showAsTag,
                contentRenderType = req.contentRenderType,
                showContentCount = req.showContentCount,
                headerIndex = req.headerIndex,
                infoContentId = null
            )
        }

        fun createIfValidForUpdate(
            tagId: String,
            req: UpdateTagRequest,
            repository: TagRepository
        ): Tag? {
            if (Encryptor.encrypt(req.password) != RootUserConfig.encryptedPassword
            ) {
                return null
            }

            val tag: Tag = repository.findEntity(tagId)!!

            return tag.copy(
                infoContentId = if(req.infoContentId.isEmpty()) null else req.infoContentId.toInt()
            )
        }
    }
}