package dev.buijs.klutter.core.shared

import dev.buijs.klutter.core.StringUtilsKt
import spock.lang.Specification

class StringUtilsSpec extends Specification {

    def "[unwrapFromList] returns value if no match found"(){
        expect:
        "List<>" == StringUtilsKt.unwrapFromList("List<>")
    }

}
