package com.philocoder.philocoder_api.repository

import com.fasterxml.jackson.databind.ObjectReader
import com.philocoder.philocoder_api.model.entity.Content
import com.philocoder.philocoder_api.model.request.ContentsOfTagRequest
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository


@Repository
open class ContentRepository(
    client: RestHighLevelClient,
    @Qualifier("contentObjectReader") objectReader: ObjectReader,
) : BaseRepository<Content>(client, objectReader) {

    override val indexName: String
        get() = "contents"

    override val entityKey: String
        get() = "contentId"

    fun getContentsForTag(page: Int, size: Int, tagName: String): List<Content> {
        return getEntities(page, size, QueryBuilders.matchQuery("tags", tagName))
    }

    fun getContentCount(tagName: String): Int {
        return getTotalEntityCount(QueryBuilders.matchQuery("tags", tagName))
    }
}


