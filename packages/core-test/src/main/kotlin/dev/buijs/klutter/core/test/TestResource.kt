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

package dev.buijs.klutter.core.test

import java.io.File
import java.io.InputStream
import java.nio.file.Path

/**
 * Testing utility to copy files from resources to a target folder.
 *
 * @author Gillian Buijs
 */
class TestResource {

    /**
     * Copy all files from the resources folder to their corresponding target folder.
     *
     * @param toBeCopied [Map<String, Any>] containing:
     * - key: resource name
     * - value: target file being [String], [File] or [Path].
     *
     * @throws [TestException] if map contains a value which is
     * not a [String], [File] or [Path].
     */
    fun copyAll(toBeCopied: Map<String, Any>) {
        toBeCopied.forEach { (from, copyTo) ->
            when (copyTo) {
                is String -> copy(from, copyTo)
                is File -> copy(from, copyTo)
                is Path -> copy(from, copyTo)
                else -> throw TestException("Can not copy resource to instance of '${copyTo::class.java}'")
            }
        }
    }

    /**
     * Copy a file from the resources folder to a target folder.
     *
     * @param from [String] name of the resource.
     * @param copyTo [String] absolute path to copy the file to.
     *
     * @return [File] copied file.
     */
    fun copy(from: String, copyTo: String) =
        copyTo.maybeCreateFile().also { file ->
            from.copyTo(file)
        }

    /**
     * Copy a file from the resources folder to a target folder.
     *
     * @param from [String] name of the resource.
     * @param copyTo [File] to copy the resource to.
     *
     * @return [File] copied file.
     */
    fun copy(from: String, copyTo: File) = copy(from, copyTo.absolutePath)

    /**
     * Copy a file from the resources folder to a target folder.
     *
     * @param from [String] name of the resource.
     * @param copyTo [Path] to copy the resource to.
     *
     * @return [File] copied file.
     */
    fun copy(from: String, copyTo: Path) = copy(from, copyTo.toFile())

    /**
     * Get file content from a resource file as [String].
     */
    fun load(resource: String): String =
        TestResource::class.java.classLoader.getResource(resource)?.readText()
            ?: throw TestException("Failed to load from resource: '$resource'")

    /**
     * Copy a file from resources to a destination file.
     */
    private fun String.copyTo(file: File) {
        file.writeText(load(this))
    }

    /**
     * Convert a String to a File instance and create it if it does not exist.
     */
    private fun String.maybeCreateFile() = File(this).also {
        if(!it.exists()) {
            it.createNewFile()
        }
    }

}