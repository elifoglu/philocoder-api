package com.philocoder.philocoder_api.util

import com.philocoder.philocoder_api.model.ContentID
import org.apache.commons.io.IOUtils
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths


object ResourceReader {

    private const val contentDirectoryName = "content"
    private const val jsonFileName = "data.json"

    fun readBaseDataFile(): String {
        javaClass.getResourceAsStream("/$contentDirectoryName/$jsonFileName")
            .use { input ->
                val data = IOUtils.toByteArray(input)
                return String(data, StandardCharsets.UTF_8);
            }
    }

    fun readContentText(contentID: ContentID): String {
        javaClass.getResourceAsStream("/$contentDirectoryName/$contentID")
            .use { input ->
                val data = IOUtils.toByteArray(input)
                return String(data, StandardCharsets.UTF_8);
            }
    }
}