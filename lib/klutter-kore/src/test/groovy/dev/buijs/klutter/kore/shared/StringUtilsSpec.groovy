package dev.buijs.klutter.kore.shared


import spock.lang.Specification

class StringUtilsSpec extends Specification {

    def "[unwrapFromList] returns value if no match found"(){
        expect:
        "List<>" == StringUtilsKt.unwrapFromList("List<>")
    }

    def "[findClassName] returns null if match is not found"(){
        expect:
        null == StringUtilsKt.findClassName("")
    }

    def "[prefixIfNot] does nothing when prefix is present"() {
        given:
        def prefix = "MyPrefixString"

        expect:
        "MyPrefixString" == StringUtilsKt.prefixIfNot("MyPrefixString", prefix)
    }

    def "[prefixIfNot] adds prefix if not present"() {
        given:
        def prefix = "MyPrefix"

        expect:
        "MyPrefixString" == StringUtilsKt.prefixIfNot("String", prefix)
    }

}
