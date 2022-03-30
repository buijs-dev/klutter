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


import dev.buijs.klutter.core.DartEnum
import org.gradle.api.logging.Logging

private val log = Logging.getLogger(DartEnumValueBuilder::class.java)

/**
 * @author Gillian Buijs
 */
data class DartEnumValues(
    val constants: List<String>,
    val jsonValues: List<String>,
)

/**
 * Proces a single line and try to create a DartEnum object.
 *
 * @return [DartEnum]
 *
 * @author Gillian Buijs
 */
internal class DartEnumValueBuilder(private val content: String) {

    fun build(): DartEnumValues {
        var step: DartEnumValueBuilderStep? = StartProcessingEnumValueStep(content)

        while(step?.hasNext() == true) {
            step = step.execute()
        }

        return DartEnumValues(
            constants = step?.constants()?: emptyList(),
            jsonValues = step?.jsonValues()?: emptyList(),
        )

    }

}


/**
 * Superclass to control processing of an enum class.
 *
 * Each step does a part of the processing and returns the next step to be executed.
 *
 * @author Gillian Buijs
 */
private abstract class DartEnumValueBuilderStep {

    abstract fun execute(): DartEnumValueBuilderStep?

    open fun hasNext(): Boolean = true

    open fun constants(): List<String>? = null

    open fun jsonValues(): List<String>? = null

}

/**
 * [DartEnumValueBuilderStep] which checks if a line has content or not.
 * @return [ProcessingEnumValueSucceeded] if line has no content  or [SplitEnumValuesStep] if content is found.
 *
 * @author Gillian Buijs
 */
private class StartProcessingEnumValueStep(private val content: String): DartEnumValueBuilderStep() {

    override fun execute() = if(content.isBlank()) {
        ProcessingEnumValueFailed("Enum has no values")
    } else SplitEnumValuesStep(content)

}

/**
 * @return [ProcessingEnumValueSucceeded] with a list of values.
 * @author Gillian Buijs
 */
private class SplitEnumValuesStep(private val content: String): DartEnumValueBuilderStep() {

    override fun execute(): DartEnumValueBuilderStep {
        val splitted = content
            .substringAfter("{")
            .substringBefore("}")
            .split(",").map { it.trim() }

        if(splitted.any { it.contains("@") }){
            val jsonValues = splitted
                .map { it.substringAfter("\"").substringBefore("\"") }
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            val constants = splitted
                .map { it.substringAfterLast(")") }
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            return ProcessingEnumValueSucceeded(
                values = constants,
                jsonValues = jsonValues
            )
        }

        return ProcessingEnumValueSucceeded(
            values = splitted,
            jsonValues = emptyList()
        )

    }

}

/**
 * Step which indicates processing has been successful.
 *
 * @author Gillian Buijs
 */
private class ProcessingEnumValueSucceeded(
    private val values: List<String>,
    private val jsonValues: List<String>,
    ): DartEnumValueBuilderStep() {

    override fun execute(): Nothing? = null

    override fun hasNext() = false

    override fun constants() = values

    override fun jsonValues() = jsonValues
}

/**
 * Step which indicates processing was unsuccessful.
 *
 * The cause of failure is logged and the processing is ended.
 *
 * @author Gillian Buijs
 */
private class ProcessingEnumValueFailed(private val why: String): DartEnumValueBuilderStep() {

    override fun execute(): Nothing? {
        log.error(why)
        return null
    }

    override fun hasNext() = false

    override fun constants(): Nothing? = null

}


