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

import com.intellij.openapi.project.Project
import dev.buijs.klutter.core.*
import java.io.File

private val enumRegex = """(enum class ([^{]+?)\{[^}]+})""".toRegex()
private val bodiesRegex = """(open class ([^(]+?)\([^)]+\))""".toRegex()

/**
 * @author Gillian Buijs
 */
internal class KlutterResponseProcessor(
    private val source: File,
    private val context: Project,
) {

    internal fun process(): DartObjects {

        val enumerations = mutableListOf<DartEnum>()
        val messages = mutableListOf<DartMessage>()

        KlutterAnnotatedSourceCollector(source, "@KlutterResponse")
            .collect()
            .map { it.toKotlinFiles(context) }
            .forEach {
                enumerations.addAll(EnumScanner(it.content).scan())
                messages.addAll(MessageScanner(it.content).scan())
            }

        val customDataTypes = mutableListOf<String>()

        //Collect all custom data types
        for (message in messages) {
            for (field in message.fields) {
                field.customDataType?.let { customDataTypes.add(it) }
            }
        }

        //Iterate the message names and match it to the custom data types.
        //Remove from custom data types list if matched.
        messages.map { it.name }.forEach { customDataTypes.removeAll { cdt -> cdt == it } }

        //Iterate the enumeration names and match it to the custom data types.
        //Remove from custom data types list if matched.
        enumerations.map { it.name }.forEach { customDataTypes.removeAll { cdt -> cdt == it } }

        //Any custom data type name left in the list means there is no class definition found by this name
        messages.removeIf { message ->
            message.fields.map { field -> field.name }.any { customDataTypes.contains(it) }
        }

        if(customDataTypes.isNotEmpty()) {
            throw KlutterCodeGenerationException(
                """ |Processing annotation '@KlutterResponse' failed, caused by:
                    |
                    |Could not resolve the following classes:
                    |
                    |${customDataTypes.joinToString{ "- '$it'\r\n" }}
                    |
                    |Verify if all KlutterResponse annotated classes comply with the following rules:
                    |
                    |1. Must be an open class
                    |2. Fields must be immutable
                    |3. Constructor only (no body)
                    |4. No inheritance
                    |5. Any field type should comply with the same rules
                    |
                    |If this looks like a bug please file an issue at: https://github.com/buijs-dev/klutter/issues
                """.trimMargin())
        }

        return DartObjects(messages, enumerations)

    }

}

/**
 * @author Gillian Buijs
 */
internal class EnumScanner(private val content: String) {

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
internal class MessageScanner(private val content: String) {

    fun scan() = bodiesRegex.findAll(content).map { match ->
        DartMessage(
            name = match.groups[2]?.value?.filter { !it.isWhitespace() }
                ?: throw KotlinFileScanningException("Failed to process an open class."),
            fields = match.value.lines().mapNotNull { DartFieldBuilder(it).build() }
        )
    }.toList()

}