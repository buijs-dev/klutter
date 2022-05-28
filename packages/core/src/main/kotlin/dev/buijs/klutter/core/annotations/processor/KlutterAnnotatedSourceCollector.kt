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

package dev.buijs.klutter.core.annotations.processor

import java.io.File

/**
 * @author Gillian Buijs
 */
internal class KlutterAnnotatedSourceCollector(
    private val source: File,
    private val annotationName: String,
){

    fun collect():  List<File> {

        var annotation = annotationName
        if(!annotation.startsWith("@")) {
            annotation = "@$annotationName"
        }

        return search(source)
            .filter { it.readText().contains(annotation) }

    }

    private fun search(directory: File): List<File> {
        val classes = mutableListOf<File>()

        if (directory.exists()) {
            directory.walkTopDown().forEach { f ->
                if(f.isFile) {
                    classes.add(f)
                }
            }
        }

        return classes
    }

}