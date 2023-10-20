package dev.buijs.klutter.gradle.tasks

import spock.lang.Specification

class GetKradleTaskSpec extends Specification {

    def "Verify kommand zip can be retrieved from resources"() {
        expect:
        GetKradleTaskKt.kradleWrapperJar.available()
        GetKradleTaskKt.kradlew.available()
        GetKradleTaskKt.kradlewBat.available()
    }

}