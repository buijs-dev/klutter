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
package dev.buijs.klutter.kore.tasks.project

import dev.buijs.klutter.kore.project.FlutterDistribution
import dev.buijs.klutter.kore.project.flutterExecutable
import dev.buijs.klutter.kore.project.folderNameString
import dev.buijs.klutter.kore.tasks.execute

internal fun ProjectBuilderOptions.toRunFlutterAction() =
    RunFlutterCreate(pluginName, groupName, rootFolder, flutterFolder)

internal class RunFlutterCreate(
    private val pluginName: PluginName,
    private val groupName: GroupName,
    private val rootFolder: RootFolder,
    private val flutterDistribution: FlutterDistribution,
): ProjectBuilderAction {

    override fun doAction() {
        val flutter = flutterExecutable(flutterDistribution.folderNameString).absolutePath

        val validRootFolder = rootFolder.validRootFolderOrThrow()

        val validPluginName = pluginName.validPluginNameOrThrow()

        val validGroupName = groupName.validGroupNameOrThrow()

        "$flutter create $validPluginName " +
                "--org $validGroupName " +
                "--template=plugin " +
                "--platforms=android,ios " +
                "-a kotlin -i swift" execute validRootFolder
    }

}