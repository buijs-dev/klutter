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

import dev.buijs.klutter.core.KlutterLogger
import dev.buijs.klutter.core.KlutterVisitor
import java.io.File

/**
 * @author Gillian Buijs
 */
internal class IosPodspecVisitor(private val podspec: File) : KlutterVisitor {

    private val excludeForPod = "spec.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }"
    private val excludeForUser = "spec.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }"
    private val excludedComment = "#These lines are added by the Klutter Framework to enable the app to run on a simulator"

    override fun visit(): KlutterLogger {
        val logger = KlutterLogger()
        val podspecName = podspec.name.substringBefore(".podspec")
        val rawContent = podspec.readText()
        val hasExcludedForPod = rawContent.contains(excludeForPod)
        val hasExcludedForUser = rawContent.contains(excludeForUser)
        val mustEditLine = !hasExcludedForPod && !hasExcludedForUser
        val newPodspecContent = podspec.readLines().asSequence()
            .map { line ->
                when {
                    line.contains("spec.ios.deployment_target") -> {
                        logger.debug("Adding lines to file $podspec to exclude iphonesimulator SDK")

                        if(!mustEditLine){
                            line
                        } else

                        """ |$line
                            |
                            |    $excludedComment
                            |    ${if(hasExcludedForPod) "" else excludeForPod}
                            |    ${if(hasExcludedForUser) "" else excludeForUser}
                        """.trimMargin()
                    }

                    line.filter { !it.isWhitespace() }.contains("spec.vendored_frameworks=\"build") -> {
                        logger.debug("Changing line in $podspec to use fat-framework")
                        "    spec.vendored_frameworks      = \"build/fat-framework/debug/${podspecName}.framework\""
                    }

                    else -> line
                }
            }
            .toList()

        newPodspecContent.joinToString(separator = "\n").also {
            podspec.writeText(it)
            logger.debug("Written content to file $podspec:\r\n$it")
        }
        return logger
    }

}