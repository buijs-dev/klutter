package dev.buijs.klutter

import dev.buijs.klutter.annotations.KlutterJSON
import dev.buijs.klutter.annotations.KlutterResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
@KlutterResponse
open class Something(
    val x: String?,
    val y: SomethingElse
): KlutterJSON<Something>() {

    override fun data() = this

    override fun strategy() = serializer<Something>()

}

@Serializable
@KlutterResponse
open class SomethingElse(
    val a: Int?,
    val b: List<Boolean>
): KlutterJSON<SomethingElse>() {

    override fun data() = this

    override fun strategy() = serializer<SomethingElse>()

}

@Serializable
@KlutterResponse
open class SomeMap(
    val a: Int?,
    val b: Map<String, Int>
): KlutterJSON<SomeMap>() {

    override fun data() = this

    override fun strategy() = serializer<SomeMap>()

}