@file:Suppress("unused")
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
package dev.buijs.klutter

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.net.URI
import java.util.*

class KlutterInternalPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        // no-op
    }
}

object ProjectVersions {
    val core = properties("core.version")
    val annotations = properties("annotations.version")
    val kompose = properties("kompose.version")
    val gradle = properties("plugin.gradle.version")
    val pub = properties("plugin.pub.version")
}

object Repository {
    val endpoint: URI = URI(properties("repo.endpoint"))
    val username = properties("repo.username")
    val password = properties("repo.password")
}

fun properties(key: String): String = Properties().also {
    it.load(KlutterInternalPlugin::class.java.classLoader
        .getResourceAsStream("publish.properties"))
}.getProperty(key) ?: throw GradleException("missing $key")