package dev.buijs.klutter.cli

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import java.nio.file.Files

/**
 * @author Gillian Buijs
 */
class ProjectGeneratorTest: WordSpec({

    "Using the ProjectGenerator" should {

        "generate a default project" {

            //given
            val temp = Files.createTempDirectory("foo").toFile()

            //when
            val resource = getResource()

            //then
            resource shouldNotBe null

            //when
            copy(resource!!, temp, "example") shouldBe true

            //then
//            temp.resolve("android").exists() shouldBe true
//            temp.resolve("buildSrc").exists() shouldBe true
//            temp.resolve("gradle").exists() shouldBe true
//            temp.resolve("ios").exists() shouldBe true
//            temp.resolve("lib").exists() shouldBe true
//            temp.resolve("platform").exists() shouldBe true
        }

    }

})
