package dev.buijs.klutter.kore

import dev.buijs.klutter.kore.KlutterException
import spock.lang.Specification

class ExceptionSpec extends Specification {

    def "Verify exception message"() {
        given:
        def exception = new KlutterException("BOOM!")

        expect:
        exception.message == "BOOM!"
    }

}
