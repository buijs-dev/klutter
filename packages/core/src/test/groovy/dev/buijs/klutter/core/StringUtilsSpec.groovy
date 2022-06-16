package dev.buijs.klutter.core


import spock.lang.Specification

class StringUtilsSpec extends Specification {

    def "[unwrapFromList] returns value if no match found"(){
        expect:
        "List<>" == StringUtilsKt.unwrapFromList("List<>")
    }

}