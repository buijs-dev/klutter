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

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension

class KlutterJavaPlugin: Plugin<Project> {

    override fun apply(target: Project) {

        target.extensions.create("dependenciesJava", KlutterJavaExtension::class.java)

        target.apply {
            it.plugin(org.gradle.api.plugins.JavaPlugin::class.java)
            it.plugin(org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin::class.java)
        }

        target.extensions.configure(JavaPluginExtension::class.java) {
            it.withJavadocJar()
            it.withSourcesJar()
            it.sourceCompatibility = JavaVersion.VERSION_11
            it.targetCompatibility = JavaVersion.VERSION_11
        }

    }

}

open class KlutterJavaExtension {

    val implementation = listOf(
        //Kotlin
        "org.jetbrains.kotlin:kotlin-reflect:1.6.21",
        "org.jetbrains.kotlin:kotlin-compiler:1.6.21",

        //Jackson for XML
        "com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2",
        "com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.13.2",
        "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.2",

        //Logging
        "org.slf4j:slf4j-api:2.0.0-alpha7",
        "io.github.microutils:kotlin-logging:2.1.23"
    )

}

internal fun Project.dependenciesJava(): KlutterJavaExtension {
    return extensions.getByName("dependenciesJava").let {
        if (it is KlutterJavaExtension) { it } else {
            throw IllegalStateException("dependenciesJava extension is not of the correct type")
        }
    }
}