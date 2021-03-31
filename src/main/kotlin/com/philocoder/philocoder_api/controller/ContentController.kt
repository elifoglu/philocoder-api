package com.philocoder.philocoder_api.controller

import com.fasterxml.jackson.databind.ObjectReader
import com.philocoder.philocoder_api.model.entity.Content
import com.philocoder.philocoder_api.model.request.ContentsOfTagRequest
import com.philocoder.philocoder_api.model.response.ContentsResponse
import com.philocoder.philocoder_api.repository.ContentRepository
import com.philocoder.philocoder_api.service.ContentService
import com.philocoder.philocoder_api.service.DataInsertionService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.*


@RestController
class ContentController(
    private val repository: ContentRepository,
    private val service: ContentService,
    @Qualifier("contentObjectReader") private val objectReader: ObjectReader
) {

    @CrossOrigin
    @GetMapping("/contents")
    fun get(req: ContentsOfTagRequest): ContentsResponse {
        return service.getContentsResponse(req)
    }

    @CrossOrigin
    @GetMapping("/contents/{contentId}")
    fun find(@PathVariable("contentId") contentId: String): Content? {
        return repository.findEntity(contentId)
    }

    @GetMapping("/delete-all-contents")
    fun delete(): Unit =
        repository.deleteAll()

    @GetMapping("/add-all-contents")
    fun addAll() {
        DataInsertionService.addAll("allContents", objectReader, repository) { it.contentId.toString() }
        val contents: List<Content> = repository.getAllEntities()
        contents
            .map { content -> content.copy(content = DataInsertionService.getContentText(content.contentId)) }
            .forEach { repository.addEntity(it.contentId.toString(), it) }
    }
}