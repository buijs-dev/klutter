package dev.buijs.klutter.core

import spock.lang.Specification

import java.nio.file.Files

class SecretsSpec extends Specification {

    def "Verify creating an instance of Secrets by supplying a location"() {

        given:
        def temp = Files.createTempDirectory("buildsrc")
        def properties = temp.resolve("klutter-secrets.properties").toFile()
        properties.createNewFile()
        properties.write(
                "store.file.uri=x\nstore.password=y\nkey.alias=me\nkey.password=pass\n"
        )

        when:
        def sut = Klutter.secrets(temp.toAbsolutePath().toFile().absolutePath)

        then:
        sut["store.file.uri"] == "x"
        sut["store.password"] == "y"
        sut["key.alias"] == "me"
        sut["key.password"] == "pass"

    }

}
