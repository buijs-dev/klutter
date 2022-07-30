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
package dev.buijs.klutter.ui

import dev.buijs.klutter.ui.event.NoEvent

/**
 *
 */
class KomposeAppBar(
    val leading: Kompose<*>?,
    val middle: Kompose<*>?,
    val trailing: Kompose<*>?,
    val backgroundColor: Long?,
    val automaticallyImplyLeading: Boolean?,
): KomposeWithoutChildren<NoEvent>(event = NoEvent::class) {

    fun asCode(): String {
        return """
            |KomposeAppBar(
            |  leading: ${leading.toLeading()},
            |  middle: ${middle.toMiddle()},
            |  trailing: ${trailing.toTrailing()},
            |  backgroundColor: ${backgroundColor.toBackgroundColor()},
            |  automaticallyImplyLeading: ${automaticallyImplyLeading.toAutomaticallyImplyLeading()},
            |);
        """.trimMargin()
    }

    private fun Kompose<*>?.toLeading(): String {
        return ""
        //return this?.asCode() ?: "null"
    }

    private fun Kompose<*>?.toMiddle(): String {
        return ""
        //return this?.asCode() ?: "null"
    }

    private fun Kompose<*>?.toTrailing(): String {
        return ""
        //return this?.asCode() ?: "null"
    }

    private fun Long?.toBackgroundColor(): String {
        return if(this == null) "null" else "Color($this)"
    }

    private fun Boolean?.toAutomaticallyImplyLeading(): String {
        return if(this == null) "null" else "$this"
    }

    override fun print(): String {
        TODO("Not yet implemented")
    }

}