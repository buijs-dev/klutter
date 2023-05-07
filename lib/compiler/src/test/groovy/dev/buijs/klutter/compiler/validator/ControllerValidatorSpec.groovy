/* Copyright (c) 2021 - 2023 Buijs Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package dev.buijs.klutter.compiler.validator

import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.common.Either
import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

class ControllerValidatorSpec extends Specification {

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
                ControllerValidatorKt.validateControllers([broadcastController], [customType])

        then:
        result instanceof EitherOk
    }

    def "An error is returned if 1 or more Controllers have a return Type that is not present in the Response list"() {
        given:
        def controller = new RequestScopedSimpleController("", "", [methodReturningMyCustomType])
        def eitherOk = Either.ok(controller)

        when:
        def result = ControllerValidatorKt.validateControllers([eitherOk], [])

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
        def result = ControllerValidatorKt.validateControllers([invalidController], [])

        then:
        result instanceof EitherNok

        and:
        def errors = result.data as List<String>

        and:
        errors[0] == "This Controller is out of control! :-) bruh..."
    }

}
