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

import dev.buijs.klutter.core.KlutterException
import dev.buijs.klutter.plugins.gradle.KlutterGradleTask
import dev.buijs.klutter.plugins.gradle.klutterExtension
import dev.buijs.klutter.ui.tasks.UiGeneratorTask
import org.gradle.jvm.tasks.Jar

/**
 * Task to generate a Flutter UI.
 *
 * Views can be constructed using a Klutter Kompose DSL, being one of:
 * - JetlagUI
 * - KlutterUI
 * - NotSwiftUI
 *
 * KlutterUI is the default DSL which resembles Flutter the most.
 * JetlagUI is a DSL which is inspired by Jetpack Compose.
 * NotSwiftUI is a DSL which is inspired by SwiftUI.
 *
 */
internal open class GenerateUI : KlutterGradleTask() {
    override fun describe() {
        project.klutterExtension().application?.let {
            UiGeneratorTask(
                pathToBuild = it.buildFolder
                    ?: project.rootProject.project(":lib").buildDir.resolve("libs/kompose-jvm.jar"),
                pathToOutput = it.outputFolder
                    ?: project.rootProject.rootDir.resolve("app/frontend/lib"),
            ).run()
        } ?: throw KlutterException("Missing application config in klutter block.")
    }
}