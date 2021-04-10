package com.philocoder.philocoder_api.model.entity

import arrow.core.extensions.list.foldable.exists
import arrow.core.extensions.list.foldable.forAll
import com.philocoder.philocoder_api.model.ContentDate
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
    val date: ContentDate,
    val contentId: ContentID,
    val content: String?,
    val tags: List<String>,
    val refs: List<ContentID>?,
    val dateAsTimestamp: Long
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
                || req.publishOrderInDay.isEmpty()
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

            val date = ContentDate(
                day = if (req.date.isNullOrEmpty()) null else req.date.split(".")[0].toInt(),
                month = if (req.date.isNullOrEmpty()) null else req.date.split(".")[1].toInt(),
                year = if (req.date.isNullOrEmpty()) null else req.date.split(".")[2].toInt(),
                publishOrderInDay = req.publishOrderInDay.toInt())

            return Content(
                title = if (req.title.isNullOrEmpty()) null else req.title,
                date = date,
                contentId = req.id.toInt(),
                content = req.text,
                tags = tagNames,
                refs = refs,
                dateAsTimestamp = Calendar.getInstance()
                    .also {
                        if (date.year == null || date.month == null || date.day == null) {
                            it.set(2000, 0, 1, date.publishOrderInDay, 0)
                        }
                    }
                    .let { it.timeInMillis }
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
                || req.publishOrderInDay.isEmpty()
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
                date = ContentDate(
                    day = if (req.date.isNullOrEmpty()) null else req.date!!.split(".")[0].toInt(),
                    month = if (req.date.isNullOrEmpty()) null else req.date!!.split(".")[1].toInt(),
                    year = if (req.date.isNullOrEmpty()) null else req.date!!.split(".")[2].toInt(),
                    publishOrderInDay = req.publishOrderInDay.toInt()
                ),
                contentId = contentId,
                content = req.text,
                tags = tagNames,
                refs = refs,
                dateAsTimestamp = Calendar.getInstance().timeInMillis
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
                || req.publishOrderInDay.isEmpty()
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
                date = ContentDate(
                    day = if (req.date.isNullOrEmpty()) null else req.date.split(".")[0].toInt(),
                    month = if (req.date.isNullOrEmpty()) null else req.date.split(".")[1].toInt(),
                    year = if (req.date.isNullOrEmpty()) null else req.date.split(".")[2].toInt(),
                    publishOrderInDay = req.publishOrderInDay.toInt()
                ),
                contentId = contentId,
                content = req.text,
                tags = tagNames,
                refs = refs,
                dateAsTimestamp = existingContent.dateAsTimestamp
            )
        }
    }
}