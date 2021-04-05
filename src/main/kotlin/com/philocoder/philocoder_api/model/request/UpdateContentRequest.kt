package com.philocoder.philocoder_api.model.request

data class UpdateContentRequest(
    val id: Int?,
    override val title: String?,
    override val text: String,
    override val date: String?,
    override val publishOrderInDay: String,
    override val tags: String,
    override val refs: String?,
    override val password: String
) : ContentRequest