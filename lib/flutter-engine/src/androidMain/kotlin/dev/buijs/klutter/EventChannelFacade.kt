package dev.buijs.klutter

import dev.buijs.klutter.kompose.Publisher
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EventChannelFacade(
    private val handler: EventChannel.StreamHandler,
    private val messenger: BinaryMessenger,
    channels: Set<String>
) {

    /**
     * Create EventChannel instances and start listening.
     */
    private val eventChannels = channels.map { name ->
        val channel = EventChannel(messenger, name)
        channel.setStreamHandler(handler)
        channel
    }

    fun cancel() {
        for (channel in eventChannels) {
            channel.setStreamHandler(null)
        }
    }
}

fun registerEventSink(
    publisher: Publisher<*>,
    subscriber: EventChannel.EventSink
): Job = publisher.scope.launch {
    publisher.receiveBroadcastAndroid()
        .collect { subscriber.success(it.encode())}
}
