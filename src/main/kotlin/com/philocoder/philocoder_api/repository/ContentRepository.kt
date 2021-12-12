package com.philocoder.philocoder_api.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import com.philocoder.philocoder_api.model.entity.Content
import com.philocoder.philocoder_api.model.entity.Tag
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository


@Repository
open class ContentRepository(
    client: RestHighLevelClient,
    @Qualifier("contentObjectReader") objectReader: ObjectReader,
    objectMapper: ObjectMapper
) : BaseRepository<Content>(client, objectReader, objectMapper) {

    override val indexName: String
        get() = "contents"

    override val entityKey: String
        get() = "contentId"

    fun getContentsForTag(page: Int, size: Int, blogMode: Boolean, tag: Tag): List<Content> {
        return getEntities(
            page = page,
            size = size,
            queryBuilder = if (!blogMode) QueryBuilders.matchQuery("tags", tag.name)
            else QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("tags", tag.name))
                .must(QueryBuilders.termQuery("okForBlogMode", true)),
            sorter = tag.contentSorter
        )
    }

    fun getContentCount(tagName: String, blogMode: Boolean): Int {
        return if (!blogMode) getTotalEntityCount(QueryBuilders.matchQuery("tags", tagName))
        else getTotalEntityCount(
            QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("tags", tagName))
                .must(QueryBuilders.termQuery("okForBlogMode", true))
        )
    }
}


