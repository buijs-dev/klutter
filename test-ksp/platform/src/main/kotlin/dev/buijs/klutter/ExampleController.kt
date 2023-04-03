package dev.buijs.klutter

import dev.buijs.klutter.annotations.*
import dev.buijs.klutter.kompose.Listener
import dev.buijs.klutter.kompose.Publisher
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Controller
class MySimpleController {

    @KlutterAdaptee(name = "foo")
    fun foo(): String? = null

    @KlutterAdaptee(name = "getChakka")
    suspend fun chakka() = MyResponse(x = "", y = 1)

    @KlutterAdaptee(name = "setChakka")
    fun setChakka(chakka: String) = MyResponse(x = chakka, y = 1)
}

@Controller
class MyBroadcastController: Publisher<MyResponse>() {

    override suspend fun publish() {
        emit(MyResponse(x = "hi!", y = 1))
    }

}

@Controller
class Counter: Publisher<Int>() {

    override suspend fun publish() {
        emit(1)
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

@Serializable
@KlutterResponse
enum class SomeValue(val x: String) {
    @SerialName("bla")
    BLA("bla!"),
    @SerialName("die")
    DIE("die!"),
    @SerialName("blabla")
    BLABLA("blabla!")
}