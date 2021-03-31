package com.philocoder.philocoder_api.util

import com.fasterxml.jackson.databind.ObjectReader
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.philocoder.philocoder_api.repository.BaseRepository


object JsonToESEntityIndexer {

    fun <T> indexFromJsonArrayField(
        jsonArrayFieldName: String,
        objectReader: ObjectReader,
        repoToIndex: BaseRepository<T>,
        idExtractor: ((T) -> String)
    ) {
        repoToIndex.deleteAll()
        val json = ResourceReader.readBaseDataFile()
        val g = Gson()
        val baseObject = g.fromJson(json, JsonObject::class.java)
        val entities: List<T> = baseObject.get(jsonArrayFieldName)
            .asJsonArray
            .map { objectReader.readValue(it.toString()) }
        entities.forEach { repoToIndex.addEntity(idExtractor(it), it) }
    }
}