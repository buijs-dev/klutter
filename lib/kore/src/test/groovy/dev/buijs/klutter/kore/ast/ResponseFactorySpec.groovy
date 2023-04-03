package dev.buijs.klutter.kore.ast

import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk
import spock.lang.Ignore
import spock.lang.Specification

import java.nio.file.Files

@Ignore // TODO
class ResponseFactorySpec extends Specification {

    def "Verify converting a KlutterResponse dto to AbstractType"() {
        given:
        def file = Files.createTempFile("SomeFile", ".kt").toFile()

        and:
        file.write(classBody)

        when:
        def result = ValidatorKt.findKlutterResponses([file])

        then:
        result instanceof EitherOk<List<String>, List<AbstractType>>

        and:
        def datatypes = result.data as List<AbstractType>

        and:
        CustomType something = datatypes[0] as CustomType
        something.className == "Something"
        something.members.size() == 2
        something.members[0].name == "x"
        something.members[0].type instanceof NullableStringType
        something.members[1].name == "y"
        something.members[1].type instanceof CustomType
        CustomType somethingElseMember = something.members[1].type as CustomType
        somethingElseMember.className == "SomethingElse"
        somethingElseMember.members.size() == 2

        //CustomType 'SomethingElse' with fields:
        // [TypeMember(name=a, type=NullableIntType),
        // TypeMember(name=b, type=ListType)]
        CustomType somethingElse = datatypes[1] as CustomType
        somethingElse.className == "SomethingElse"
        somethingElse.members.size() == 2
        somethingElse.members[0].type instanceof NullableIntType
        somethingElse.members[1].type instanceof ListType
        ListType listType = somethingElse.members[1].type as ListType
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
        def result = ValidatorKt.findKlutterResponses([file])

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

    def "If a KlutterResponse has no fields then Either.nok is returned."() {
        given:
        def file = Files.createTempFile("SomeFile", ".kt").toFile()

        and:
        file.write(classBody)

        when:
        def result = ValidatorKt.findKlutterResponses([file])

        then:
        result instanceof EitherNok<List<String>, List<AbstractType>>

        and:
        result.data.size() == 1
        result.data[0] == "KlutterResponse contract violation! Some classes have no fields: [Something]"

        where:
        classBody = """
            @Serializable
            @KlutterResponse
            open class Something(): KlutterJSON<Something>() {
        
                override fun data() = this
        
                override fun strategy() = serializer()
        
            }
            
            """
    }

    def "If multiple KlutterResponse are found with identical names then Either.nok is returned."() {
        given:
        def file = Files.createTempFile("SomeFile", ".kt").toFile()

        and:
        file.write(classBody)

        when:
        def result = ValidatorKt.findKlutterResponses([file])

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

    def "If required annotations are missing then Either.nok is returned."() {
        given:
        def file = Files.createTempFile("SomeFile", ".kt").toFile()

        and:
        file.write(classBody)

        when:
        def result = ValidatorKt.findKlutterResponses([file])

        then:
        result instanceof EitherNok<List<String>, List<AbstractType>>

        and:
        result.data.size() == 2
        result.data[0] == "Class is missing @KlutterResponse annotation: Something"
        result.data[1] == "Class is missing @Serializable annotation: SomethingElse"

        where: "invalid because SomethingElse is not found"
        classBody = """
            @Serializable
            open class Something(
                val x: String?,
                val y: Boolean,
            ): KlutterJSON<Something>() {
        
                override fun data() = this
        
                override fun strategy() = serializer()
        
            }
            
            @KlutterResponse
            open class SomethingElse(
                val x: String?,
                val y: Boolean,
            ): KlutterJSON<SomethingElse>() {
        
                override fun data() = this
        
                override fun strategy() = serializer()
        
            }
            """
    }

    def "If required annotations are missing then Either.nok is returned."() {
        given:
        def file = Files.createTempFile("SomeFile", ".kt").toFile()

        and:
        file.write(classBody)

        when:
        def result = ValidatorKt.findKlutterResponses([file])

        then:
        result instanceof EitherNok<List<String>, List<AbstractType>>

        and:
        result.data.size() == 1
        result.data[0] == "KlutterResponse class could not be processed: " +
                "KlutterJSON TypeParameter does not match class name: Something | SomethingX"

        where:
        classBody = """
            @Serializable
            @KlutterResponse
            open class Something(
                val x: String?,
                val y: Boolean,
            ): KlutterJSON<SomethingX>() {
        
                override fun data() = this
        
                override fun strategy() = serializer()
        
            } """
    }

    def "If there are invalid TypeMembers then Either.nok is returned."() {
        given:
        def file = Files.createTempFile("SomeFile", ".kt").toFile()

        and:
        file.write(classBody)

        when:
        def result = ValidatorKt.findKlutterResponses([file])

        then:
        result instanceof EitherNok<List<String>, List<AbstractType>>

        and:
        result.data.size() == 1
        result.data[0] == "KlutterResponse class could not be processed: Field member could not be processed: '>'"

        where:
        classBody = """
            @Serializable
            @KlutterResponse
            open class Something(
                val x: String?,
                val y: List<x,>,
            ): KlutterJSON<Something>() {
        
                override fun data() = this
        
                override fun strategy() = serializer()
        
            } """
    }
}
