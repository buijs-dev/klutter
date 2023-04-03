package dev.buijs.klutter.kore.ast

import spock.lang.Specification

class InterfacesSpec extends Specification {

    def "toString returns classname"() {
        expect:
        new IntType().toString() == "IntType"
    }

    def "copy with fields returns correct name and fields"() {
        given:
        def type1 = new CustomType("Foo", "", [new TypeMember("count", new IntType())])
        def type2 = new CustomType("Foo", "", [new TypeMember("doubleCount", new DoubleType())])

        when:
        type1 = type1.copy(type2.members)

        then:
        type1.members == type2.members
    }

    def "nullable type copy with fields returns correct name and fields"() {
        given:
        def type1 = new NullableCustomType("Foo", "", [new TypeMember("count", new IntType())])
        def type2 = new NullableCustomType("Foo", "", [new TypeMember("doubleCount", new DoubleType())])

        when:
        type1 = type1.copy(type2.members)

        then:
        type1.members == type2.members
    }

    def "StandardType returns correct Kotlin and Dart type"() {
        expect:
        // L.O.L. yes use String because then
        // its clear if its a Dart or Kotlin type ;-)
        new StringType().kotlinType == "String"
        new StringType().dartType == "String"
        // Okay Int there you go:
        new IntType().kotlinType == "Int"
        new IntType().dartType == "int"
    }

}
