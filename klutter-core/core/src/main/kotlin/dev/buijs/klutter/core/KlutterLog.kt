/* Copyright (c) 2021 - 2022 Buijs Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */


package dev.buijs.klutter.core


/**
 * Wrapper class to collect and merge logging messages so they can be
 * filtered, decorated and/or outputted to a console.
 *
 * @author Gillian Buijs
 */
open class KlutterLogger {

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

    fun merge(logger: KlutterLogger): KlutterLogger {
        _messages.addAll(logger.messages())
        return this
    }

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