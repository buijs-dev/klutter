package dev.buijs.klutter.core.tasks.project

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.nio.file.Files

/**
 * @author Gillian Buijs
 */
class CreateProjectTaskTest: WordSpec({

    "Using the Create command" should {

        "create a klutter project folder in the given folder" {

            //given
            val temp = Files.createTempDirectory("foo").toFile()

            //and
            val sut = CreateProjectTask(
                projectName = "example",
                projectLocation = temp.absolutePath,
                appId = "com.example.blabla"
            )

            //when
            sut.run()

            //then
            temp.resolve("example/android").exists() shouldBe true
            temp.resolve("example/buildSrc").exists() shouldBe true
            temp.resolve("example/gradle").exists() shouldBe true
            temp.resolve("example/ios").exists() shouldBe true
            temp.resolve("example/lib").exists() shouldBe true
            temp.resolve("example/platform").exists() shouldBe true

        }

    }

})
