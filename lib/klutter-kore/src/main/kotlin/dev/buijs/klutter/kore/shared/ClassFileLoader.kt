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
package dev.buijs.klutter.kore.shared

import dev.buijs.klutter.kore.project.Project
import mu.KotlinLogging
import java.lang.reflect.Type

/**
 * Utility to load a local File as Class.
 */
object ClassFileLoader: ClassLoader(findClassLoader()) {

    private val log = KotlinLogging.logger { }

    private val classes: MutableList<Class<*>> = mutableListOf()

    fun all(): List<Class<*>> = classes

    fun get(byType: Type): Class<*>? {

        if(classes.isEmpty()) {
            log.info { "No pre-loaded classes. Will attempt to tryLoadClass with current classloader." }
            return tryLoadClass(byType.typeName)
        }

        val foundOrNull = classes.firstOrNull { it == byType }

        if(foundOrNull == null) {
            log.info {
                "Class not found in pre-loaded classes. Will attempt to tryLoadClass with current classloader."
            }
        }

        return foundOrNull ?: tryLoadClass(byType.typeName)

    }

    fun set(loaded: List<Class<*>>) {
        classes.addAll(loaded)
    }

    fun load(classFile: ClassFile): Class<*>? {
        log.info("Loading: $classFile")
        return try {
            super.defineClass(
                classFile.className,
                classFile.classContent, 0,
                classFile.classContent.size,
            )
        } catch (e: Throwable) {
            log.info("Failed to load: $classFile", e); null
        }
    }

    fun tryLoadClass(name: String): Class<*>? {
        log.info("Try loading: $name")
        return try {
            super.loadClass(name)
        } catch (e: Throwable) {
            log.info("Failed to load: $name", e); null
        }
    }


}

/**
 * Get current classloader from context.
 */
fun findClassLoader(): ClassLoader {

    val log = KotlinLogging.logger { }

    val fromContext = Thread.currentThread().contextClassLoader

    if(fromContext == null) {
        log.debug { "Current Thread Context has no classloader" }
    }

    return Project::class.java.classLoader

}