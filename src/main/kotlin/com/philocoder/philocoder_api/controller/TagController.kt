package com.philocoder.philocoder_api.controller

import com.fasterxml.jackson.databind.ObjectReader
import com.philocoder.philocoder_api.model.entity.Tag
import com.philocoder.philocoder_api.model.request.*
import com.philocoder.philocoder_api.model.response.TagDataResponse
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
    fun get(req: TagsRequest): TagDataResponse
    {
        val allTags: List<Tag> = repository.getTags(req)
        return TagDataResponse(
            allTags = allTags.map { TagResponse.createForAllContentsMode(it, contentRepository) }
                .filter { it.contentCount != 0 },
            blogModeTags = allTags.map { TagResponse.createForBlogMode(it, contentRepository) }
                .filter { it.contentCount != 0 }
        )
    }

    @CrossOrigin
    @PostMapping("/tags")
    fun addTag(@RequestBody req: CreateTagRequest): String =
        Tag.createIfValidForCreation(req, repository)!!
            .run {
                repository.addEntity(tagId, this)
                "done"
            }

    @CrossOrigin
    @PostMapping("/tags/{tagId}")
    fun updateContent(
        @PathVariable("tagId") tagId: String,
        @RequestBody req: UpdateTagRequest
    ): String =
        Tag.createIfValidForUpdate(tagId, req, repository)!!
            .run {
                repository.deleteEntity(tagId)
                Thread.sleep(1000)
                repository.addEntity(tagId, this)
                "done"
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