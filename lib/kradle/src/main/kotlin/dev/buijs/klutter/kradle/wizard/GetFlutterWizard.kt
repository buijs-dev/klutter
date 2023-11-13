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
package dev.buijs.klutter.kradle.wizard

import dev.buijs.klutter.kore.common.ExcludeFromJacocoGeneratedReport
import dev.buijs.klutter.kore.project.*
import dev.buijs.klutter.kore.tasks.project.DownloadFlutterTask
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

@ExcludeFromJacocoGeneratedReport
internal fun getFlutterWizard() {
    val distribution = askForFlutterVersion().let { version ->
        PrettyPrintedFlutterDistribution(version).flutterDistribution
    }

    val target = askForDownloadFolder()

    val overwrite = mrWizard.promptConfirm(
        message = "Delete the existing SDK distribution if it exists?",
        default = false)
    println("Downloading Flutter SDK...")
    DownloadFlutterTask(distribution, overwrite, target).run()
    println("Finished!")
}

private fun askForDownloadFolder(): File? {
    val cache = "Default cache"
    val other = "Other (specify)"
    val cacheOrOther = mrWizard.promptList(
        hint = "press Enter to pick",
        message = "Where to store the Flutter SDK distribution?",
        choices = listOf(cache, other))

    /**
     * Return null if cache is to be used,
     * so that DownloadFlutterTask automatically uses its default configuration.
     */
    if(cacheOrOther == cache) return null

    var file: File
    do {
        val path = mrWizard.promptInput(
            message = "Please specify a download folder.",
            default = Paths.get("").absolutePathString())
        file = File(path)
    } while(!file.exists())
    return file
}