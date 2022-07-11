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

import mu.KotlinLogging
import org.jetbrains.kotlin.descriptors.runtime.structure.safeClassLoader

/**
 * Utility to load a local File as Class.
 */
internal class ClassFileLoader(
    loader: ClassLoader? = findClassLoader()
) : ClassLoader(loader) {

    private val log = KotlinLogging.logger { }

    fun load(classFile: ClassFile): Class<*> {
        log.debug("Loading: $classFile")
        return super.defineClass(
            classFile.className,
            classFile.classContent, 0,
            classFile.classContent.size,
        )
    }

}

private fun findClassLoader(): ClassLoader {

    val log = KotlinLogging.logger { }

    val fromContext = Thread.currentThread().contextClassLoader

    if(fromContext == null) {
        log.debug { "Current Thread Context has no classloader" }
    }

    return UIBuilderCollector::class.java.safeClassLoader

}