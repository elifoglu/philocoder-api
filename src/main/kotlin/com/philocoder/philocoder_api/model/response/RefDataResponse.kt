package com.philocoder.philocoder_api.model.response

import arrow.core.Tuple2
import com.philocoder.philocoder_api.model.ContentID

data class RefDataResponse(
    val titlesToShow: List<String>,
    val contentIds: List<ContentID>,
    val connections: List<Tuple2<Int, Int>>
)