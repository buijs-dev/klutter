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

package dev.buijs.klutter.plugins.gradle

import dev.buijs.klutter.core.shared.KlutterDSL
import dev.buijs.klutter.core.shared.KlutterDSLBuilder
import dev.buijs.klutter.core.shared.KlutterDTO
import java.io.File

@DslMarker
internal annotation class KlutterApplicationDSLMarker

@KlutterApplicationDSLMarker
class KlutterApplicationDSL: KlutterDSL<KlutterApplicationBuilder> {
    override fun configure(lambda: KlutterApplicationBuilder.() -> Unit): KlutterApplicationDTO {
        return KlutterApplicationBuilder().apply(lambda).build()
    }
}

@KlutterApplicationDSLMarker
class KlutterApplicationBuilder: KlutterDSLBuilder {

    var name: String = ""
    var root: File? = null
    var uiBuildFolder: File? = null
    var uiOutputFolder: File? = null
    var uiTestFolder: File? = null

    override fun build() = KlutterApplicationDTO(
        name = name,
        root = root,
        buildFolder = uiBuildFolder,
        outputFolder = uiOutputFolder,
        uiTestFolder = uiTestFolder,
    )

}

/**
 * DTO for storing shared plugin configuration.
 */
data class KlutterApplicationDTO(
    val name: String,
    val root: File?,
    val buildFolder: File?,
    val outputFolder: File?,
    val uiTestFolder: File?,
): KlutterDTO