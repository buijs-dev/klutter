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

package dev.buijs.klutter.annotations.processor.dart


import dev.buijs.klutter.core.KlutterLogger
import dev.buijs.klutter.core.DartEnum

private var log: KlutterLogger? = null

/**
 * Proces a single line and try to create a DartEnum object.
 *
 * @return [DartEnum]
 *
 * @author Gillian Buijs
 */
internal class DartEnumValueBuilder(private val content: String) {

    fun build(): List<String> {
        var step: DartEnumValueBuilderStep? = StartProcessingEnumValueStep(content)

        while(step?.hasNext() == true) {
            step = step.execute()
        }

        return step?.values()?: emptyList()
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

    open fun values(): List<String>? = null

    fun getLogger(): KlutterLogger {

        if(log == null){
            log = KlutterLogger()
        }

        return log as KlutterLogger
    }

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

    override fun execute() = ProcessingEnumValueSucceeded(content
        .substringAfter("{")
        .substringBefore("}")
        .split(",").map { it.trim() }
    )


}

/**
 * Step which indicates processing has been successful.
 *
 * @author Gillian Buijs
 */
private class ProcessingEnumValueSucceeded(private val values: List<String>): DartEnumValueBuilderStep() {

    override fun execute(): Nothing? = null

    override fun hasNext() = false

    override fun values() = values

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
        getLogger().error(why)
        return null
    }

    override fun hasNext() = false

    override fun values(): Nothing? = null

}


