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
package dev.buijs.klutter.tasks

import dev.buijs.klutter.kore.common.ExcludeFromJacocoGeneratedReport
import dev.buijs.klutter.kore.project.isWindows
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

var executor: Executor = Executor()

infix fun String. execute(file: File) =
    executor.execute(runFrom = file, command = this)

/**
 * Execute a CLI command.
 */
open class Executor {

    val String.platformSpecific: List<String>
        get()= buildList {
            if(isWindows) {
                add("cmd.exe")
                add("/c")
            }

            addAll(split(" "))
        }

    /**
     * Execute a CLI command.
     */
    @JvmOverloads
    @ExcludeFromJacocoGeneratedReport
    open fun execute(
        /**
         * Folder from where to execute this command.
         */
        runFrom: File = File(""),

        /**
         * Maximum time in seconds to wait for the command to be executed.
         */
        timeout: Long? = null,

        /**
         * The command to be executed.
         */
        command: String,

        /**
         * Any environment variable to add.
         */
        environment: Map<String,String> = emptyMap(),
    ): String = ProcessBuilder()
        .command(command.platformSpecific)
        .directory(runFrom)
        .also { it.environment().putAll(environment)}
        .start()
        .finish(timeout)
}

@ExcludeFromJacocoGeneratedReport
fun Process.finish(timeout: Long?): String {
    if(timeout == null) waitFor() else waitFor(timeout, TimeUnit.SECONDS)
    if(exitValue() != 0)
        throw IOException("Failed to execute command: \n${errorStream.reader().readText()}")
    return inputStream.readBytes().decodeToString()
}