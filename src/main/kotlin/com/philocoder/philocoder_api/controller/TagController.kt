package com.philocoder.philocoder_api.controller

import com.fasterxml.jackson.databind.ObjectReader
import com.philocoder.philocoder_api.model.request.TagsRequest
import com.philocoder.philocoder_api.model.response.TagResponse
import com.philocoder.philocoder_api.repository.ContentRepository
import com.philocoder.philocoder_api.repository.TagRepository
import com.philocoder.philocoder_api.util.JsonToESEntityIndexer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class TagController(
    private val repository: TagRepository,
    private val contentRepository: ContentRepository,
    @Qualifier("tagObjectReader") private val objectReader: ObjectReader
) {

    @CrossOrigin
    @GetMapping("/tags")
    fun get(req: TagsRequest): List<TagResponse> =
        repository.getTags(req)
            .map { TagResponse.createWith(it, contentRepository) }

    @GetMapping("/delete-all-tags")
    fun delete(): Unit =
        repository.deleteAll()

    @GetMapping("/add-all-tags")
    fun addAll(): Unit =
        JsonToESEntityIndexer.indexFromJsonArrayField(
            jsonArrayFieldName = "allTags",
            objectReader = objectReader,
            repoToIndex = repository
        ) { it.tagId }
}