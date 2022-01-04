package dev.buijs.klutter.core.flutter

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
 * @author Gillian Buijs
 * @contact https://buijs.dev
 *
 * Takes the AndroidManifest file in
 * flutter/android/app/src/main folder
 * and makes necessary changes if needed.
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
        if(parsedXml.application?.androidExported == null) {
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
    @field:JacksonXmlProperty(localName = "application")
    val application: AndroidManifestApplication? = null
}

private class AndroidManifestApplication {
    @field:JacksonXmlProperty(isAttribute = true, namespace = "android")
    val androidExported: String? = null
}