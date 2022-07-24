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
 * Printer to create SwiftKomposeAppState class which controls the
 * initialization and disposing of KomposeViewModel objects.
 */
internal class IosAdapterState(
    private val classes: List<String>,
): KlutterPrinter {

    override fun print(): String = """
        |import Platform
        |
        |${classes.asVariables()}
        |
        |${classes.toGetController()}
        |
        |${classes.toDisposeController()}
        |
        |${classes.asFunctions()}
        |
        |extension String: LocalizedError {
        |    public var errorDescription: String? { return self }
        |}
        |""".trimMargin()

}

/**
 * Print the variable containing a reference to the model or null.
 *
 * <B>Example</B>
 *
 * Given:
 * A model named GreetingModel
 *
 * Returns:
 * ```
 * private let greetingmodelState: GreetingModel? = GreetingModel()
 * ```
 */
private fun List<String>.asVariables(): String = joinToString("\n") {
    "private var ${it.state()}: ${it.withoutPackage()}? = ${it.withoutPackage()}()"
}

private fun List<String>.toGetController(): String =
    """
     |func getController(controller: String) -> AnyObject? {
     |      switch controller {
     |        ${joinToString("\n") {   """
     |        case "$it":
     |            return ${it.get().withoutPackage()}
     |                                          """.trimMargin() }}
     |        default: return nil
     |    }
     |}
    """.trimMargin()

private fun List<String>.toDisposeController(): String =
    """
     |func disposeController(controller: String?) throws {
     |    switch controller {
     |        ${joinToString("\n") { """
     |        case "$it":
     |            ${it.dispose()}
     |                                         """.trimMargin() }}
     |        default: throw "No such controller with name '\(controller)'"
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
 * (Kotlin source package where model is compiled)
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
 *  return foobarexamplegreetingmodelState!
 * }
 * ```
 */
private fun List<String>.asFunctions(): String = joinToString("\n") {
    """|func ${it.dispose()} {
       |    ${it.state()} = nil
       |}
       |
       |func ${it.init()} {
       |    ${it.state()} = ${it.withoutPackage()}()
       |}
       |
       |func ${it.get()} -> ${it.withoutPackage()} {
       |    if (${it.state()} == nil) {
       |        ${it.init().withoutPackage()}
       |    }
       |
       |    return ${it.state()}!
       |}
    """.trimMargin()
}

internal fun String.withoutPackage(): String = substringAfterLast(".")