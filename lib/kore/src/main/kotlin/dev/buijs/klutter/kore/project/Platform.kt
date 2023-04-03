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

package dev.buijs.klutter.kore.project

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.shared.verifyExists
import java.io.File

/**
 * Wrapper class with a file instance pointing to the kmp sub-module in root/platform.
 *
 * @property folder path to the Platform folder.
 */
class Platform(
    val folder: File,
    private val pluginName: String,
) {

    /**
     * Function to return the location of the src module containing the common/shared platform code.
     *
     * @throws KlutterException if file(s) do not exist.
     * @return the absolute path to the common source code.
     */
    fun source() = folder
        .verifyExists()
        .resolve("src/commonMain")
        .verifyExists()

    /**
     * Function to return the location of the podspec in the platform sub-module.
     *
     * @throws KlutterException if file(s) do not exist.
     * @return the absolute path to the ios Podfile.
     */
    fun podspec() = folder
        .verifyExists()
        .resolve("$pluginName.podspec")
        .verifyExists()
}