package dev.buijs.klutter.kore.ast

sealed interface FlutterChannel {
    val name: String
}

data class FlutterAsyncChannel(
    override val name: String
): FlutterChannel

data class FlutterSyncChannel(
    override val name: String
): FlutterChannel