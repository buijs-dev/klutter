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

package dev.buijs.klutter.plugins.gradle.tasks

import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.tasks.adapter.GenerateAdapterTask
import java.io.File

/**
 * @author Gillian Buijs
 */
open class GenerateAdapterGradleTask: KlutterGradleTask() {

    override fun describe() {

        // A plugin project does not have all klutter modules
        // and the platform module has a different name/location.
        if(isPlugin()) {
            val pluginName = ext.plugin?.name
                ?: throw KlutterGradleException("""
                    |Missing plugin-name. Specify plugin-name in klutter plugin DSL:
                    |
                    |Example:
                    |```
                    |klutter {
                    |   plugin {
                    |       name = "your_plugin_name"
                    |   }
                    |}
                    |```
                """.trimMargin())
           asPlugin(pluginName, ext.root?.absolutePath ?: project.rootDir.path)
        } else {
            project().let { project ->
                GenerateAdapterTask(
                    android = project.android,
                    ios = project.ios,
                    flutter = project.flutter,
                    platform = project.platform,
                ).run()
            }
        }

    }

}

internal fun asPlugin(pluginName: String, pathToRoot: String) {

    Root(
        location = pathToRoot,
        validate = false,
    ).let { root ->

        GenerateAdapterTask(
            isPlugin = true,
            libName = pluginName,
            android = Android(root = root),
            ios = IOS(root = root),
            flutter = Flutter(root = root),
            platform = Platform(
                root = root,
                file = File("${root.folder.absolutePath}/klutter/$pluginName"),
            ),
        ).run()
    }
}