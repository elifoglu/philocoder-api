package com.philocoder.philocoder_api.controller

import com.philocoder.philocoder_api.model.request.TagsRequest
import com.philocoder.philocoder_api.model.response.TagResponse
import com.philocoder.philocoder_api.service.TagService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class TagController(
    private val service: TagService
) {

    @CrossOrigin
    @GetMapping("/tags")
    fun get(req: TagsRequest): List<TagResponse> =
        service.getTags(req)

    @GetMapping("/delete-all-tags")
    fun delete(): Unit =
        service.deleteAll()

    @GetMapping("/add-all-tags")
    fun addAll(): Unit =
        service.addAll()
}