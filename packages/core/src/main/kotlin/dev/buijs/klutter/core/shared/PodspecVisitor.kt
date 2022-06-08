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

package dev.buijs.klutter.core.shared

import java.io.File


/**
 * Visitor which adds EXCLUDED_ARCHS for iphone simulator if not present.
 *
 * These excludes are needed to be able to run the app on a simulator.
 */
fun File.excludeArm64() {
    val regex = "Pod::Spec.new.+?do.+?\\|([^|]+?)\\|".toRegex()

    /** Check the prefix used in the podspec or default to 's'.
     *
     *  By default the podspec file uses 's' as prefix.
     *  In case a podspec does not use this default,
     *  this regex will find the custom prefix.
     *
     *  If not found then 's' is used.
     */
    val prefix = regex.find(readText())?.groupValues?.let { it[1] } ?: "s"

    // INPUT
    val lines = readLines()

    // OUTPUT
    val newLines = mutableListOf<String>()

    for(line in lines){
        newLines.add(line)

        // Check if line contains Flutter dependency (which should always be present).
        // If so then add the vendored framework dependency.
        // This is done so the line is added at a fixed point in the podspec.
        if(line.filter { !it.isWhitespace() }.contains("$prefix.dependency'Flutter'")) {
            newLines.add("""  $prefix.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }""")
            newLines.add("""  $prefix.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }""")
        }

    }

    // Write the editted line to the podspec file.
    writeText(newLines.joinToString("\n") { it })

}
