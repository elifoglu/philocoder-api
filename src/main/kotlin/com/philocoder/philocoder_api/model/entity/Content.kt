package com.philocoder.philocoder_api.model.entity

import arrow.core.extensions.list.foldable.exists
import arrow.core.extensions.list.foldable.forAll
import com.philocoder.philocoder_api.model.ContentDate
import com.philocoder.philocoder_api.model.ContentID
import com.philocoder.philocoder_api.model.request.CreateContentRequest
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
    val refs: List<ContentID>?
) {

    //This property is being indexed to elasticsearch, so it being used. Don't delete it.
    val dateAsTimestamp: Long =
        Calendar.getInstance()
            .also {
                if (date.year != null && date.month != null && date.day != null) {
                    it.set(date.year, date.month - 1, date.day, date.publishOrderInDay, 0)
                } else {
                    it.set(2000, 0, 1, date.publishOrderInDay, 0)
                }
            }
            .let { it.timeInMillis }

    companion object {
        fun fromRequest(
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
            if(
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
                if (!allRefIdsExists) null else refIds
            }

            //
            //ref id girilen her content var mı kontrol et
            //val refs = if (req.refs.isNullOrEmpty()) null else req.refs

            return Content(
                title = if (req.title.isNullOrEmpty()) null else req.title,
                date = ContentDate(
                    day = if (req.date.isNullOrEmpty()) null else req.date.split(".")[0].toInt(),
                    month = if (req.date.isNullOrEmpty()) null else req.date.split(".")[1].toInt(),
                    year = if (req.date.isNullOrEmpty()) null else req.date.split(".")[2].toInt(),
                    publishOrderInDay = req.publishOrderInDay.toInt()
                ),
                contentId = req.id.toInt(),
                content = req.text,
                tags = tagNames,
                refs = refs
            )
        }
    }
}