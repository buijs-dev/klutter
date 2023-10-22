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
package dev.buijs.klutter.kore.tasks

import dev.buijs.klutter.kore.KlutterTask
import dev.buijs.klutter.kore.common.verifyExists
import dev.buijs.klutter.kore.project.isWindows
import java.io.File

/**
 * Copy the 'Platform.xcframework' build in the platform module
 * to the plugin ios/Klutter folder.
 */
class CopyXCFrameworkTask(
    private val pathToRoot: File,
): KlutterTask {

    private val pathToTarget: String = ""

    private val pathToSource: String = "platform"

    override fun run() {
        // XCFramework can not be build on Windows.
        if(isWindows) return

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