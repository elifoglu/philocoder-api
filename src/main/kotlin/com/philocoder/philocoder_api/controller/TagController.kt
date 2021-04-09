package com.philocoder.philocoder_api.controller

import com.fasterxml.jackson.databind.ObjectReader
import com.philocoder.philocoder_api.model.entity.Content
import com.philocoder.philocoder_api.model.entity.Tag
import com.philocoder.philocoder_api.model.request.CreateContentRequest
import com.philocoder.philocoder_api.model.request.CreateTagRequest
import com.philocoder.philocoder_api.model.request.TagsRequest
import com.philocoder.philocoder_api.model.response.ContentResponse
import com.philocoder.philocoder_api.model.response.TagResponse
import com.philocoder.philocoder_api.repository.ContentRepository
import com.philocoder.philocoder_api.repository.TagRepository
import com.philocoder.philocoder_api.util.JsonToESEntityIndexer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.*


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

    @CrossOrigin
    @PostMapping("/tags")
    fun addTag(@RequestBody req: CreateTagRequest): String =
        Tag.createIfValidForCreation(req, repository)!!
            .run {
                repository.addEntity(tagId, this)
                "created"
            }

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