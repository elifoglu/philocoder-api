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

    //This property is being indexed to elasticsearch, so it being used. Don't delete it.
    val dateAsTimestamp: Long =
        if (date.year != null && date.month != null && date.day != null) {
            val calendar = Calendar.getInstance()
            calendar.set(date.year, date.month - 1, date.day, date.publishOrderInDay, 0)
            calendar.timeInMillis
        } else {
            val calendar = Calendar.getInstance()
            calendar.set(2000, 0, 1, date.publishOrderInDay, 0)
            calendar.timeInMillis
        }
}