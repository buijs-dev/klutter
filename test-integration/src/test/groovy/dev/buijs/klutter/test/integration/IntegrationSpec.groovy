package dev.buijs.klutter.test.integration

import spock.lang.Specification

class IntegrationSpec extends Specification {

    def "Verify new project creation"() {
        given:
        def project = GenerateTestProjectKt.generatePlugin()

        expect:
        project != null
    }

}
