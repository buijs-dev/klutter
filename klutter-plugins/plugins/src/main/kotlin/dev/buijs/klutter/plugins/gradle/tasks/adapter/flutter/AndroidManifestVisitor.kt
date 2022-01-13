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

package dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter


import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.module.kotlin.readValue
import dev.buijs.klutter.core.KlutterCodeGenerationException
import dev.buijs.klutter.core.KlutterLogger
import dev.buijs.klutter.core.KlutterVisitor
import java.io.File


/**
 * Class which may or may not edit the AndroidManifest file in flutter/android/app/src/main folder.
 *
 * @author Gillian Buijs
 */
internal class AndroidManifestVisitor(
    private val manifestFile: File
): KlutterVisitor {

    private val xmlMapper = XmlMapper(JacksonXmlModule()
        .apply { setDefaultUseWrapper(false) })
        .apply { enable(SerializationFeature.INDENT_OUTPUT) }
        .apply { configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) }

    override fun visit(): KlutterLogger {
        if(!manifestFile.exists()) {
            throw KlutterCodeGenerationException("Could not locate AndroidManifest file at path: ${manifestFile.absolutePath}")
        }

        val logger = KlutterLogger()
        val rawContent = manifestFile.readText()
        val parsedXml: AndroidManifestXML = xmlMapper.readValue(rawContent)

        if(parsedXml.application?.activity?.androidExported == null) {
            val output = mutableListOf<String>()
            for (line in rawContent.reader().readLines()) {
                output.add(line)
                if(line.contains("<activity")) {
                    output.add("""            android:exported="true"""")
                    logger.debug("""Added line to $manifestFile:  'android:exported="true"'""")
                }
            }
            manifestFile.delete()
            manifestFile.createNewFile()
            manifestFile.writeText(output.joinToString("\r\n"))
            return logger
        }

        logger.debug("AndroidManifest file is OK, no editing done.")
        return logger
    }
}

@JacksonXmlRootElement(localName = "manifest")
private class AndroidManifestXML {
    val application: AndroidManifestApplication? = null
}

@JacksonXmlRootElement(localName = "application")
private class AndroidManifestApplication {
    val activity: AndroidActivity? = null
}

@JacksonXmlRootElement(localName = "activity")
private class AndroidActivity {
    @field:JacksonXmlProperty(
        namespace = "android",
        localName = "exported",
        isAttribute = true, )
    val androidExported: String? = null
}