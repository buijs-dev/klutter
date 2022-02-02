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


import dev.buijs.klutter.core.DartField
import dev.buijs.klutter.core.DartKotlinMap
import dev.buijs.klutter.core.KlutterLogger


private var log: KlutterLogger? = null

/**
 * Proces a single line and try to create a DartField object.
 *
 * @return [DartField]
 *
 * @author Gillian Buijs
 */
internal class DartFieldBuilder(private val line: String) {

    fun build(): DartField? {
        var step: DartFieldBuilderStep? = StartDartFieldProcessingStep(line)

        while(step?.hasNext() == true) {
            step = step.execute()
        }

        return step?.field()
    }

}


/**
 * Superclass to control processing of an open class DTO.
 *
 * Each step does a part of the processing and returns the next step to be executed.
 *
 * @author Gillian Buijs
 */
private abstract class DartFieldBuilderStep {

    abstract fun execute(): DartFieldBuilderStep?

    open fun hasNext(): Boolean = true

    open fun field(): DartField? = null

    fun getLogger(): KlutterLogger {

        if(log == null){
            log = KlutterLogger()
        }

        return log as KlutterLogger
    }

}

/**
 * [DartFieldBuilderStep] which checks if a line has content or not.
 * @return [ProcessingEnumValueSucceeded] if line has no content  or [SplitEnumValuesStep] if content is found.
 *
 * @author Gillian Buijs
 */
private class StartDartFieldProcessingStep(private val line: String): DartFieldBuilderStep() {

    override fun execute() = if(line.isBlank()) DartFieldProcessingSucceeded() else ApplyDartFieldRegexStep(line)

}

/**
 * [DartFieldBuilderStep] which applies regex <i>((va.) ([^:]+?): (.+))</i> to the line.
 * @return [ProcessingEnumValueFailed] if the regex does not match or [DetermineDataTypeStep] if a match is found.
 *
 * @author Gillian Buijs
 */
private class ApplyDartFieldRegexStep(private val content: String): DartFieldBuilderStep() {

    private val lineRegex = """val ([^:]+?): (.+)""".toRegex()

    override fun execute(): DartFieldBuilderStep {
        val groups = lineRegex.find(content) ?.groupValues
            ?: return DartFieldProcessingFailed("Invalid field declaration: $content")

        return DetermineDartFieldNameStep(
            group1 = groups[1],
            group2 = groups[2],
        )
    }

}

/**
 * [DartFieldBuilderStep] which determines the name of the field and if it is optional or not.
 * @return [DetermineDartFieldDataTypeStep] if successfull or [ProcessingEnumValueFailed] if not.
 *
 * @author Gillian Buijs
 */
private class DetermineDartFieldNameStep(
    private val group1: String?,
    private val group2: String?,
    ): DartFieldBuilderStep() {

    override fun execute(): DartFieldBuilderStep {
        val name = group1?.trim()

        return when {

            name.isNullOrBlank() -> {
                DartFieldProcessingFailed("Could not determine name of field.")
            }

            name.contains(" ") -> {
                DartFieldProcessingFailed("Name of field is invalid: '$name'")
            }

            else -> {
                DetermineDartFieldDataTypeStep(name, group2)
            }
        }

    }

}

/**
 * [DartFieldBuilderStep] which checks the data type declaration of the field.
 * @return [ProcessingEnumValueSucceeded] with the assembled DartField or [ProcessingEnumValueFailed].
 *
 * @author Gillian Buijs
 */
private class DetermineDartFieldDataTypeStep(
    private val name: String,
    private val group1: String?,
): DartFieldBuilderStep() {

    private val listRegex = """List<([^>]+?)>""".toRegex()

    override fun execute(): DartFieldBuilderStep {
        var type: String = group1
            ?.filter { !it.isWhitespace() }
            ?: return DartFieldProcessingFailed("Could not determine datatype of field.")

        type = type.removeSuffix(",")

        //Setting a field to null explicitly in Kotlin
        //gives the ability to have optional fields in JSON.
        //Without the '= null' deserialization would fail
        //if the key is missing.
        type = type.removeSuffix("=null")

        //If at this point the type still contains a default value
        //then processing can not continue.
        //The DTO to be generated in Dart can not have default values
        //for an immutable field.
        if(type.contains("=")){
            return DartFieldProcessingFailed("A KlutterResponse DTO can not have default values.")
        }

        val optional = type.endsWith("?")

        if(optional) type = type.removeSuffix("?")

        var isList = false

        listRegex.find(type)?.groups?.get(1)?.value?.also {
            type = it
            isList = true
        }

        val dataType = DartKotlinMap.toMapOrNull(type)?.dartType ?: ""
        val customDataType = if(dataType == "") type else null

        return DartFieldProcessingSucceeded(
            field = DartField(
                name = name,
                isList = isList,
                optional = optional,
                dataType = dataType,
                customDataType = customDataType
            )
        )
    }

}

/**
 * Step which indicates a line has been processed successfully.
 * A blank line also ends with a succeeded step hence why field [DartField] is nullable.
 *
 * @author Gillian Buijs
 */
private class DartFieldProcessingSucceeded(private val field: DartField? = null): DartFieldBuilderStep() {

    override fun execute(): Nothing? = null

    override fun hasNext() = false

    override fun field() = field

}

/**
 * Step which indicates a line has been processed unsuccessfully.
 *
 * The cause of failure is logged and the processing is ended.
 *
 * @author Gillian Buijs
 */
private class DartFieldProcessingFailed(private val why: String): DartFieldBuilderStep() {

    override fun execute(): Nothing? {
        getLogger().error(why)
        return null
    }

    override fun hasNext() = false

    override fun field(): Nothing? = null

}