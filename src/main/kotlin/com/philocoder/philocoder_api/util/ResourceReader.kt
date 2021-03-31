package com.philocoder.philocoder_api.util

import com.philocoder.philocoder_api.model.ContentID
import java.nio.file.Files
import java.nio.file.Paths


object ResourceReader {

    private const val contentDirectoryName = "content"
    private const val jsonFileName = "data.json"

    fun readBaseDataFile(): String {
        val url = this::class.java.getResource("/$contentDirectoryName/$jsonFileName")
        return String(Files.readAllBytes(Paths.get(url.toURI())))
    }

    fun readContentText(contentID: ContentID): String {
        val url = this::class.java.getResource("/$contentDirectoryName/$contentID")
        return String(Files.readAllBytes(Paths.get(url.toURI())))
    }
}