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

import com.intellij.openapi.util.Disposer
import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.tasks.adapter.GenerateAdapterTask
import dev.buijs.klutter.plugins.gradle.KlutterGradleExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CompilerConfiguration

/**
 * @author Gillian Buijs
 */
open class GenerateAdapterGradleTask: KlutterGradleTask() {

    private val context by lazy {
        val config = CompilerConfiguration()
        config.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        KotlinCoreEnvironment.createForProduction(Disposer.newDisposable(), config, EnvironmentConfigFiles.JVM_CONFIG_FILES).project
    }

    override fun describe() {

        // A plugin project does not have all klutter modules
        // and the platform module has a different name/location.
        if(isPlugin()) {

            val root = if(ext.root == null) {
                Root(location = project.rootDir.absolutePath, validate = false)
            } else {
                Root(location = ext.root?.absolutePath!!, validate = false)
            }

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

            GenerateAdapterTask(
                context = context,
                android = Android(root = root),
                ios = IOS(root = root),
                flutter = Flutter(root = root),
                platform = Platform(
                    root = root,
                    file = root.resolve("klutter/$pluginName")
                        .normalize()
                        .absoluteFile,
                ),
                iosVersion = "9.0"
            )
        } else {
            project().let { project ->
                GenerateAdapterTask(
                    context = context,
                    android = project.android,
                    ios = project.ios,
                    flutter = project.flutter,
                    platform = project.platform,
                    iosVersion = "13.0"
                ).run()
            }
        }

    }

}