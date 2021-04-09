package com.philocoder.philocoder_api.model.entity

import arrow.core.Tuple2
import com.fasterxml.jackson.annotation.JsonIgnore
import org.elasticsearch.search.sort.SortOrder

data class Tag(
    val tagId: String,
    val name: String,
    val contentSortStrategy: String,
    val showAsTag: Boolean,
    val contentRenderType: String,
    val showContentCount: Boolean,
    val showInHeader: Boolean,
    val infoContentId: Int?
) {

    @JsonIgnore
    val contentSorter: Tuple2<String, SortOrder> = Tuple2(
        "dateAsTimestamp",
        if (contentSortStrategy == "DateASC") SortOrder.ASC else SortOrder.DESC
    )
}