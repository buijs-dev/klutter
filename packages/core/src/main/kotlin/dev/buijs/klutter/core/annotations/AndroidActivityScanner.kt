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

import dev.buijs.klutter.core.KlutterException
import dev.buijs.klutter.core.shared.verifyExists
import java.io.File

private typealias Collector = KlutterAnnotatedSourceCollector

private const val MSG_NO_RESULTS =
    "MainActivity not found or the @KlutterAdapter is missing in folder %s."

private const val MSG_TOO_MANY_RESULTS =
    "Expected to find one @KlutterAdapter annotation in the MainActivity file but found %s files."

/**
 * Scans the root/android/app folder to find the MainActivity file
 * which should be annotated with @KlutterAdapter.
 *
 * @throws [KlutterException] if no MainActivity file with @KlutterAdapter annotation is found.
 * @throws [KlutterException] if more than 1 MainActivity files with @KlutterAdapter annotations are found.
 *
 * @returns [File] MainActivity.
 *
 * @author Gillian Buijs
 */
internal class AndroidActivityScanner(
    private val pathToAndroidApp: File,
) {

    internal fun scan() = pathToAndroidApp
        .verifyExists()
        .collect()
        .validate()

    private fun File.collect(): List<File> {
        return Collector(this, "@KlutterAdapter").collect()
    }

    private fun List<File>.validate() = when {
        this.isEmpty() -> {
            throw KlutterException(MSG_NO_RESULTS.format(pathToAndroidApp))
        }

        this.size > 1 -> {
            throw KlutterException(MSG_TOO_MANY_RESULTS.format(size))
        }

        else -> this[0]
    }

}
