package dev.buijs.klutter

import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

class MethodChannelFacade(
    private val handler: MethodCallHandler,
    private val messenger: BinaryMessenger,
    channels: Set<String>
) {

    /**
     * Create MethodChannel instances and start listening.
     */
    private val methodChannels = channels.map { name ->
        val channel = MethodChannel(messenger, name)
        channel.setMethodCallHandler(handler)
        channel
    }

    fun cancel() {
        for (channel in methodChannels) {
            channel.setMethodCallHandler(null)
        }
    }

}