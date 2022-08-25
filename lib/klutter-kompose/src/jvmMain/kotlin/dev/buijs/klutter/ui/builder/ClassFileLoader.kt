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

import mu.KotlinLogging
import java.io.File
import java.lang.reflect.Type
import java.net.URL
import java.net.URLClassLoader
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Utility to load a local File as Class.
 */
object ClassFileLoader: ClassLoader(findClassLoader()) {

    private val log = KotlinLogging.logger { }

    private val classes: MutableList<Class<*>> = mutableListOf()

    internal fun all(): List<Class<*>> = classes

    internal fun get(byType: Type): Class<*>? {

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

    internal fun set(loaded: List<Class<*>>) {
        classes.addAll(loaded)
    }

    private fun load(classFile: ClassFile): Class<*>? {
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

    private fun tryLoadClass(name: String): Class<*>? {
        log.info("Try loading: $name")
        return try {
            super.loadClass(name)
        } catch (e: Throwable) {
            log.info("Failed to load: $name", e); null
        }
    }

    /**
     * Load all classes from a build/classes folder.
     */
    fun loadBuild(pathToClasses: File) {
        log.info("Creating new ClassFileLoader to find UIBuilders.")
        log.info("Lookup all files in {}", pathToClasses)
        set(
            pathToClasses.walkTopDown()
                .map { if(!it.isFile) null else it }
                .filterNotNull()
                .filter { it.name.endsWith(".class") }
                .map { ClassFile(it, it.name.substringBeforeLast(".")) }
                .map { cf -> // Load the class
                    val clazz = load(cf)
                    when {
                        clazz != null -> {
                            log.info { "Loaded class: ${cf.className}" }
                            // If synthetic class return null to filter it
                            if(cf.className.contains("$")) null else clazz
                        }
                        else -> null
                    }
                }
                .filterNotNull()
                .toList()
        )
    }

    /**
     * Load all classes from a Jar file.
     */
    fun loadJar(pathToJar: String) {
        val jarFile = JarFile(pathToJar)
        val e: Enumeration<JarEntry> = jarFile.entries()

        val ctx = findClassLoader()
        val urls: Array<URL> = arrayOf(URL("jar:file:$pathToJar!/"))
        val cl: URLClassLoader = URLClassLoader.newInstance(urls, ctx)

        val loaded = mutableListOf<Class<*>>()
        while (e.hasMoreElements()) {
            val je: JarEntry = e.nextElement()
            if (je.isDirectory || !je.name.endsWith(".class")) {
                continue
            }
            // -6 because of .class
            var className: String = je.name.substring(0, je.name.length - 6)
            className = className.replace('/', '.')
            loaded.add(cl.loadClass(className))
        }
        set(loaded)
    }
}

/**
 * Get current classloader from context.
 */
private fun findClassLoader(): ClassLoader {

    val log = KotlinLogging.logger { }

    val fromContext = Thread.currentThread().contextClassLoader

    if(fromContext == null) {
        log.debug { "Current Thread Context has no classloader" }
    }

    return UIBuilderCollector::class.java.classLoader

}