package com.philocoder.philocoder_api.model.request

data class CreateContentRequest(
    val id: String,
    override val title: String?,
    override val text: String,
    override val tags: String,
    override val refs: String?,
    override val password: String,
    override val okForBlogMode: Boolean
): ContentRequest