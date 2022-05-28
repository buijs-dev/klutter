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

package dev.buijs.klutter.plugins.gradle.dsl

@DslMarker
internal annotation class KlutterAppInfoDSLMarker

@KlutterAppInfoDSLMarker
class KlutterAppInfoDSL: KlutterDSL<KlutterAppInfoBuilder> {
    override fun configure(lambda: KlutterAppInfoBuilder.() -> Unit): KlutterAppInfoDTO {
        return KlutterAppInfoBuilder().apply(lambda).build()
    }
}

@KlutterAppInfoDSLMarker
class KlutterAppInfoBuilder: KlutterDSLBuilder {

    var name: String = ""
    var version: String = ""
    var description: String = ""
    var projectFolder = ""
    var outputFolder = ""

    override fun build(): KlutterAppInfoDTO {
        return KlutterAppInfoDTO(
            name = name,
            version = version,
            appId = "dev.buijs.klutter.activity",
            description = description,
            projectFolder = projectFolder,
            outputFolder = outputFolder,
        )
    }
}

/**
 * DTO for storing shared app configuration.
 */
data class KlutterAppInfoDTO(
    val name: String,
    var version: String,
    val appId: String,
    val description: String,
    val projectFolder: String,
    val outputFolder: String,
): KlutterDTO