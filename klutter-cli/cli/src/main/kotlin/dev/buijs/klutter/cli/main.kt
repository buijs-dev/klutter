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

package dev.buijs.klutter.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import dev.buijs.klutter.cli.commands.Create
import dev.buijs.klutter.cli.commands.CreateOptions
import dev.buijs.klutter.cli.commands.CreateOptionsDefaults

/**
 * Commandline tool for creating and managing a Klutter project.
 *
 * Available commands:
 * [Create] Command to create a new Klutter project.
 *
 * @author Gillian Buijs
 */
internal const val klutterVersion = "2022-pre-alpha-4b"

fun main(args: Array<String>) = KlutterCli()
    .subcommands(Create())
    .main(args)

internal class KlutterCli : CliktCommand() {

    private val projectName: String by option(help = "Name of the project").default(CreateOptionsDefaults.projectName)
    private val location: String by option(help= "Location of the project" ).default(CreateOptionsDefaults.location)
    private val appId: String by option(help= "ApplicationId of the app" ).default(CreateOptionsDefaults.appId)

    override fun run() {
        when (currentContext.invokedSubcommand) {
            null -> echo("please supply a command")
            is Create -> {
                currentContext.obj = CreateOptions(
                    projectName = projectName,
                    location = location,
                    appId = appId,
                )
            }
            else -> echo("unknown command")
        }
    }

}