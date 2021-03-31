package com.philocoder.philocoder_api.model.entity

import com.philocoder.philocoder_api.model.ContentDate
import com.philocoder.philocoder_api.model.ContentID
import java.util.*

data class Content(
    val title: String?,
    val date: ContentDate,
    val contentId: ContentID,
    val content: String?,
    val tags: List<String>,
    val refs: List<ContentID>?
) {

    //this property is being indexed to elasticsearch
    val dateAsTimestamp: Long? =
        if (date.year != null && date.month != null && date.day != null) {
            val calendar = Calendar.getInstance()
            calendar.set(date.year, date.month - 1, date.day, date.publishOrderInDay, 0)
            calendar.timeInMillis
        } else null
}