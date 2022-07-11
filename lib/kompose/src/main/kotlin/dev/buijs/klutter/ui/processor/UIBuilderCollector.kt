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
package dev.buijs.klutter.ui.processor

import dev.buijs.klutter.annotations.KomposeView
import dev.buijs.klutter.ui.builder.UIBuilder
import mu.KotlinLogging
import java.io.File

/**
 * Processor to read a build/classes folder and return all classes
 * which implement UIBuilder and are annotated with [KomposeView].
 */
class UIBuilderCollector(pathToBuildFolder: String) {

    private val log = KotlinLogging.logger { }

    private val pathToClasses: File = pathToClasses(pathToBuildFolder)

    /**
     * Process all files and return every UIBuilder annotated with [KomposeView].
     */
    fun collect(): List<UIBuilder> = findClasses()
        .filter { it.isAnnotationPresent(KomposeView::class.java) }
        .mapNotNull { toUIBuilder(it) }

    private fun findClasses(): Set<Class<*>> {
        log.debug("Creating new ClassFileLoader to find UIBuilders.")
        val loader = ClassFileLoader()
        log.debug("Lookup all files in {}", pathToClasses)
        return pathToClasses.walkTopDown()
            .map { if(!it.isFile) null else it }
            .filterNotNull()
            .filter { it.name.endsWith(".class") }
            .map { ClassFile(it, it.name.substringBeforeLast(".")) }
            .map { loader.load(it).also { log.debug { "Loaded class: ${it.name}" } } }
            .toSet()
    }

    private fun toUIBuilder(clazz: Class<*>): UIBuilder? {
        return try {
            log.debug("Constructing new UIBuilder from Class<?>.")
            clazz.getDeclaredConstructor().newInstance() as UIBuilder
        } catch (e: Exception) {
            log.warn("Constructing new UIBuilder failed.", e)
            return null
        }
    }

    private fun pathToClasses(pathToBuildFolder: String): File {
        log.info("UIBuilderCollector pathToBuilderFolder: {}", pathToBuildFolder)
        val pathToClasses = File(pathToBuildFolder).resolve("classes")
        log.info("UIBuilderCollector pathToClasses: {}", pathToClasses)
        return pathToClasses
    }

}