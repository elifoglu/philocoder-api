package com.philocoder.philocoder_api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class PhilocoderApiApp

fun main(args: Array<String>) {
    print("hi!")
    runApplication<PhilocoderApiApp>(*args)
}