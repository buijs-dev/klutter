package dev.buijs.klutter.kore.common

import spock.lang.Specification

class EitherSpec extends Specification {

    def "Verify Either.ok helper"(){
        given:
        def ok = Either.ok("MyData")

        expect:
        ok instanceof EitherOk
        ok.data == "MyData"
    }

    def "Verify Either.nok helper"(){
        given:
        def nok = Either.nok(99)

        expect:
        nok instanceof EitherNok
        nok.data == 99
    }
}