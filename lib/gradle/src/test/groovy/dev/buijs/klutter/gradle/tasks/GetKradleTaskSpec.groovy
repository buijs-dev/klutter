package dev.buijs.klutter.gradle.tasks

import spock.lang.Specification

class GetKradleTaskSpec extends Specification {

    def "Verify kradlew scripts can be retrieved from resources"() {
        expect:
        GetKradleTaskKt.kradlew.available()
        GetKradleTaskKt.kradlewBat.available()
    }

}