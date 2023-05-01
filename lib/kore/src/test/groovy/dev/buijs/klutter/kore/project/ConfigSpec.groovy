package dev.buijs.klutter.kore.project

import spock.lang.Specification

import java.nio.file.Files

class ConfigSpec extends Specification {

    def "Verify config can be deserialized" () {
        given:
        def configFile = Files.createTempFile("", "").toFile()

        and:
        configFile.write(content)

        when:
        def config = ConfigKt.toConfigOrNull(configFile)

        then:
        config != null
        config.bomVersion == "2023.1.2-SNAPSHOT"
        config.dependencies.klutter == "0.3.0"
        config.dependencies.klutterUI == "0.0.1"
        config.dependencies.squint == "0.0.6"
        config.dependencies.embedded.size() == 1
        config.dependencies.embedded[0] == "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"

        where:
        content = '''bom-version: "2023.1.2-SNAPSHOT"
dependencies:
  klutter: "0.3.0"
  klutter_ui: "0.0.1"
  squint_json: "0.0.6"
  embedded:
    - "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"'''
    }

    def "When config File does not exist then null is returned" () {
        expect:
        ConfigKt.toConfigOrNull(new File("doesNotExist")) == null
    }

}