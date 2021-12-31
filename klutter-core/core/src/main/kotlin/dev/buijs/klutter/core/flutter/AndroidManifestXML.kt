package dev.buijs.klutter.core.flutter

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "manifest")
class AndroidManifestXML {
    @field:JacksonXmlProperty(localName = "application")
    val application: AndroidManifestApplication? = null
}

class AndroidManifestApplication {
    @field:JacksonXmlProperty(isAttribute = true, namespace = "android")
    val androidExported: String? = null
}