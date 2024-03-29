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
package dev.buijs.klutter.kradle

import dev.buijs.klutter.kore.project.klutterBomVersion
import dev.buijs.klutter.kradle.command.getNewProjectOptions
import dev.buijs.klutter.kradle.shared.*
import dev.buijs.klutter.kradle.shared.createNewProject
import dev.buijs.klutter.kradle.shared.execFlutterCommand
import dev.buijs.klutter.kradle.shared.execGradleCommand
import dev.buijs.klutter.kradle.wizard.mrWizard
import java.nio.file.Paths

fun main(args: Array<String>) {
    println("""
  ════════════════════════════════════════════
     KRADLE (v$klutterBomVersion)
  ════════════════════════════════════════════
  """)

    val currentFolder = Paths.get("").toAbsolutePath().toFile()

    val first: (Array<String>) -> MutableList<String> = {
        args.toMutableList().also { it.removeFirst() }
    }

    when {
        args.isEmpty() -> mrWizard.runWizard(currentFolder)

        args.firstOrNull() == "-g" ->
            first(args).execGradleCommand(currentFolder)

        args.firstOrNull() == "-f" ->
            first(args).execFlutterCommand(currentFolder)

        args.firstOrNull() == "build" ->
            build(currentFolder)

        args.firstOrNull() == "clean" ->
            first(args).clean()

        args.firstOrNull() == "create" ->
            first(args).getNewProjectOptions().createNewProject()

        args.firstOrNull() == "get" ->
            first(args).getDependency()

        else -> println("I don't know what to do. Please try again!")
    }

}