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
package dev.buijs.klutter.gradle

import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import java.io.File

/**
 * Glue for the DSL used in a build.gradle(.kts) file and the Klutter tasks.
 */
open class KlutterGradleExtension(project: Project) {

    private var handler = KlutterDependencyHandler(project)

    var root: File? = null

    @Internal
    internal var plugin: KlutterPluginDTO? = null

    @Internal
    internal var application: KlutterApplicationDTO? = null

    /**
     * Configure the Gradle Plugin for a klutter plugin (consumer or producer).
     */
    fun plugin(lambda: KlutterPluginBuilder.() -> Unit) {
        plugin = KlutterPluginBuilder().apply(lambda).build()
    }

    /**
     * Configure the Gradle Plugin for a klutter application using Kompose for the UI.
     */
    fun application(lambda: KlutterApplicationBuilder.() -> Unit) {
        application = KlutterApplicationBuilder().apply(lambda).build()
    }

    /**
     * Add klutter implementation dependency to this project.
     */
    fun implementation(simpleModuleName: String, version: String? = null) {
        handler.addImplementation(simpleModuleName, version)
    }

    /**
     * Add klutter testImplementation dependency to this project.
     */
    fun testImplementation(simpleModuleName: String, version: String? = null) {
        handler.addTestImplementation(simpleModuleName, version)
    }

}
