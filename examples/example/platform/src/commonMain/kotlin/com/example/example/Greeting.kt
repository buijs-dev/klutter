package com.example.example

import dev.buijs.klutter.annotations.kmp.KlutterAdaptee
import dev.buijs.klutter.annotations.kmp.KlutterJSON
import dev.buijs.klutter.annotations.kmp.KlutterResponse
import com.example.example.Platform
import kotlinx.serialization.*

class Greeting {

    @KlutterAdaptee(name = "getGreeting")
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }

}

class ExtensiveGreeting {

    @KlutterAdaptee(name = "getExtensiveGreeting")
    fun greeting(): ExtensiveGreetingInfo {
        return ExtensiveGreetingInfo(
            "Just want to say hi!",
            GreetingType.LONG
        )
    }

}

@Serializable
@KlutterResponse
open class ExtensiveGreetingInfo(
    val info: String,
    val type: GreetingType
): KlutterJSON<ExtensiveGreetingInfo>() {

    override fun data() = this

    override fun strategy() = serializer()

}


enum class GreetingType {
    LONG, VERY_LONG, EXHAUSTIVE, LEAVE_ME_ALONE
}