package dev.buijs.klutter.example.basic.backend

import dev.buijs.klutter.annotations.kmp.KlutterAdaptee

class Greeting {

    @KlutterAdaptee(name = "getGreeting")
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}