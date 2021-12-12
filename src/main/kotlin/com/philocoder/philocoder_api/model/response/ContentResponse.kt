package com.philocoder.philocoder_api.model.response

import com.philocoder.philocoder_api.model.ContentID
import com.philocoder.philocoder_api.model.entity.Content
import com.philocoder.philocoder_api.model.entity.Ref
import com.philocoder.philocoder_api.repository.ContentRepository
import org.joda.time.DateTime
import org.joda.time.Instant
import java.util.*

data class ContentResponse(
    val title: String?,
    val dateAsTimestamp: String,
    val contentId: ContentID,
    val content: String?,
    val tags: List<String>,
    val refs: List<Ref>?,
    val okForBlogMode: Boolean
) {

    companion object {
        fun createWith(content: Content, repo: ContentRepository): ContentResponse {
            val refs: List<Ref>? = content.refs
                ?.mapNotNull { id -> repo.findEntity(id.toString()) }
                ?.map(Ref.Companion::createWith)
                ?.distinctBy { it.id }
            return ContentResponse(
                title = content.title,
                dateAsTimestamp = content.dateAsTimestamp.toString(),
                contentId = content.contentId,
                content = content.content,
                tags = content.tags,
                refs = refs,
                okForBlogMode = content.okForBlogMode
            )
        }
    }
}