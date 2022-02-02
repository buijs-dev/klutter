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

package dev.buijs.klutter.annotations.processor


import dev.buijs.klutter.annotations.processor.dart.DartEnumValueBuilder
import dev.buijs.klutter.annotations.processor.dart.DartFieldBuilder
import dev.buijs.klutter.core.*

private val enumRegex = """(enum class ([^{]+?)\{[^}]+})""".toRegex()
private val bodiesRegex = """(open class ([^(]+?)\([^)]+\))""".toRegex()

/**
 * @author Gillian Buijs
 */
class KlutterResponseScanner(private val ktFileBody: String) {

    fun scan(): KlutterResponseScanResult {
        val messages = MessageScanner(ktFileBody).scan()
        val enumerations = EnumScanner(ktFileBody).scan()
        return KlutterResponseScanResult(DartObjects(messages, enumerations))
    }
}

/**
 * @author Gillian Buijs
 */
private class EnumScanner(private val content: String) {

    fun scan() = enumRegex.findAll(content).map { match ->
        val values = DartEnumValueBuilder(match.value).build()

        DartEnum(
            name = match.groups[2]?.value?.filter { !it.isWhitespace() }
                ?: throw KotlinFileScanningException("Failed to process an enum class."),
            values = values.constants,
            jsonValues = values.jsonValues
        )
    }.toList()

}

/**
 * @author Gillian Buijs
 */
private class MessageScanner(private val content: String) {

    fun scan() = bodiesRegex.findAll(content).map { match ->
        DartMessage(
            name = match.groups[2]?.value?.filter { !it.isWhitespace() }
                ?: throw KotlinFileScanningException("Failed to process an open class."),
            fields = match.value.lines().mapNotNull { DartFieldBuilder(it).build() }
        )
    }.toList()

}

/**
 * @author Gillian Buijs
 */
data class KlutterResponseScanResult(
    val dart: DartObjects,
)