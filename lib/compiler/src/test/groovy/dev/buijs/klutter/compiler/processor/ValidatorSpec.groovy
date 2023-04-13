package dev.buijs.klutter.compiler.processor

import dev.buijs.klutter.kore.ast.SingletonBroadcastController
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

}
