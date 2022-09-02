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

import dev.buijs.klutter.annotations.KomposeView
import dev.buijs.klutter.kore.shared.ClassFileLoader
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

/**
 * Processor to read a build/classes folder and return all classes
 * which implement UIBuilder and are annotated with [KomposeView].
 */
class UIBuilderCollector {

    /**
     * Process all files and return every UIBuilder annotated with [KomposeView].
     */
    fun collect(): List<UIBuilder> = ClassFileLoader.all().mapNotNull { toUIBuilder(it) }

    private fun toUIBuilder(clazz: Class<*>): UIBuilder? {
        return try {
            log.info { "Constructing new UIBuilder from Class<?>." }
            clazz.getDeclaredConstructor().newInstance() as UIBuilder
        } catch (e: Throwable) {
            log.info("Constructing new UIBuilder failed.", e)
            return null
        }
    }

}