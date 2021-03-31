package com.philocoder.philocoder_api.model.entity

import arrow.core.Tuple2
import org.elasticsearch.search.sort.SortOrder

data class Tag(
    val tagId: String,
    val name: String,
    val contentSortStrategy: String,
    val showAsTag: Boolean,
    val contentRenderType: String,
    val showContentCount: Boolean,
    val showInHeader: Boolean
) {

    fun getContentSorter(): Tuple2<String, SortOrder> {
        return Tuple2(
            "dateAsTimestamp",
            if (contentSortStrategy == "DateASC") SortOrder.ASC else SortOrder.DESC
        )
    }
}