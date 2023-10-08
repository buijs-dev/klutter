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
@file:Suppress("unused")
package dev.buijs.klutter.gradle.dsl

import dev.buijs.klutter.kore.common.ExcludeFromJacocoGeneratedReport
import dev.buijs.klutter.kore.project.mapper
import dev.buijs.klutter.kore.project.toConfigOrNull
import dev.buijs.klutter.kore.common.maybeCreate
import mu.KotlinLogging
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import java.io.File

private val log = KotlinLogging.logger { }

@ExcludeFromJacocoGeneratedReport(
    reason = "Should be test with System Test (see test-ksp)")
fun KotlinDependencyHandler.embedded(dependencyNotation: String) {
    this.implementation(dependencyNotation)

    val extension =
        project.extensions.findByType(KlutterExtension::class.java)

    if(extension == null)
        log.info { "KlutterExtension Not Found." }

    val configFile = extension?.root?.resolve("klutter.yaml") ?: return

    val config = configFile.maybeCreate().toConfigOrNull() ?: return

    val embedded = config.dependencies.embedded
        .toMutableSet()
        .bumpDependencyVersion(dependencyNotation)

    configFile.writeText(mapper.writeValueAsString(
        config.copy(
            dependencies = config.dependencies.copy(
                embedded = embedded))))
}

@ExcludeFromJacocoGeneratedReport
internal fun Project.createKlutterDependency(
    simpleModuleName: String, version: String? = null
): ExternalModuleDependency {
    val versionNotNull = version ?: KlutterVersion.byName(simpleModuleName)
    val dependency = project.dependencies.create("dev.buijs.klutter:$simpleModuleName:$versionNotNull")
    return dependency as ExternalModuleDependency
}

@ExcludeFromJacocoGeneratedReport
internal fun Project.createDependency(
    dependencyNotation: String,
): ExternalModuleDependency {
    val dependency = project.dependencies.create(dependencyNotation)
    return dependency as ExternalModuleDependency
}

internal fun File.embeddedDependenciesFromConfigFile(): Set<String> {
    val regex = """\s*?-\s*("|'|)(.+?)("|'|)${'$'}""".toRegex()
    if(!exists()) return emptySet()
    val output = mutableSetOf<String?>()
    val lines = readLines()
    var insideEmbeddedBlock = false
    for(line in lines) {
        if(insideEmbeddedBlock) {
            when {
                line.isBlank() -> { }
                line.matches(regex) ->
                    output.add(regex.find(line)?.groupValues?.get(2))
                else -> insideEmbeddedBlock = false
            }
        } else if(line.trim() == "embedded:") {
            insideEmbeddedBlock = true
        }
    }
    return output.filterNotNull().toSet()
}

internal fun MutableSet<String>.bumpDependencyVersion(dependencyNotation: String): MutableSet<String> {
    val dependencySplitted = dependencyNotation.split(":")
    val dependencyGroupAndArtifact = "${dependencySplitted[0]}:${dependencySplitted[1]}"
    return filter { !it.startsWith(dependencyGroupAndArtifact) }
        .toMutableSet()
        .also { it.add(dependencyNotation) }
}