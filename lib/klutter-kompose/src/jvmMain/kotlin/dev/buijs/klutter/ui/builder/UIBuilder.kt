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
package dev.buijs.klutter.ui.builder

import dev.buijs.klutter.kore.KlutterPrinter
import dev.buijs.klutter.ui.KomposeBody
import dev.buijs.klutter.ui.builder.jetlag.JetlagBuilder
import dev.buijs.klutter.ui.builder.klutterui.KlutterUIBuilder
import dev.buijs.klutter.ui.builder.notswiftui.NotSwiftUIBuilder
import dev.buijs.klutter.ui.templates.ScreenTemplate

abstract class KlutterUI(
    name: String,
): UIBuilder(name) {

    abstract fun screen(): KlutterUIBuilder.() -> Unit

    override fun view(): KomposeBody = KlutterUIBuilder().apply(screen()).build()

}

abstract class JetlagUI(
  name: String,
): UIBuilder(name) {

    abstract fun screen(): JetlagBuilder.() -> Unit

    override fun view(): KomposeBody = JetlagBuilder().apply(screen()).build()

}

abstract class NotSwiftUI(
   name: String,
): UIBuilder(name) {

    abstract fun screen(): NotSwiftUIBuilder.() -> Unit

    override fun view(): KomposeBody = NotSwiftUIBuilder().apply(screen()).build()

}

sealed class UIBuilder(private val name: String): KlutterPrinter {

    abstract fun view(): KomposeBody

    fun name(): String = name

    override fun print(): String = ScreenTemplate(name, view()).print()

}

typealias KlutterScreen = KlutterUIBuilder.() -> Unit

typealias JetlagScreen = JetlagBuilder.() -> Unit

typealias NotSwiftUIScreen = NotSwiftUIBuilder.() -> Unit