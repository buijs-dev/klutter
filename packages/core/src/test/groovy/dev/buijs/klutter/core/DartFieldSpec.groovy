package dev.buijs.klutter.core


import spock.lang.Specification

class DartFieldSpec extends Specification {

    def "Processing a a String that does not match the regex returns null"() {
        expect:
        DartFieldKt.toDartField(line) == null

        where:
        line << ["", "WHAT", "WOULD", "BATMAN", "DO?", "val foo!=mc2"]
    }

    def "Verify processing returns a DartField"() {
        expect:
        with(DartFieldKt.toDartField(input)) {
            it.name == name
            it.type == type
            it.isList() == isList
            it.isOptional() == isOptional
            it.isCustomType() == isCustomType
        }

        where:
        input                       | type              | name      | isList    | isOptional    | isCustomType
        "val foo: List<Sidekick>"   | "Sidekick"        | "foo"     | true      | false         | true
        "val foo: List<String>"     | "String"          | "foo"     | true      | false         | false
        "val foo: List<Sidekick>?"  | "Sidekick"        | "foo"     | true      | true          | true
        "val foo: List<String>?"    | "String"          | "foo"     | true      | true          | false
        "val foo: Sidekick"         | "Sidekick"        | "foo"     | false     | false         | true
        "val foo: String"           | "String"          | "foo"     | false     | false         | false
        "val foo: Sidekick?"        | "Sidekick"        | "foo"     | false     | true          | true
        "val foo: String?"          | "String"          | "foo"     | false     | true          | false

    }



}