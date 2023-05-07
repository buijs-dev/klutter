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

import dev.buijs.klutter.kore.ast.CustomType
import dev.buijs.klutter.kore.ast.Method
import dev.buijs.klutter.kore.ast.SquintCustomType
import dev.buijs.klutter.kore.ast.SquintCustomTypeMember
import dev.buijs.klutter.kore.ast.SquintMessageSource
import dev.buijs.klutter.kore.ast.StringType
import dev.buijs.klutter.kore.ast.TypeMember
import spock.lang.Shared
import spock.lang.Specification
import dev.buijs.klutter.kore.common.*

import java.nio.file.Files

class ResponseValidatorSpec extends Specification {

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

    @Shared
    CustomType fooCustomTypeWithUnknownTypeMember

    @Shared
    CustomType dunnoCustomType

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
        fooCustomTypeWithUnknownTypeMember = new CustomType("FooType", "",
                [new TypeMember("dunno", new CustomType("DunnoType", "foo.com.ex", []))])
        dunnoCustomType = new CustomType("DunnoType", "foo.com.ex", [new TypeMember("doKnow", new StringType())])
        squintMessageWithoutTypeMember = new SquintMessageSource(
                new CustomType("FooType", "", []),
                new SquintCustomType("FooType", []),
                squintMessageSource)
    }

    def "A list of errors is returned if there are 1 or more errors"() {
        given:
        def invalidResponse = Either.nok("This Response is NOT Ok.")

        when:
        def result = ResponseValidatorKt.validateResponses([invalidResponse])

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
        def result = ResponseValidatorKt.validateResponses([eitherOk])

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
        def result = ResponseValidatorKt.validateResponses([eitherOk])

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
        def result = ResponseValidatorKt.validateResponses([eitherOk])

        then:
        result instanceof EitherNok

        and:
        def errors = result.data as List<String>

        and:
        errors[0] == "Response contract violation! Some classes have no fields: [FooType]"

    }

    def "An error is returned if there are 2 or more Response classes with the same name"() {

        when:
        def result = ResponseValidatorKt.validateResponses([
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
                ResponseValidatorKt.validateResponses([Either.ok(squintMessage)])

        then:
        result instanceof EitherOk

        and:
        def responses = result.data as List<SquintMessageSource>

        and:
        responses[0] == squintMessage

    }

    def "When a TypeMember's CustomType has no fields but no match is found for replacement, then nothing is replaced."() {
        expect:
        ResponseValidatorKt.copyOrReplace(fooCustomTypeWithUnknownTypeMember, []) == fooCustomTypeWithUnknownTypeMember
    }

    def "When a TypeMember's CustomType has no fields but a match is found for replacement, then the CustomType is replaced."() {
        expect:
        def replaced = ResponseValidatorKt.copyOrReplace(fooCustomTypeWithUnknownTypeMember, [dunnoCustomType])
        replaced != fooCustomTypeWithUnknownTypeMember
        replaced.members[0].type == dunnoCustomType
    }
}
