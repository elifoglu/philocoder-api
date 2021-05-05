package com.philocoder.philocoder_api.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import com.philocoder.philocoder_api.model.entity.Tag
import com.philocoder.philocoder_api.model.request.TagsRequest
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
open class TagRepository(
    client: RestHighLevelClient,
    @Qualifier("tagObjectReader") objectReader: ObjectReader,
    objectMapper: ObjectMapper
) : BaseRepository<Tag>(client, objectReader, objectMapper) {

    override val indexName: String
        get() = "tags"

    override val entityKey: String
        get() = "tagId"

    fun getTags(req: TagsRequest): List<Tag> {
        return (if (req.onlyHeaderTags)
            getEntities(req.page, req.size, QueryBuilders.existsQuery("headerIndex"))
        else
            getEntities(req.page, req.size))
            .sortedWith { a, b ->
                when {
                    a.headerIndex == null -> 1
                    b.headerIndex == null -> -1
                    else -> a.headerIndex - b.headerIndex
                }
            }
    }
}
