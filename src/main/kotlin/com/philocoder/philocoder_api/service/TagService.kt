package com.philocoder.philocoder_api.service

import com.fasterxml.jackson.databind.ObjectReader
import com.philocoder.philocoder_api.model.request.TagsRequest
import com.philocoder.philocoder_api.model.response.TagResponse
import com.philocoder.philocoder_api.repository.ContentRepository
import com.philocoder.philocoder_api.repository.TagRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service


@Service
class TagService(
    private val tagRepository: TagRepository,
    private val contentRepository: ContentRepository,
    @Qualifier("tagObjectReader") private val objectReader: ObjectReader
) {

    fun getTags(req: TagsRequest): List<TagResponse> {
        return tagRepository.getTags(req)
            .map { TagResponse.createWith(it, contentRepository) }
    }

    fun deleteAll() {
        tagRepository.deleteAll()
    }

    fun addAll() {
        DataInsertionService.addAll("allTags", objectReader, tagRepository) { it.tagId }
    }
}