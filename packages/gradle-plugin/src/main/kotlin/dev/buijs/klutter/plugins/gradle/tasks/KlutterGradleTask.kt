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
import dev.buijs.klutter.plugins.gradle.adapter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Parent of all Gradle Tasks.
 * This class groups all implementing class under the <b>klutter</> group and gives access to the klutter configuration.
 *
 * @author Gillian Buijs
 */
abstract class KlutterGradleTask: DefaultTask() {
    init { group = "klutter" }

    @Internal
    val ext = project.adapter()

    /**
     * The implementing class must describe what the task does by implementing this function.
     */
    abstract fun describe()

    @TaskAction
    fun execute() = describe()

    fun iosVersion() = ext.getIos()?.version?:"13.0"

    fun appInfo() = ext.getAppInfo() ?: throw KlutterConfigException("App Config missing!")

    fun project() = KlutterProjectFactory.create(
        Root(ext.root ?: throw KlutterGradleException("Path to root folder is not set."))
    ) ?: throw KlutterGradleException("Invalid Klutter Project.")

}