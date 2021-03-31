package com.philocoder.philocoder_api.service

import com.fasterxml.jackson.databind.ObjectReader
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.philocoder.philocoder_api.model.ContentID
import com.philocoder.philocoder_api.repository.BaseRepository
import java.nio.file.Files
import java.nio.file.Paths

object DataInsertionService {

    private const val contentPath = "/home/mert/Desktop/dev/workspace/m/philocoder-api/src/main/resources/content/"

    fun <T> addAll(jsonFieldName: String, objectReader: ObjectReader, repository: BaseRepository<T>, idExtractor: ((T) -> String)) {
        repository.deleteAll()
        val file = contentPath + "data.json"
        val json = String(Files.readAllBytes(Paths.get(file)))
        val g = Gson()
        val baseObject = g.fromJson(json, JsonObject::class.java)
        val entities: List<T> = baseObject.get(jsonFieldName)
            .asJsonArray
            .map { objectReader.readValue(it.toString()) }
        entities.forEach { repository.addEntity(idExtractor(it), it) }
    }

    fun getContentText(contentID: ContentID): String {
        val file = contentPath + contentID
        return String(Files.readAllBytes(Paths.get(file)))
    }
}