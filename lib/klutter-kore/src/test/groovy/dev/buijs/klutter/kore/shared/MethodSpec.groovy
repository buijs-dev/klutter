package dev.buijs.klutter.kore.shared

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.ast.StandardTypeMap
import dev.buijs.klutter.kore.project.*
import dev.buijs.klutter.kore.test.TestResource
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

class MethodSpec extends Specification {

    @Shared
    def resources = new TestResource()

    def "Verify Method constructor"(){
        expect:
        with(new Method(
                "a",
                "b",
                "c",
                true,
                "String",
                false,
                false,
        )){
            it.command == "a"
            it.import == "b"
            it.method == "c"
            it.dataType == "String"
            it.async
            !it.nullable
            !it.stateful
        }
    }

    def "[asDataType] #dataType is returned as #expected"() {
        expect:
        MethodKt.asDataType(dataType, lang) == expected

        where:
        dataType        | expected          | lang
        "Stttttring!"   | "Stttttring!"     | Language.KOTLIN
        "Int  "         | "Int"             | Language.KOTLIN
        "  Double"      | "Double"          | Language.KOTLIN
        "Boolean"       | "Boolean"         | Language.KOTLIN
        "String"        | "String"          | Language.KOTLIN
        "int"           | "Int"             | Language.KOTLIN
        "double"        | "Double"          | Language.KOTLIN
        "bool"          | "Boolean"         | Language.KOTLIN
        "String"        | "String"          | Language.KOTLIN
        "Stttttring!"   | "Stttttring!"     | Language.DART
        "Int"           | "int"             | Language.DART
        "Double"        | "double"          | Language.DART
        "Boolean  "     | "bool"            | Language.DART
        "  String?"     | "String"          | Language.DART
        "int"           | "int"             | Language.DART
        "double"        | "double"          | Language.DART
        "bool"          | "bool"            | Language.DART
        "  String  "    | "String"          | Language.DART
    }

    def "[toMethod] an empty list is returned when no methods are found"() {
        given:
        def file = Files.createTempFile("SomeClass", "kt").toFile()

        expect:
        MethodKt.toMethods(file, Language.KOTLIN).isEmpty()
    }

    def "[toMethod] a list of methods is returned"() {
        given:
        def file = Files.createTempFile("SomeClass", "kt").toFile()
        resources.copy("platform_source_code", file.absolutePath)

        expect:
        !MethodKt.toMethods(file, lang).isEmpty()

        where:
        lang << [Language.KOTLIN, Language.DART]
    }

    def "[toMethod] throws an exception if a return type contains Lists with null values"() {
        given:
        def file = Files.createTempFile("SomeClass", "kt").toFile()
        file.write(classBody)

        when:
        MethodKt.toMethods(file, Language.KOTLIN)

        then:
        KlutterException e = thrown()
        e.message == "Failed to convert datatype. Lists may not contain null values: 'List<String?>'"

        where:
        classBody = """
        package foo.bar.baz

        import dev.buijs.klutter.annotations.Annotations
        
            class FakeClass {
                @KlutterAdaptee(name = "DartMaul")
                fun foo(): String {
                    return "Maul"
                }
            
                @KlutterAdaptee(name = "BabyYoda")
                fun fooBar(): List<String?> {
                    return listOf("baz")
                }
            }
            
            @Serializable
            @KlutterResponse
            enum class {
                @SerialName("boom") BOOM,
                @SerialName("boom boom") BOOM_BOOM,
            }
        """
    }

    def "[toMethod] returns empty list if class name is undetermined"() {
        given:
        def file = Files.createTempFile("SomeClass", "kt").toFile()

        and:
        file.write(classBody)

        expect:
        MethodKt.toMethods(file, Language.KOTLIN).isEmpty()

        where:
        classBody = """
        package foo.bar.baz

        import dev.buijs.klutter.annotations.Annotations
     
        @KlutterAdaptee(name = "DartMaul")
        fun foo(): String {
            return "Maul"
        }
    
        @KlutterAdaptee(name = "BabyYoda")
        fun fooBar(): List<String?> {
            return listOf("baz")
        }
        
        @Serializable
        @KlutterResponse
        enum class {
            @SerialName("boom") BOOM,
            @SerialName("boom boom") BOOM_BOOM,
        }
        """
    }

    def "[toMethod] returns correct data types if context is an argument"() {
        given:
        def file = Files.createTempFile("Platform", "kt").toFile()

        and:
        file.write(classBody)

        when:
        def methods = MethodKt.toMethods(file, Language.KOTLIN)

        then:
        methods.size() == 1

        and:
        def method = methods.first()

        and:
        method.nullable
        method.async
        method.command == "getBatteryLevel"
        method.import == "com.example.batterylevel.platform.Platform"
        method.method == "Platform().getBatteryLevel(context)"
        method.dataType == "Double"

        where:
        classBody << [
                """
                    package com.example.batterylevel.platform
                
                    import dev.buijs.klutter.annotations.kmp.*
                
                    class Platform {
                
                        @AndroidContext
                        @KlutterAdaptee("getBatteryLevel")
                        suspend fun getBatteryLevel(context: Any): Double? {
                            return BatteryLevel(context).level.toDouble()
                        }
                
                    }
                """,

                """
                    package com.example.batterylevel.platform
                
                    import dev.buijs.klutter.annotations.kmp.*
                
                    class Platform {
                
                       
                        @KlutterAdaptee("getBatteryLevel")
                        @AndroidContext
                        suspend fun getBatteryLevel(context: Any): Double? {
                            return BatteryLevel(context).level.toDouble()
                        }
                
                    }
                """,

                """
                    package com.example.batterylevel.platform
                
                    import dev.buijs.klutter.annotations.kmp.*
                
                    class Platform {
                
                        @AndroidContext
                        @KlutterAdaptee("getBatteryLevel")
                        @AndroidContext
                        suspend fun getBatteryLevel(context: Any): Double? {
                            return BatteryLevel(context).level.toDouble()
                        }
                
                    }
                """

        ]
    }

    def "[toChannelName] pluginPackage name is returned"() {
        given:
        Pubspec pubspec = new Pubspec("Na!", new PubspecFlutter(plugin))

        expect:
        MethodKt.toChannelName(pubspec) == expected

        where:
        plugin << [
                null,
                new PubspecPlugin(null),
                new PubspecPlugin(new PubspecPluginPlatforms(null, null)),
                new PubspecPlugin(new PubspecPluginPlatforms(new PubspecPluginClass(null, null), null)),
                new PubspecPlugin(new PubspecPluginPlatforms(new PubspecPluginClass("Batman!", null), null))
        ]

        expected << [
                "Na!.klutter",
                "Na!.klutter",
                "Na!.klutter",
                "Na!.klutter",
                "Batman!",
        ]
    }

    def "[toMethod] returns NOTHING type correctly"() {
        given:
        def file = Files.createTempFile("", "").toFile()
        file.write(fileContent)

        when:
        def methods = MethodKt.toMethods(file, Language.DART)

        then:
        methods.size() == 1

        and:
        with(methods.first()) {
            it.dataType == "void"
        }

        where:
        fileContent << [
                '''
                    package com.example
        
                    class Greeting {
                        @KlutterAdaptee(name = "stopBroadcast")
                        fun stopBroadcast() {
                            _shouldBroadcast = false
                        }
                    }
                ''',
                '''
                    package com.example
        
                    class Greeting {
                        @KlutterAdaptee(name = "stopBroadcast")
                        fun stopBroadcast(): Unit {
                            _shouldBroadcast = false
                        }
                    }
                ''']
    }

}