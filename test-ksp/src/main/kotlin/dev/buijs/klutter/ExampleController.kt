package dev.buijs.klutter

import dev.buijs.klutter.annotations.Controller
import dev.buijs.klutter.annotations.KlutterAdaptee
import dev.buijs.klutter.annotations.KlutterJSON
import dev.buijs.klutter.annotations.KlutterResponse
import dev.buijs.klutter.kompose.KlutterBroadcast
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer

@Controller
class MySimpleController {

    @KlutterAdaptee(name = "foo")
    fun foo() = "bar"

}

@Controller
class MyBroadcastController: KlutterBroadcast<MyResponse>() {

    override suspend fun broadcast() {
        TODO("Not yet implemented")
    }

}

@Serializable
@KlutterResponse
open class MyResponse(
    val x: String?,
    val y: Int,
): KlutterJSON<MyResponse>() {

    val x2 = ""

    var y2 = 0

    override fun data() = this

    override fun strategy() = serializer<MyResponse>()

}