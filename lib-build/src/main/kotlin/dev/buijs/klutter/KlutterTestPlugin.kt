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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

class KlutterTestPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        target.extensions.create("dependenciesTest", KlutterTestExtension::class.java)

        target.apply {
            it.plugin(org.gradle.api.plugins.JavaPlugin::class.java)
            it.plugin(org.gradle.api.plugins.GroovyPlugin::class.java)
        }

        target.tasks.withType(Test::class.java).configureEach {
            it.useJUnitPlatform()
        }

    }

}

open class KlutterTestExtension {
    val implementation: List<String> = listOf(
        "io.kotlintest:kotlintest-runner-junit5:3.1.10",
         "org.codehaus.groovy:groovy-all:3.0.9",
        "org.spockframework:spock-core:2.2-M1-groovy-3.0",
         "org.mockito:mockito-core:4.2.0",
        "org.mockito.kotlin:mockito-kotlin:4.0.0",
    )
}

internal fun Project.dependenciesTest(): KlutterTestExtension {
    return extensions.getByName("dependenciesTest").let {
        if (it is KlutterTestExtension) { it } else {
            throw IllegalStateException("dependenciesTest extension is not of the correct type")
        }
    }
}