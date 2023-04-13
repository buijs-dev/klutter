package dev.buijs.klutter

import cocoapods.flutter_framework.FlutterEventChannel
import cocoapods.flutter_framework.FlutterMethodCall
import platform.darwin.NSObject

class EventChannelFacade(
    private val handler: NSObject,
    channels: Set<FlutterEventChannel>
) {

    /**
     * Create EventChannel instances and start listening.
     */
    private val eventChannels = channels.map { channel ->
        channel.setStreamHandler(handler)
        channel
    }

    fun cancel() {
        for (channel in eventChannels) {
            channel.setStreamHandler(null)
        }
    }

}