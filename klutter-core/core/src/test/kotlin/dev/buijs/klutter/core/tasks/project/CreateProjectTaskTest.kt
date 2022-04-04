package dev.buijs.klutter.core.tasks.project

import dev.buijs.klutter.core.KlutterTestProject
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
            KlutterTestProject(projectDir = temp.toPath())
            val dist = temp.resolve("cli-dist").also { it.mkdirs() }
            val lib = dist.resolve("lib").also { it.mkdirs() }
            lib.resolve("bla.jar").also { it.createNewFile() }

            //and
            val sut = CreateProjectTask(
                projectName = "example",
                projectLocation = temp.absolutePath,
                appId = "com.example.blabla",
                cliDistributionLocation = dist.absolutePath,
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
            temp.resolve("example/.tools/klutter-cli/lib/bla.jar").exists() shouldBe true

        }

    }

})
