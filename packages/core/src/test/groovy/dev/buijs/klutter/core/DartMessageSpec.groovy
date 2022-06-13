package dev.buijs.klutter.core


import spock.lang.Specification

class DartMessageSpec extends Specification {

    def "Constructing a DartMessage without fields throws an exception"() {
        when:
        new DartMessage("", [])

        then:
        KlutterException e = thrown()
        e.message == "Invalid DartMessage: List of fields is empty."
    }

}