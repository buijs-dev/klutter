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
package dev.buijs.klutter.gradle.tasks

import dev.buijs.klutter.gradle.dsl.KlutterGradleDSL
import dev.buijs.klutter.kore.KlutterTask
import dev.buijs.klutter.kore.project.Project
import dev.buijs.klutter.kore.project.plugin
import mu.KotlinLogging
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

private val log = KotlinLogging.logger { }

internal typealias project = org.gradle.api.Project

/**
 * Parent of all Gradle Tasks.
 */
internal abstract class AbstractTask: DefaultTask() {

    init { group = "klutter" }

    internal abstract fun klutterTask(): KlutterTask

    @TaskAction
    fun execute() = klutterTask().run()

    fun project(): Project {
        val ext = project.klutterExtension()
        val root = ext.root ?: project.rootProject.projectDir
        log.info { "Klutter Gradle configured as plugin." }
        return root.plugin()
    }

}

internal fun org.gradle.api.Project.klutterExtension(): KlutterGradleDSL {
    return extensions.getByName("klutter").let {
        if (it is KlutterGradleDSL) { it } else {
            throw IllegalStateException("klutter extension is not of the correct type")
        }
    }
}