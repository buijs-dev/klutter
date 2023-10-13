package dev.buijs.klutter.kore

import spock.lang.Specification

class ExceptionSpec extends Specification {

    def "Verify exception message"() {
        given:
        def exception = new KlutterException("BOOM!", null)

        expect:
        exception.message == "BOOM!"
    }

}
