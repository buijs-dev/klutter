package dev.buijs.klutter.core

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

/**
 * @author Gillian Buijs
 */
class MethodTest: WordSpec({

    "Using the utility method" should {

        "return a valid Dart data type" {

            DartKotlinMap.toDartType("Double") shouldBe "double"
            DartKotlinMap.toDartType("Int") shouldBe "int"
            DartKotlinMap.toDartType("Boolean") shouldBe "bool"
            DartKotlinMap.toDartType("String") shouldBe "String"

        }

        "return a valid Kotlin data type" {

            DartKotlinMap.toKotlinType("double") shouldBe "Double"
            DartKotlinMap.toKotlinType("int") shouldBe "Int"
            DartKotlinMap.toKotlinType("bool") shouldBe "Boolean"
            DartKotlinMap.toKotlinType("String") shouldBe "String"

        }

    }

})
