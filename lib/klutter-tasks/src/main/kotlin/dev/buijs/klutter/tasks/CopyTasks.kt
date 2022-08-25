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
package dev.buijs.klutter.tasks

import dev.buijs.klutter.kore.KlutterTask
import dev.buijs.klutter.kore.shared.verifyExists
import java.io.File

/**
 * Copy the platform release aar file build in the platform module
 * to the plugin android/Klutter folder.
 */
class CopyAndroidAarFileKlutterTask(
    private val isApplication: Boolean,
    private val pathToRoot: File,
    private val pluginName: String? = null,
): KlutterTask {

    private val pathToTarget: String =
        if(isApplication) "app/backend" else ""

    private val pathToSource: String =
        if(isApplication) "lib" else "platform"

    override fun run() {
        val name = when {
            isApplication -> {
                "lib"
            }
            pluginName != null -> {
                pluginName
            }
            else -> {
                "platform"
            }
        }

        val pathToAarFile = pathToRoot
            .resolve(pathToSource)
            .resolve("build/outputs/aar/$name-release.aar")
            .verifyExists()

        val target = pathToRoot
            .resolve(pathToTarget)
            .resolve("android/klutter")
            .verifyExists()

        pathToAarFile.renameTo(target.resolve("platform.aar"))
    }


}

/**
 * Copy the 'Platform.xcframework' build in the platform module
 * to the plugin ios/Klutter folder.
 */
class CopyIosFrameworkKlutterTask(
    isApplication: Boolean,
    private val pathToRoot: File,
): KlutterTask {

    private val pathToTarget: String =
        if(isApplication) "app/backend" else ""

    private val pathToSource: String =
        if(isApplication) "lib" else "platform"

    override fun run() {
        val target = pathToRoot
            .resolve(pathToTarget)
            .resolve("ios/Klutter")
            .verifyExists()
            .resolve("Platform.xcframework")
            .also { if(it.exists()) it.deleteRecursively() }

        val pathToIosFramework = pathToRoot
            .resolve(pathToSource)
            .resolve("build/XCFrameworks/release/Platform.xcframework")
            .verifyExists()

        pathToIosFramework.copyRecursively(target)
    }

}