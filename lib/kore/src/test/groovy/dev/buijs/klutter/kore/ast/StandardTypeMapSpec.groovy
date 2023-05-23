package dev.buijs.klutter.kore.ast

import dev.buijs.klutter.kore.KlutterException
import spock.lang.Specification

class StandardTypeMapSpec extends Specification {

    def "[StandardTypeMap] returns a valid Dart data type"() {
        expect:
        StandardTypeMap.toDartType("Double") == "double"
        StandardTypeMap.toDartType("Int") == "int"
        StandardTypeMap.toDartType("Boolean") == "bool"
        StandardTypeMap.toDartType("String") == "String"
    }

    def "[StandardTypeMap] returns a valid Kotlin data type"() {
        expect:
        StandardTypeMap.toKotlinType("double") == "Double"
        StandardTypeMap.toKotlinType("int") == "Int"
        StandardTypeMap.toKotlinType("bool") == "Boolean"
        StandardTypeMap.toKotlinType("String") == "String"
    }

    def "[StandardTypeMap] an exception is thrown when a Kotlin type does not exist"() {
        when:
        StandardTypeMap.toKotlinType("Stttttring!")

        then:
        KlutterException e = thrown()
        e.getMessage() == "No such type in KotlinDartMap: Stttttring!"
    }

    def "[StandardTypeMap] an exception is thrown when a Dart type does not exist"() {
        when:
        StandardTypeMap.toDartType("Stttttring!")

        then:
        KlutterException e = thrown()
        e.getMessage() == "No such type in KotlinDartMap: Stttttring!"
    }

    def "[StandardTypeMap] an exception is thrown when a Dart/Kotlin type does not exist"() {
        when:
        StandardTypeMap.toMap("Stttttring!")

        then:
        KlutterException e = thrown()
        e.getMessage() == "No such type in KotlinDartMap: Stttttring!"
    }

    def "[StandardTypeMap] null is returned when a Dart/Kotlin type does not exist"() {
        expect:
        StandardTypeMap.toMapOrNull("Stttttring!") == null
    }

}