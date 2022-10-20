package dev.buijs.klutter.kore.ast

import dev.buijs.klutter.kore.utils.EitherNok
import dev.buijs.klutter.kore.utils.EitherOk
import spock.lang.Specification

class TypeMemberFactorySpec extends Specification {

    def "If regex finds no match then result is EitherNok with error message"() {
        expect:
        with(TypeMemberFactoryKt.toTypeMember(line)) {
            it instanceof EitherNok<String, TypeMember>
            it.data == "Field member could not be processed: '$line'"
        }

        where:
        line << [
                "varrrr x: X",
                "vall x: X",
                "val x: ?X",
                "rval x: ?X",
                "val x: X..",
                "valx:X",
        ]
    }

    def "If field is mutable then result is EitherNok with error message"() {
        expect:
        with(TypeMemberFactoryKt.toTypeMember(line)) {
            it instanceof EitherNok<String, TypeMember>
            it.data == "Field member is mutable: 'var x: X'"
        }

        where:
        line = "var x: X"
    }

    def "If StandardType processing failes then result is EitherNok with error message"() {
        expect:
        with(TypeMemberFactoryKt.toTypeMember(line)) {
            it instanceof EitherNok<String, TypeMember>
            it.data == "KeyType of Map could not be processed: ''"
        }

        where:
        line = "val x: Map<,WheresMyKey>"
    }

    def "If field is immutable and valid then result is EitherOk with TypeMember"() {
        expect:
        with(TypeMemberFactoryKt.toTypeMember(line)) {
            it instanceof EitherOk<String, TypeMember>
            it.data.name == name
            if(isCustom) {
                it.data.type instanceof CustomType
                it.data.type.className == typeName
            } else {
                it.data.type instanceof StandardType
                it.data.type.kotlinType == typeName
            }

        }

        where:
        line                                 | name        | isCustom  | typeName
        "val x: Xxxx"                        | "x"         | true      | "Xxxx"
        "val string: String"                 | "string"    | false     | "String"
        "val aMap4You: Map<String,XX>"       | "aMap4You"  | false     | "Map"
        "val maybeList: List<Boolean>?"      | "maybeList" | false     | "List"
    }
}
