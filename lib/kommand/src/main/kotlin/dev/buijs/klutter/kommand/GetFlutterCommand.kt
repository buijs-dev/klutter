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
package dev.buijs.klutter.kommand

import dev.buijs.klutter.kore.project.*
import dev.buijs.klutter.tasks.project.DownloadFlutterTask
import kotlinx.cli.ArgParser

private const val distributionDescription = "Flutter distribution in format major.minor.patch.platform.architecture (example: 3.0.5.windows.x64)."

private const val forceDescription = "Set to true to delete existing Flutter distribution."

internal fun List<String>.downloadFlutterByCommand() =
    toTypedArray().downloadFlutterByCommand()

internal fun Array<String>.downloadFlutterByCommand() =
    GetFlutterCommand(parser = ArgParser("klutter"), args = this).downloadOrFail()

private fun GetFlutterCommand.downloadOrFail() {
    DownloadFlutterTask(flutterDistribution, overwriteExistingDistribution).run()
}

private class GetFlutterCommand(parser: ArgParser, args: Array<String>) {

    private val dist by parser required distributionDescription

    private val overwrite by parser optionalBoolean forceDescription

    init {
        parser.parse(args)
    }

    val flutterDistribution: FlutterDistribution
        get() = FlutterDistributionFolderName(dist).flutterDistribution

    val overwriteExistingDistribution: Boolean
        get() = overwrite ?: false

}
