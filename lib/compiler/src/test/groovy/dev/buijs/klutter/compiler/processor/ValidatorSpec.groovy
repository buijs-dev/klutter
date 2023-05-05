package dev.buijs.klutter.compiler.processor

import dev.buijs.klutter.compiler.validator.ValidatorKt
import dev.buijs.klutter.kore.ast.CustomType
import dev.buijs.klutter.kore.ast.Method
import dev.buijs.klutter.kore.ast.RequestScopedSimpleController
import dev.buijs.klutter.kore.ast.SingletonBroadcastController
import dev.buijs.klutter.kore.ast.SquintCustomType
import dev.buijs.klutter.kore.ast.SquintCustomTypeMember
import dev.buijs.klutter.kore.ast.SquintMessageSource
import dev.buijs.klutter.kore.ast.StringType
import dev.buijs.klutter.kore.ast.TypeMember
import spock.lang.Shared
import spock.lang.Specification
import dev.buijs.klutter.kore.common.*

import java.nio.file.Files

class ValidatorSpec extends Specification {

    @Shared
    File rootFolder

    @Shared
    CustomType customType

    @Shared
    TypeMember fooTypeMember

    @Shared
    TypeMember customTypeMember

    @Shared
    SquintCustomType squintCustomType

    @Shared
    SquintCustomTypeMember squintTypeMember

    @Shared
    SquintMessageSource squintMessageWithoutSourceFile

    @Shared
    SquintMessageSource squintMessage

    @Shared
    SquintMessageSource squintMessageWithUnknownTypeMember

    @Shared
    File squintMessageSource

    @Shared
    String squintMessageContent

    @Shared
    SquintMessageSource squintMessageWithoutTypeMember

    @Shared
    Method methodReturningMyCustomType

    def setupSpec() {
        rootFolder = Files.createTempDirectory("").toFile()
        squintCustomType =  new SquintCustomType("MyCustomType", [squintTypeMember])
        squintTypeMember = new SquintCustomTypeMember("foo", "String", false)
        fooTypeMember = new TypeMember("foo", new StringType())
        customTypeMember = new TypeMember("foo", new CustomType("DunnoType", "dot.dot.dot", [fooTypeMember]))
        customType = new CustomType("MyCustomType", "foo.dot.com", [new TypeMember("foo", new StringType())])
        squintMessageWithoutSourceFile = new SquintMessageSource(new StringType(), squintCustomType, null)
        squintMessageSource = rootFolder.toPath().resolve("sqdb_my_custom_type.json").toFile()
        squintMessageSource.createNewFile()
        squintMessageContent = '{"className":"MyCustomType","members":[{"name":"foo","type":"String","nullable":false}]}'
        squintMessageSource.write(squintMessageContent)
        squintMessage = new SquintMessageSource(customType, squintCustomType, squintMessageSource)
        methodReturningMyCustomType = new Method("", "", "", false, customType, null, null)
        squintMessageWithUnknownTypeMember = new SquintMessageSource(
                new CustomType("FooType", "", [customTypeMember]),
                new SquintCustomType("FooType", [squintTypeMember]),
                squintMessageSource)
        squintMessageWithoutTypeMember = new SquintMessageSource(
                new CustomType("FooType", "", []),
                new SquintCustomType("FooType", []),
                squintMessageSource)
    }

    def "When all Controllers are valid then they are returned as a Set"() {
        given:
        def broadcastController =
                Either.ok(new SingletonBroadcastController("foo.bar", "MyController", [], new StringType()))

        when:
        def result =
                ValidatorKt.validateControllers([broadcastController], [customType])

        then:
        result instanceof EitherOk
    }

    ///                                 Validate Response TestCases                                 \\\
    /// =========================================================================================== \\\

    def "A list of errors is returned if there are 1 or more errors"() {
        given:
        def invalidResponse = Either.nok("This Response is NOT Ok.")

        when:
        def result = ValidatorKt.validateResponses([invalidResponse])

        then:
        result instanceof EitherNok

        and:
        def errors = result.data as List<String>

        and:
        errors[0] == "This Response is NOT Ok."
    }

    def "An error is returned if there are 1 or more Responses without a source File"() {

        given:
        def eitherOk = Either.ok(squintMessageWithoutSourceFile)

        when:
        def result = ValidatorKt.validateResponses([eitherOk])

        then:
        result instanceof EitherNok

        and:
        def errors = result.data as List<String>

        and:
        errors[0] == "Source File from which to generate dart code is missing: [MyCustomType]"
    }

    def "An error is returned if there are 1 or more Responses with unknown TypeMembers"() {

        given:
        def eitherOk = Either.ok(squintMessageWithUnknownTypeMember)

        when:
        def result = ValidatorKt.validateResponses([eitherOk])

        then:
        result instanceof EitherNok

        and:
        def errors = result.data as List<String>

        and:
        errors[0] == "Unknown Response TypeMember: [DunnoType]"
    }

    def "An error is returned if there are 1 or more Responses without members"() {

        given:
        def eitherOk = Either.ok(squintMessageWithoutTypeMember)

        when:
        def result = ValidatorKt.validateResponses([eitherOk])

        then:
        result instanceof EitherNok

        and:
        def errors = result.data as List<String>

        and:
        errors[0] == "Response contract violation! Some classes have no fields: [FooType]"

    }

    def "An error is returned if there are 2 or more Response classes with the same name"() {

        when:
        def result = ValidatorKt.validateResponses([
                Either.ok(squintMessage),
                Either.ok(squintMessage)
        ])

        then:
        result instanceof EitherNok

        and:
        def errors = result.data as List<String>

        and:
        errors[0] == "Response contract violation! Duplicate class names: [MyCustomType]"

    }

    def "A List of distict Responses is returned if everything is a-okay"() {

        when:
        def result =
                ValidatorKt.validateResponses([Either.ok(squintMessage)])

        then:
        result instanceof EitherOk

        and:
        def responses = result.data as List<SquintMessageSource>

        and:
        responses[0] == squintMessage

    }

    ///                                 Validate Controller TestCases                                 \\\
    /// =========================================================================================== \\\

    def "An error is returned if 1 or more Controllers have a return Type that is not present in the Response list"() {
        given:
        def controller = new RequestScopedSimpleController("", "", [methodReturningMyCustomType])
        def eitherOk = Either.ok(controller)

        when:
        def result = ValidatorKt.validateControllers([eitherOk], [])

        then:
        result instanceof EitherNok

        and:
        def errors = result.data as List<String>

        and:
        errors[0] == "Unknown Response and/or Request Type: [MyCustomType]"
    }

    def "A list of errors is returned if there are 1 or more errors"() {
        given:
        def invalidController = Either.nok("This Controller is out of control! :-) bruh...")

        when:
        def result = ValidatorKt.validateControllers([invalidController], [])

        then:
        result instanceof EitherNok

        and:
        def errors = result.data as List<String>

        and:
        errors[0] == "This Controller is out of control! :-) bruh..."
    }

}
