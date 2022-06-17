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

package dev.buijs.klutter.core.annotations

import dev.buijs.klutter.core.shared.verifyExists
import org.jetbrains.kotlin.util.prefixIfNot
import java.io.File

/**
 * Find all files in a folder (including sub folders)
 * containing an annotation with [annotationName].
 *
 * @return List of Files that contain the given annotation.
 */
internal fun File.collectAnnotatedWith(annotationName: String): List<File> {

    val annotation = annotationName.prefixIfNot("@")

    return this
        .verifyExists()
        .walkTopDown()
        .map { f -> if(!f.isFile) null else f }
        .toList()
        .filterNotNull()
        .filter { it.readText().contains(annotation) }

}