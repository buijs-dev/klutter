package dev.buijs.klutter.kore.shared

import dev.buijs.klutter.kore.shared.StringUtilsKt
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

}
