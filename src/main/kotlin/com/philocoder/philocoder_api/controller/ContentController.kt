package com.philocoder.philocoder_api.controller

import com.fasterxml.jackson.databind.ObjectReader
import com.philocoder.philocoder_api.model.entity.Content
import com.philocoder.philocoder_api.model.request.ContentsOfTagRequest
import com.philocoder.philocoder_api.model.response.ContentsResponse
import com.philocoder.philocoder_api.repository.ContentRepository
import com.philocoder.philocoder_api.service.ContentService
import com.philocoder.philocoder_api.util.JsonToESEntityIndexer
import com.philocoder.philocoder_api.util.ResourceReader
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


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
        JsonToESEntityIndexer.indexFromJsonArrayField(
            jsonArrayFieldName = "allContents",
            objectReader = objectReader,
            repoToIndex = repository
        ) { it.contentId.toString() }
        repository.getEntities()
            .map { content -> content.copy(content = ResourceReader.readContentText(content.contentId)) }
            .forEach { repository.addEntity(it.contentId.toString(), it) }
    }
}