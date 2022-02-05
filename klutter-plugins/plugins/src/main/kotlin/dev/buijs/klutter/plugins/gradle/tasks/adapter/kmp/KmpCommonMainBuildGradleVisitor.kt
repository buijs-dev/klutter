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

package dev.buijs.klutter.plugins.gradle.tasks.adapter.kmp

import dev.buijs.klutter.core.*

/**
 * @author Gillian Buijs
 */
internal class KmpCommonMainBuildGradleVisitor(
    private val kmp: KMP,
) : KlutterVisitor {

    private val exclusionMark = "//NOKLUTTER"
    private val exclusionRegex = """((runtimeOnly|implementation)\([^)]+?\))$exclusionMark""".toRegex()
    private val commonMainRegex = """val${kmp.moduleName()}Mainbygetting\{dependencies\{([^}]+?)}}""".toRegex()
    private val androidMainRegex = """valandroidMainbygetting\{dependencies\{([^}]+?)}}""".toRegex()
    private val implementationRegex = """((runtimeOnly|implementation)\([^)]+?\))""".toRegex()
    private val properties = KlutterPropertiesReader(kmp.root.folder.resolve(".klutter/klutter.properties")).read()

    /**
     * List of normalized dependencies required by Android.
     */
    val requiredDependencies = mutableListOf<String>()

    override fun visit(): KlutterLogger {
        val logger = KlutterLogger()

        val gradleFile = kmp.module().resolve("build.gradle.kts")

        if(!gradleFile.exists()){
            logger.error("Could not resolve file: $gradleFile")
            return logger
        }

        val gradleContent = gradleFile.readText().filter { !it.isWhitespace() }
        val commons = commonMainRegex.find(gradleContent)?.groups?.get(0)?.value?:""
        val android = androidMainRegex.find(gradleContent)?.groups?.get(0)?.value?:""

        implementationRegex.findAll("$commons$android").forEach { match ->
            if(!match.value.contains(exclusionMark)){
                requiredDependencies.add(match.value.replaceVariable())
            }
        }

        exclusionRegex.findAll("$commons$android").forEach { match ->
            val str = match.value
                .replace(exclusionMark, "")
                .replaceVariable()

            requiredDependencies.remove(str)
        }

        return logger
    }

    private fun String.toDotCase() = this.map {
        if(it.isUpperCase()) ".${it.lowercase()}" else it
    }.joinToString("")

    private fun get(key: String) = properties[key]
        ?: throw KlutterConfigException("klutter.properties is missing property: $key")

    private fun String.replaceVariable() =
        if(this.contains("$")) {
            var variable = this
                .substringAfter("$")
                .substringBeforeLast("\"")
                .toDotCase()

            variable = get(variable)

            var dep = this.substringBefore("$")
            dep = dep.replace("(", " ")
            dep = dep.replace("\"", "'")

            "$dep$variable'"
        } else this.replace("(", " ")
            .replace(")", "")
            .replace("\"", "'")

}
