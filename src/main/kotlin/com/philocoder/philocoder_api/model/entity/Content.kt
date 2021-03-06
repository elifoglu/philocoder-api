package com.philocoder.philocoder_api.model.entity

import arrow.core.extensions.list.foldable.exists
import arrow.core.extensions.list.foldable.forAll
import com.philocoder.philocoder_api.model.ContentID
import com.philocoder.philocoder_api.model.request.ContentRequest
import com.philocoder.philocoder_api.model.request.CreateContentRequest
import com.philocoder.philocoder_api.model.request.UpdateContentRequest
import com.philocoder.philocoder_api.repository.ContentRepository
import com.philocoder.philocoder_api.repository.TagRepository
import com.philocoder.philocoder_api.util.Encryptor
import com.philocoder.philocoder_api.util.RootUserConfig
import java.util.*

data class Content(
    val title: String?,
    val contentId: ContentID,
    val content: String?,
    val tags: List<String>,
    val refs: List<ContentID>?,
    val dateAsTimestamp: Long,
    val okForBlogMode: Boolean
) {

    companion object {
        fun createIfValidForCreation(
            req: CreateContentRequest,
            contentRepository: ContentRepository,
            tagRepository: TagRepository
        ): Content? {
            if (Encryptor.encrypt(req.password) != RootUserConfig.encryptedPassword
                || req.id.isEmpty()
                || req.text.isEmpty()
                || req.tags.isEmpty()
            ) {
                return null
            }

            //check if every entered tag name exists
            val tagNames = req.tags.split(",")
            val allTags = tagRepository.getEntities()
            val allTagNamesExists = tagNames.forAll { tagName ->
                allTags.exists { it.name == tagName }
            }
            if (!allTagNamesExists) {
                return null
            }

            //check if content with specified id already exists
            val allContents = contentRepository.getEntities()
            if (
                allContents.exists { it.contentId == req.id.toInt() }
            ) {
                return null
            }

            //check if every entered ref id exists
            val refs = if (req.refs.isNullOrEmpty()) null else {
                val refIds = req.refs.split(",").map { it.toInt() }
                val allRefIdsExists = refIds.forAll { refId ->
                    allContents.exists { it.contentId == refId }
                }
                if (!allRefIdsExists) {
                    return null
                }
                refIds
            }

            return Content(
                title = if (req.title.isNullOrEmpty()) null else req.title,
                contentId = req.id.toInt(),
                content = req.text,
                tags = tagNames,
                refs = refs,
                dateAsTimestamp = Calendar.getInstance().timeInMillis,
                okForBlogMode = req.okForBlogMode
            )
        }

        fun createIfValidForPreview(
            contentId: ContentID,
            req: ContentRequest,
            contentRepository: ContentRepository,
            tagRepository: TagRepository
        ): Content? {
            if (Encryptor.encrypt(req.password) != RootUserConfig.encryptedPassword
                || req.text.isEmpty()
                || req.tags.isEmpty()
            ) {
                return null
            }

            //check if every entered tag name exists
            val tagNames = req.tags.split(",")
            val allTags = tagRepository.getEntities()
            val allTagNamesExists = tagNames.forAll { tagName ->
                allTags.exists { it.name == tagName }
            }
            if (!allTagNamesExists) {
                return null
            }

            //check if every entered ref id exists
            val allContents = contentRepository.getEntities()
            val refs = if (req.refs.isNullOrEmpty()) null else {
                val refIds = req.refs!!.split(",").map { it.toInt() }
                val allRefIdsExists = refIds.forAll { refId ->
                    allContents.exists { it.contentId == refId }
                }
                if (!allRefIdsExists) {
                    return null
                }
                refIds
            }

            return Content(
                title = if (req.title.isNullOrEmpty()) null else req.title,
                contentId = contentId,
                content = req.text,
                tags = tagNames,
                refs = refs,
                dateAsTimestamp = Calendar.getInstance().timeInMillis,
                okForBlogMode = req.okForBlogMode
            )
        }


        fun createIfValidForUpdate(
            contentId: ContentID,
            req: UpdateContentRequest,
            contentRepository: ContentRepository,
            tagRepository: TagRepository
        ): Content? {
            if (Encryptor.encrypt(req.password) != RootUserConfig.encryptedPassword
                || contentId.toString().isEmpty()
                || req.text.isEmpty()
                || req.tags.isEmpty()
            ) {
                return null
            }

            //check if every entered tag name exists
            val tagNames = req.tags.split(",")
            val allTags = tagRepository.getEntities()
            val allTagNamesExists = tagNames.forAll { tagName ->
                allTags.exists { it.name == tagName }
            }
            if (!allTagNamesExists) {
                return null
            }

            //check if content with specified id not exists
            val existingContent: Content = contentRepository.findEntity(contentId.toString())
                ?: return null

            //check if every entered ref id exists
            val allContents = contentRepository.getEntities()
            val refs = if (req.refs.isNullOrEmpty()) null else {
                val refIds = req.refs.split(",").map { it.toInt() }
                val allRefIdsExists = refIds.forAll { refId ->
                    allContents.exists { it.contentId == refId }
                }
                if (!allRefIdsExists) {
                    return null
                }
                refIds
            }

            return Content(
                title = if (req.title.isNullOrEmpty()) null else req.title,
                contentId = contentId,
                content = req.text,
                tags = tagNames,
                refs = refs,
                dateAsTimestamp = existingContent.dateAsTimestamp,
                okForBlogMode = req.okForBlogMode
            )
        }
    }
}