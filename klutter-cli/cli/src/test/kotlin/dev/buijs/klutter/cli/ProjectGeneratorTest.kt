package dev.buijs.klutter.cli

import dev.buijs.klutter.cli.commands.CreateOptions
import dev.buijs.klutter.cli.commands.CreateOptionsDefaults
import dev.buijs.klutter.cli.commands.CreateResult
import dev.buijs.klutter.cli.commands.CreateTask
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.nio.file.Files

/**
 * @author Gillian Buijs
 */
class ProjectGeneratorTest: WordSpec({

    "Using the Create command" should {

        "create a project named example in the root folder" {

            //given
            val temp = Files.createTempDirectory("foo").toFile()

            //and
            val sut = CreateTask(
                options = CreateOptions(
                    projectName = CreateOptionsDefaults.projectName,
                    appId = CreateOptionsDefaults.appId,
                    location = temp.absolutePath,
                )
            )

            //when
            val result = sut.run()

            //then
            result shouldBe CreateResult.PROJECT_CREATED

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
