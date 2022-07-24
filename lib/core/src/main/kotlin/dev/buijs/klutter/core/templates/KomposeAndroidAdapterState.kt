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
package dev.buijs.klutter.core.templates

import dev.buijs.klutter.core.KlutterPrinter

/**
 * Printer to create KomposeAppState class which controls the
 * initialization and disposing of KomposeViewModel objects.
 */
internal class AndroidAdapterState(
    private val pluginPackageName: String,
    private val fullyQualifiedControllers: List<String>,
): KlutterPrinter {

    override fun print(): String = """
        |package $pluginPackageName
        |
        |${fullyQualifiedControllers.asVariables()}
        |
        |${fullyQualifiedControllers.toGetController()}
        |
        |${fullyQualifiedControllers.toDisposeController()}
        |
        |${fullyQualifiedControllers.asFunctions()}
        |""".trimMargin()

}

/**
 * Print the variable containing a reference to the model or null.
 *
 * <B>Example</B>
 *
 * Given:
 * A model named GreetingModel in package foo.bar.example
 *
 * Returns:
 * ```
 * private var foobarexamplegreetingmodelState: foo.bar.example.GreetingModel? = foo.bar.example.GreetingModel()
 * ```
 */
private fun List<String>.asVariables(): String = joinToString("\n") {
    "private var ${it.state()}: ${it}? = ${it}()"
}

private fun List<String>.toGetController(): String =
    """
     |fun getController(controller: String) = when {
     |        ${joinToString("\n") {   """
     |        controller == "$it" -> {
     |            ${it.get()}
     |        }                                 """.trimMargin() }}
     |        else -> throw Exception("No such controller with name '${"$"}controller'")
     |    }
    """.trimMargin()

private fun List<String>.toDisposeController(): String =
    """
     |fun disposeController(controller: String?) {
     |    when {
     |        ${joinToString("\n") { """
     |        controller == "$it" -> {
     |            ${it.dispose()}
     |        }                               """.trimMargin() }}
     |        else -> throw Exception("No such controller with name '${"$"}controller'")
     |    }
     |}
    """.trimMargin()

/**
 * Print the functions that will either initialize or dispose a model.
 *
 * <B>Example</B>
 *
 * Given:
 * A model named GreetingModel in package foo.bar.example
 *
 * Returns:
 * ```
 * fun dispose_foobarexampleGreetingModelState() {
 *      foobarexamplegreetingmodelState = null
 * }
 *
 * fun init_foobarexampleGreetingState() {
 *       foobarexamplegreetingmodelState = GreetingModel()
 * }
 *
 * fun get_foobarexampleGreetingState(): GreetingModel {
 *  if(foobarexamplegreetingmodelState == null) {
 *      init_foobarexampleGreetingState()
 *  }
 *
 *  return foobarexamplegreetingmodelState!!
 * }
 * ```
 */
private fun List<String>.asFunctions(): String = joinToString("\n") {
    """|private fun ${it.dispose()} {
       |    ${it.state()} = null
       |}
       |
       |private fun ${it.init()} {
       |    ${it.state()} = ${it}()
       |}
       |
       |private fun ${it.get()}: $it {
       |    if (${it.state()} == null) {
       |        ${it.init()}
       |    }
       |
       |    return ${it.state()}!!
       |}
    """.trimMargin()
}

internal fun String.dispose(): String = "dispose_${replace(".", "")}State()"
internal fun String.init(): String = "init_${replace(".", "")}State()"
internal fun String.get(): String = "get_${replace(".", "")}State()"
internal fun String.state(): String = "${replace(".", "").lowercase()}State"