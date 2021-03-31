package com.philocoder.philocoder_api.service

import com.philocoder.philocoder_api.model.entity.Tag
import com.philocoder.philocoder_api.model.request.ContentsOfTagRequest
import com.philocoder.philocoder_api.model.response.ContentResponse
import com.philocoder.philocoder_api.model.response.ContentsResponse
import com.philocoder.philocoder_api.repository.ContentRepository
import com.philocoder.philocoder_api.repository.TagRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ContentService(
    private val repository: ContentRepository,
    private val tagRepository: TagRepository
) {

    fun getContentsResponse(req: ContentsOfTagRequest): ContentsResponse {
        val tag: Tag = tagRepository.findEntity(req.tagId)
            ?: return ContentsResponse.empty
        val contentResponses = repository.getContentsForTag(req.page, req.size, tag)
            .map { ContentResponse.createWith(it, repository) }
        val contentCount = repository.getContentCount(tag.name)
        val totalPageCount =
            if (contentCount % req.size == 0) contentCount / req.size else (contentCount / req.size) + 1
        return ContentsResponse(totalPageCount, contentResponses)
    }
}