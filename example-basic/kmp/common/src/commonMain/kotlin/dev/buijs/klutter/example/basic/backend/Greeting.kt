package dev.buijs.klutter.example.basic.backend

import dev.buijs.klutter.annotations.kmp.KlutterAdaptee

@KlutterAdaptee(name = "getGreeting")
class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}