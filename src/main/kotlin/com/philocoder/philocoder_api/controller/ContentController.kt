package com.philocoder.philocoder_api.controller

import arrow.core.Tuple2
import com.fasterxml.jackson.databind.ObjectReader
import com.philocoder.philocoder_api.model.entity.Content
import com.philocoder.philocoder_api.model.request.ContentsOfTagRequest
import com.philocoder.philocoder_api.model.request.CreateContentRequest
import com.philocoder.philocoder_api.model.request.UpdateContentRequest
import com.philocoder.philocoder_api.model.response.ContentResponse
import com.philocoder.philocoder_api.model.response.ContentsResponse
import com.philocoder.philocoder_api.repository.ContentRepository
import com.philocoder.philocoder_api.repository.TagRepository
import com.philocoder.philocoder_api.service.ContentService
import com.philocoder.philocoder_api.util.JsonToESEntityIndexer
import com.philocoder.philocoder_api.util.ResourceReader
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.*


@RestController
class ContentController(
    private val repository: ContentRepository,
    private val tagRepository: TagRepository,
    private val service: ContentService,
    @Qualifier("contentObjectReader") private val objectReader: ObjectReader
) {

    @CrossOrigin
    @GetMapping("/contents")
    fun get(req: ContentsOfTagRequest): ContentsResponse =
        service.getContentsResponse(req)

    @CrossOrigin
    @GetMapping("/content-refs")
    fun getRefs(): List<Tuple2<Int, Int>> =
        service.getAllReferenceData()

    @CrossOrigin
    @GetMapping("/contents/{contentId}")
    fun find(@PathVariable("contentId") contentId: String): ContentResponse =
        ContentResponse.createWith(repository.findEntity(contentId)!!, repository)

    @CrossOrigin
    @PostMapping("/contents")
    fun addContent(@RequestBody req: CreateContentRequest): ContentResponse =
        Content.createIfValidForCreation(req, repository, tagRepository)!!
            .apply {
                repository.addEntity(contentId.toString(), this)
            }
            .let { ContentResponse.createWith(it, repository) }

    @CrossOrigin
    @PostMapping("/contents/{contentId}")
    fun updateContent(
        @PathVariable("contentId") contentId: String,
        @RequestBody req: UpdateContentRequest
    ): ContentResponse =
        Content.createIfValidForUpdate(contentId.toInt(), req, repository, tagRepository)!!
            .apply {
                repository.deleteEntity(contentId)
                Thread.sleep(1000)
                repository.addEntity(contentId, this)
            }
            .let { ContentResponse.createWith(it, repository) }

    @CrossOrigin
    @PostMapping("/previewContent")
    fun previewContent(@RequestBody req: CreateContentRequest): ContentResponse =
        Content.createIfValidForPreview(req.id.toInt(), req, repository, tagRepository)!!
            .let { ContentResponse.createWith(it, repository) }

    @GetMapping("/delete-all-contents")
    fun delete(): Unit =
        repository.deleteAll()

    @GetMapping("/add-all-contents")
    fun addAll() {
        JsonToESEntityIndexer.indexFromJsonArrayField(
            jsonArrayFieldName = "allContents",
            objectReader = objectReader,
            repoToIndex = repository
        ) { it.contentId.toString() }
        Thread.sleep(1000);
        repository.getEntities()
            .map { content -> content.copy(content = ResourceReader.readContentText(content.contentId)) }
            .forEach { repository.addEntity(it.contentId.toString(), it) }
    }
}