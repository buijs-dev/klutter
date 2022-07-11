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

import dev.buijs.klutter.core.KlutterException
import dev.buijs.klutter.ui.viewmodel.KomposeViewModel
import dev.buijs.klutter.ui.viewmodel.Navigator

/**
 *
 */
data class TextButton<T: KomposeViewModel>(
    private val model: T,
    val text: String,
    private val onPressed: T.() -> Unit
): Kompose {

    override fun hasChild(): Boolean = false

    override fun hasChildren(): Boolean = false

    override fun child(): Kompose {
        throw KlutterException("Text has no child")
    }

    override fun children(): List<Kompose> = emptyList()

    override fun print(): String {

        val model = model()

        if(model !is Navigator) {
            //TODO
            throw Exception("todo")
        }

        //TODO arguments could be more than just a single String value
        return """ 
            |PlatformTextButton(
            |   child: Text('$text'),
            |   onPressed: () => Navigator.pushNamed(
            |    context, 
            |    KomposeNavigator.${model.route}Route,
            |    arguments: '${model.arguments}',
            |    ),
            |)""".trimMargin()
    }

    fun model(): T = model.apply(onPressed)
}