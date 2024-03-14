package dev.buijs.klutter.kore.tasks.codegen

import spock.lang.Specification

import java.nio.file.Files

class GenerateProtoExtensionsTaskSpec extends Specification {

    def "Verify protof extension methods are generated correctly"() {

        given:
        def source = Files.createTempDirectory("").toFile()
        def kotlin = source.toPath().resolve("kotlin/com/example/my_plugin/platform").toFile()
        kotlin.mkdirs()

        when:
        new GenerateProtoExtensionsTask(source, ["com.example.my_plugin.platform.MyGreeting"]).run()

        then:
        def generatedFile = kotlin.toPath().resolve("\$protogenMyGreeting.kt").toFile()
        generatedFile.exists()
        println(generatedFile.text)
        generatedFile.text == """package com.example.my_plugin.platform

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
fun MyGreeting.encodeToByteArray(): ByteArray =
    ProtoBuf.encodeToByteArray(this)

@OptIn(ExperimentalSerializationApi::class)
fun decodeByteArrayToMyGreeting(byteArray: ByteArray): MyGreeting =
    ProtoBuf.decodeFromByteArray(byteArray)"""

    }

}
