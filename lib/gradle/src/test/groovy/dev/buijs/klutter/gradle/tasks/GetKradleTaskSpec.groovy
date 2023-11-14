package dev.buijs.klutter.gradle.tasks

import spock.lang.Ignore
import spock.lang.Specification

import java.nio.file.Files

class GetKradleTaskSpec extends Specification {

    def "Verify kradlew scripts can be retrieved from resources"() {
        expect:
        GetKradleTaskKt.kradlew.available()
        GetKradleTaskKt.kradlewBat.available()
    }

    @Ignore // TODO get kradle from url
    def "Verify kradlew files can be copied" () {
        given:
        def root = Files.createTempDirectory("").toFile()

        when:
        GetKradleTaskKt.toGetKradleTask(root).run()

        then:
        root.toPath().resolve("kradlew").toFile().exists()
        root.toPath().resolve("kradlew.bat").toFile().exists()
        root.toPath().resolve(".kradle").resolve("kradle-wrapper.jar").toFile().exists()
    }

}