/* Copyright (c) 2021 - 2023 Buijs Software
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
package dev.buijs.klutter.tasks.codegen

import dev.buijs.klutter.kore.KlutterException

inline fun <reified T: GenerateCodeAction> findGenerateCodeAction(
    options: GenerateCodeOptions
): GenerateCodeAction = when(T::class.java) {
    DartFormatTask::class.java ->
        options.toDartFormatTask()
    ExcludeArm64Task::class.java ->
        options.toExcludeArm64Task()
    GenerateAndroidLibTask::class.java ->
        options.toGenerateAndroidLibTask()
    GenerateCodeTask::class.java ->
        options.toGenerateCodeTask()
    GenerateFlutterControllersTask::class.java ->
        options.toGenerateControllersTask()
    GenerateFlutterLibTask::class.java ->
        options.toGenerateFlutterLibTask()
    GenerateIosLibTask::class.java ->
        options.toGenerateIosLibTask()
    GenerateFlutterMessagesTask::class.java ->
        options.toGenerateFlutterMessagesTask()
    PubGetTask::class.java ->
        options.toPubGetTask()
    else -> throw KlutterException("Unknown GenerateCodeAction: ${T::class.java}")
}

fun runGenerateCodeActions(
    vararg action: GenerateCodeAction
) {
    action.toList().forEach { it.run() }
}

sealed interface GenerateCodeAction {
    fun run()
}

// Use for UT only!
@Suppress("unused")
private object GroovyHelper {
    @JvmStatic
    fun findGenerateCodeAction(options: GenerateCodeAction): GenerateCodeAction =
        findGenerateCodeAction(options)
}