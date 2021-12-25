package dev.buijs.klutter.core.log


/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
open class KlutterLogging {

    private val _messages = mutableListOf<KlutterLogMessage>()

    fun debug(msg: String) {
        _messages.add(KlutterLogMessage(msg, KlutterLogLevel.INFORMATIVE))
    }

    fun info(msg: String) {
        _messages.add(KlutterLogMessage(msg, KlutterLogLevel.HAPPY_FEET))
    }

    fun warn(msg: String) {
        _messages.add(KlutterLogMessage(msg, KlutterLogLevel.NEEDS_ATTENTION))
    }

    fun error(msg: String) {
        _messages.add(KlutterLogMessage(msg, KlutterLogLevel.CALL_BATMAN))
    }

    fun messages() = _messages

    fun messages(debug: Boolean) = if(debug) _messages
    else _messages.filter { it.level != KlutterLogLevel.INFORMATIVE }

}

data class KlutterLogMessage(
    val message: String,
    val level: KlutterLogLevel
    )

enum class KlutterLogLevel {
    INFORMATIVE,
    NEEDS_ATTENTION,
    HAPPY_FEET,
    CALL_BATMAN
}