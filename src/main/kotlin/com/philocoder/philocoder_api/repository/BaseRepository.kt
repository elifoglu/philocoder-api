package com.philocoder.philocoder_api.repository

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import org.elasticsearch.action.get.GetRequest
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.core.CountRequest
import org.elasticsearch.client.core.CountResponse
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.index.reindex.DeleteByQueryRequest
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.builder.SearchSourceBuilder


abstract class BaseRepository<T>(
    private val client: RestHighLevelClient,
    private val objectReader: ObjectReader
) : HasIndexName, HasEntityKey {

    fun getAllEntities(): List<T> {
        return getEntities(1, 100000, QueryBuilders.matchAllQuery())
    }

    fun getEntities(page: Int, size: Int): List<T> {
        return getEntities(page, size, QueryBuilders.matchAllQuery())
    }

    fun findEntity(id: String): T? {
        val getRequest = GetRequest(indexName)
            .id(id)
        val get: GetResponse = client.get(getRequest, RequestOptions.DEFAULT)
        return if (!get.isExists) null
        else toEntity(get.sourceAsBytes!!).orNull()
    }


    fun getEntities(page: Int, size: Int, queryBuilder: QueryBuilder): List<T> {
        val searchRequest = SearchRequest(indexName)
        val searchSourceBuilder = SearchSourceBuilder()
            .query(queryBuilder)
            .from(size * (page - 1))
            .size(size)
            .trackTotalHits(true)
        searchRequest.source(searchSourceBuilder)
        val searchResponse = client.search(searchRequest, RequestOptions.DEFAULT)
        val hits = searchResponse.hits.hits
        return toEntities(hits)
    }

    fun getTotalEntityCount(queryBuilder: QueryBuilder): Int {
        val countRequest = CountRequest(indexName)
            .query(queryBuilder)
        val countResponse: CountResponse = client.count(countRequest, RequestOptions.DEFAULT)
        return countResponse.count.toInt()
    }

    fun addEntity(id: String, it: T) {
        val indexRequest = IndexRequest(indexName)
        indexRequest.id(id)
            .source(ObjectMapper().writeValueAsString(it), XContentType.JSON)
        val index = client.index(indexRequest, RequestOptions.DEFAULT)
    }

    fun deleteAll() {
        val deleteAllRequest = DeleteByQueryRequest(indexName)
        deleteAllRequest.setQuery(QueryBuilders.matchAllQuery())
        client.deleteByQuery(deleteAllRequest, RequestOptions.DEFAULT)
    }

    private fun toEntities(hits: Array<SearchHit>?): List<T> {
        return hits!!
            .map { toEntity(it) }
            .flatMap { it.toList() }
    }

    private fun toEntity(hit: SearchHit): Option<T> {
        val bytes = hit.sourceRef.toBytesRef().bytes
        return toEntity(bytes)
    }

    private fun toEntity(bytes: ByteArray): Option<T> {
        if (bytes.isEmpty()) return None
        return try {
            val content = objectReader.readValue<T>(bytes)
            Some(content)
        } catch (e: Exception) {
            None
        }
    }
}
