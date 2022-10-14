package dev.buijs.klutter.kore.ast

import dev.buijs.klutter.kore.utils.EitherNok
import dev.buijs.klutter.kore.utils.EitherOk
import spock.lang.Specification

import java.nio.file.Files

class ResponseFactorySpec extends Specification {

    def "Verify converting a KlutterResponse dto to AbstractType"() {
        given:
        def file = Files.createTempFile("SomeFile", ".kt").toFile()

        and:
        file.write(classBody)

        when:
        def result = ResponseFactoryKt.findKlutterResponses([file])

        then:
        result instanceof EitherOk<List<String>, List<AbstractType>>

        and:
        def datatypes = result.data as List<AbstractType>

        and:
        CustomType something = datatypes[0] as CustomType
        something.name == "Something"
        something.fields.size() == 2
        something.fields[0].name == "x"
        something.fields[0].type instanceof NullableStringType
        something.fields[1].name == "y"
        something.fields[1].type instanceof CustomType
        CustomType somethingElseMember = something.fields[1].type as CustomType
        somethingElseMember.name == "SomethingElse"
        somethingElseMember.fields.size() == 2

        //CustomType 'SomethingElse' with fields:
        // [TypeMember(name=a, type=NullableIntType),
        // TypeMember(name=b, type=ListType)]
        CustomType somethingElse = datatypes[1] as CustomType
        somethingElse.name == "SomethingElse"
        somethingElse.fields.size() == 2
        somethingElse.fields[0].type instanceof NullableIntType
        somethingElse.fields[1].type instanceof ListType
        ListType listType = somethingElse.fields[1].type as ListType
        listType.child instanceof BooleanType

        where:
        classBody = """
            @Serializable
            @KlutterResponse
            open class Something(
                val x: String?,
                val y: SomethingElse
            ): KlutterJSON<Something>() {
        
                override fun data() = this
        
                override fun strategy() = serializer()
        
            }
        
            @Serializable
            @KlutterResponse
            open class SomethingElse(
                val a: Int?,
                val b: List<Boolean>
            ): KlutterJSON<SomethingElse>() {
        
                override fun data() = this
        
                override fun strategy() = serializer()
        
            }"""

    }

    def "If an error occurs then findKlutterResponses returns EitherNok"() {
        given:
        def file = Files.createTempFile("SomeFile", ".kt").toFile()

        and:
        file.write(classBody)

        when:
        def result = ResponseFactoryKt.findKlutterResponses([file])

        then:
        result instanceof EitherNok<List<String>, List<AbstractType>>

        and:
        result.data.size() == 1
        result.data[0] == "Unknown KlutterResponse TypeMember: [SomethingElse]"

        where: "invalid because SomethingElse is not found"
        classBody = """
            @Serializable
            @KlutterResponse
            open class Something(
                val x: String?,
                val y: SomethingElse
            ): KlutterJSON<Something>() {
        
                override fun data() = this
        
                override fun strategy() = serializer()
        
            }"""
    }

    // TODO klutterResponse without fields

    def "If multiple KlutterResponse are found with identical names then Either.nok is returned."() {
        given:
        def file = Files.createTempFile("SomeFile", ".kt").toFile()

        and:
        file.write(classBody)

        when:
        def result = ResponseFactoryKt.findKlutterResponses([file])

        then:
        result instanceof EitherNok<List<String>, List<AbstractType>>

        and:
        result.data.size() == 1
        result.data[0] == "KlutterResponse contract violation! Duplicate class names: [Something]"

        where: "invalid because SomethingElse is not found"
        classBody = """
            @Serializable
            @KlutterResponse
            open class Something(
                val x: String?,
                val y: Boolean,
            ): KlutterJSON<Something>() {
        
                override fun data() = this
        
                override fun strategy() = serializer()
        
            }
            
            @Serializable
            @KlutterResponse
            open class Something(
                val x: String?,
                val y: Boolean,
            ): KlutterJSON<Something>() {
        
                override fun data() = this
        
                override fun strategy() = serializer()
        
            }
            """
    }

    // TODO missing @Serializable

    // TODO missing @KlutterResponse

}
