package dev.buijs.klutter.kore.ast

import spock.lang.Specification

class InterfacesSpec extends Specification {

    def "toString returns classname"() {
        expect:
        new IntType().toString() == "IntType"
    }

    def "toString returns name and members"() {
        expect:
        new CustomType("Foo", "com.example", [new TypeMember("count", new IntType())])
                .toString() == "CustomType = Foo\n" +
                "Package = com.example\n" +
                "Fields = [count: Int]\n" +
                "Nullable = false"

        new NullableCustomType("Foo", "", [new TypeMember("count", new IntType())])
                .toString() == "CustomType = Foo\n" +
                "Package = \n" +
                "Fields = [count: Int]\n" +
                "Nullable = false"
    }

    def "copy with fields returns correct name and fields"() {
        given:
        def type1 = new CustomType("Foo", "", [new TypeMember("count", new IntType())])
        def type2 = new CustomType("Foo", "", [new TypeMember("doubleCount", new DoubleType())])

        when:
        type1 = type1.copy(type2.fields)

        then:
        type1.fields == type2.fields
    }

    def "nullable type copy with fields returns correct name and fields"() {
        given:
        def type1 = new NullableCustomType("Foo", "", [new TypeMember("count", new IntType())])
        def type2 = new NullableCustomType("Foo", "", [new TypeMember("doubleCount", new DoubleType())])

        when:
        type1 = type1.copy(type2.fields)

        then:
        type1.fields == type2.fields
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
