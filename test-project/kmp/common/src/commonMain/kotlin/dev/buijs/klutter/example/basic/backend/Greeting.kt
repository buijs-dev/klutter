package dev.buijs.klutter.example.basic.backend

import dev.buijs.klutter.annotations.kmp.KlutterAdaptee

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

@KlutterResponse
open class ExtensiveGreetingInfo(
    val info: String,
    val type: GreetingType
)

enum class GreetingType {
    LONG, VERY_LONG, EXHAUSTIVE, LEAVE_ME_ALONE
}