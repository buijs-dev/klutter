package dev.buijs.klutter.core

import dev.buijs.klutter.core.annotations.ReturnTypeLanguage
import dev.buijs.klutter.core.test.TestResource
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

class MethodSpec extends Specification {

    @Shared
    def resources = new TestResource()

    def "Verify Method constructor"(){
        expect:
        with(new Method("a", "b", "c", true, "String")){
            it.command == "a"
            it.import == "b"
            it.method == "c"
            it.dataType == "String"
            it.async
        }
    }

    def "[DartKotlinMap] returns a valid Dart data type"() {
        expect:
        DartKotlinMap.toDartType("Double") == "double"
        DartKotlinMap.toDartType("Int") == "int"
        DartKotlinMap.toDartType("Boolean") == "bool"
        DartKotlinMap.toDartType("String") == "String"
    }

    def "[DartKotlinMap] returns a valid Kotlin data type"() {
        expect:
        DartKotlinMap.toKotlinType("double") == "Double"
        DartKotlinMap.toKotlinType("int") == "Int"
        DartKotlinMap.toKotlinType("bool") == "Boolean"
        DartKotlinMap.toKotlinType("String") == "String"
    }

    def "[DartKotlinMap] an exception is thrown when a Kotlin type does not exist"() {
        when:
        DartKotlinMap.toKotlinType("Stttttring!")

        then:
        KlutterException e = thrown()
        e.getMessage() == "No such kotlinType in KotlinDartMap: Stttttring!"
    }

    def "[DartKotlinMap] an exception is thrown when a Dart type does not exist"() {
        when:
        DartKotlinMap.toDartType("Stttttring!")

        then:
        KlutterException e = thrown()
        e.getMessage() == "No such dartType in KotlinDartMap: Stttttring!"
    }

    def "[DartKotlinMap] an exception is thrown when a Dart/Kotlin type does not exist"() {
        when:
        DartKotlinMap.toMap("Stttttring!")

        then:
        KlutterException e = thrown()
        e.getMessage() == "No such dartType or kotlinType in KotlinDartMap: Stttttring!"
    }

    def "[DartKotlinMap] null is returned when a Dart/Kotlin type does not exist"() {
        expect:
        DartKotlinMap.toMapOrNull("Stttttring!") == null
    }

    def "[asDataType] #dataType is returned as #expected"() {
        expect:
        MethodKt.asDataType(dataType, lang) == expected

        where:
        dataType        | expected          | lang
        "Stttttring!"   | "Stttttring!"     | ReturnTypeLanguage.KOTLIN
        "Int"           | "Int"             | ReturnTypeLanguage.KOTLIN
        "Double"        | "Double"          | ReturnTypeLanguage.KOTLIN
        "Boolean"       | "Boolean"         | ReturnTypeLanguage.KOTLIN
        "String"        | "String"          | ReturnTypeLanguage.KOTLIN
        "int"           | "Int"             | ReturnTypeLanguage.KOTLIN
        "double"        | "Double"          | ReturnTypeLanguage.KOTLIN
        "bool"          | "Boolean"         | ReturnTypeLanguage.KOTLIN
        "String"        | "String"          | ReturnTypeLanguage.KOTLIN
        "Stttttring!"   | "Stttttring!"     | ReturnTypeLanguage.DART
        "Int"           | "int"             | ReturnTypeLanguage.DART
        "Double"        | "double"          | ReturnTypeLanguage.DART
        "Boolean"       | "bool"            | ReturnTypeLanguage.DART
        "String"        | "String"          | ReturnTypeLanguage.DART
        "int"           | "int"             | ReturnTypeLanguage.DART
        "double"        | "double"          | ReturnTypeLanguage.DART
        "bool"          | "bool"            | ReturnTypeLanguage.DART
        "String"        | "String"          | ReturnTypeLanguage.DART
    }

    def "[toMethod] an empty list is returned when no methods are found"() {
        given:
        def file = Files.createTempFile("SomeClass", "kt").toFile()

        expect:
        MethodKt.toMethod(file, ReturnTypeLanguage.KOTLIN).isEmpty()
    }

    def "[toMethod] a list of methods is returned"() {
        given:
        def file = Files.createTempFile("SomeClass", "kt").toFile()
        resources.copy("platform_source_code", file.absolutePath)

        expect:
        !MethodKt.toMethod(file, ReturnTypeLanguage.KOTLIN).isEmpty()
    }

    def "[packageName] returns null if file contains no package name"() {
        expect:
        MethodKt.packageName("") == ""
    }

    def "[packageName] package name is returned"() {
        expect:
        MethodKt.packageName("package a.b.c") == "a.b.c"
    }
}