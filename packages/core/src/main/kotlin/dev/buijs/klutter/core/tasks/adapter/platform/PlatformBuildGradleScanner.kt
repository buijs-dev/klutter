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

package dev.buijs.klutter.core.tasks.adapter.platform

import dev.buijs.klutter.core.AndroidConfig
import java.io.File

/**
 * @author Gillian Buijs
 */
internal class PlatformBuildGradleScanner(private val buildGradle: File)  {

    private val iosVersionRegex = """kotlin[^{]+?\{[\w\W]+?cocoapods[^{]+?\{[\w\W]+?deploymentTarget[\w\W]+?"([^"]+?)"""".toRegex()
    private val compileSdkRegex = """android[\w\W]+?compileSdk[\w\W]+?([0-9]+)""".toRegex()
    private val targetSdkRegex = """android[\w\W]+?defaultConfig[\w\W]+?\{[\w\W]+targetSdk[\w\W]+?([0-9]+)""".toRegex()
    private val minSdkRegex = """android[\w\W]+?defaultConfig[\w\W]+?\{[\w\W]+minSdk[\w\W]+?([0-9]+)""".toRegex()

    /**
     * Get the Android configuration.
     *
     * Given this DSL:
     *
     * android {
     *   compileSdk = 31
     *   sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
     *   defaultConfig {
     *      minSdk = 21
     *      targetSdk = 31
     *   }
     * }
     *
     * Then it would return compileSdk 31, minSdk 21, targetSdk 31.
     *
     * Returns [AndroidConfig].
     */
    fun androidConfig(): AndroidConfig {
        val content = buildGradle.readText()

        val minSdk = minSdkRegex.find(content)?.groupValues?.get(1)?.toInt() ?: 21
        val targetSdk = targetSdkRegex.find(content)?.groupValues?.get(1)?.toInt() ?: 31
        val compileSdk = compileSdkRegex.find(content)?.groupValues?.get(1)?.toInt() ?: 31

        return AndroidConfig(
            minSdk = minSdk,
            targetSdk = targetSdk,
            compileSdk = compileSdk,
        )
    }

    /**
     * Get the IOS version from the build.gradle.kts.
     *
     * Given this DSL:
     *
     *         kotlin {
     *           ...
     *
     *           cocoapods {
     *              summary = "Some description for the Shared Module"
     *              homepage = "Link to the Shared Module homepage"
     *              ios.deploymentTarget = "14.1"
     *              ...
     *           }
     *       }
     *
     * Then it would return version '14.1'.
     *
     * Returns [Int] IOS version code.
     */
    fun iosVersion(): String {
        val content = buildGradle.readText()
        val maybeIosVersion = iosVersionRegex.find(content)
        val iosVersion = maybeIosVersion?.groupValues?.get(1)
        return iosVersion ?: "13.0"
    }
}