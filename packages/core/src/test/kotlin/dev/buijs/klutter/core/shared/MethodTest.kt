package dev.buijs.klutter.core.shared

import dev.buijs.klutter.core.test.TestResource
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

import java.nio.file.Files

class MethodTest: WordSpec({

    "When creating a Project with a plugin name" should {

        "[toMethod] an empty list is returned when no methods are found" {
            //given:
            val file = Files.createTempFile("SomeClass", "kt").toFile()

            //expect:
            file.toMethods().isEmpty() shouldBe true
        }

        "[toMethod] a list of methods is returned" {
            //given:
            val file = Files.createTempFile("SomeClass", "kt").toFile()
            TestResource().copy("platform_source_code", file.absolutePath)

            //expect:
            file.toMethods(Language.DART).isNotEmpty() shouldBe true
        }

    }

})
