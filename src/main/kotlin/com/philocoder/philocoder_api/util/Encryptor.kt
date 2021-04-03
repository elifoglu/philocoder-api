package com.philocoder.philocoder_api.util

import java.security.MessageDigest
import java.util.*


object Encryptor {

    fun encrypt(password: String): String {
        val m = MessageDigest.getInstance("MD5")
        val value: ByteArray = password.encodeToByteArray()
        val a = m.digest(value)
        return Base64.getEncoder().encodeToString(a)
    }
}
