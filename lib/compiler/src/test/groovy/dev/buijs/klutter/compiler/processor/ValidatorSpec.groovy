package dev.buijs.klutter.compiler.processor

import dev.buijs.klutter.kore.ast.SingletonBroadcastController
import dev.buijs.klutter.kore.ast.SquintCustomType
import dev.buijs.klutter.kore.ast.SquintCustomTypeMember
import dev.buijs.klutter.kore.ast.SquintMessageSource
import dev.buijs.klutter.kore.ast.StringType
import spock.lang.Specification
import dev.buijs.klutter.kore.common.*

class ValidatorSpec extends Specification {

    def "When all Controllers are valid then they are returned as a Set"() {
        given:
        def broadcastController =
                Either.ok(new SingletonBroadcastController("foo.bar", "MyController", [], new StringType()))

        when:
        def result =
                ValidatorKt.validateControllers([broadcastController])

        then:
        result instanceof EitherOk
    }

    ///                                 Validate Response TestCases                                 \\\
    /// =========================================================================================== \\\

    def "A list of errors is returned if there are errors"() {
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
        def myCustomTypeName = "MyCustomType"
        def fooTypeMember = new SquintCustomTypeMember("foo", "String", false)
        def noSourceFile = new SquintMessageSource(
                new StringType(),
                new SquintCustomType(myCustomTypeName, [fooTypeMember]), null)
        def eitherOk = Either.ok(noSourceFile)

        when:
        def result = ValidatorKt.validateResponses([eitherOk])

        then:
        result instanceof EitherNok

        and:
        def errors = result.data as List<String>

        and:
        errors[0] == "Source File from which to generate dart code is missing: [MyCustomType]"
    }

}
