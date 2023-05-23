package dev.buijs.klutter.kore.common


import spock.lang.Specification

class StringUtilsSpec extends Specification {

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

    def "[toSnakeCase] converts camelcase correctly"() {
        expect:
        StringUtilsKt.toSnakeCase("MyCamelCas1eValue") == "my_camel_cas1e_value"
    }

}
