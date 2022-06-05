package dev.buijs.klutter.core.shared

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.nio.file.Files

/**
 * @author Gillian Buijs
 */
class PubspecVisitorTest: WordSpec({

    "Using the IosInfoPlistVisitor" should {

        val projectDir = Files.createTempDirectory("")
        val pubspec = projectDir.resolve("plugin_pubspec").toFile()

        pubspec.createNewFile()

        "Set app name and display name" {
            pubspec.writeText(
                """
                  name: my app
                  description: this is my app
                  version: 1.0.0+1
            """.trimIndent()
            )

            PubspecVisitor(pubspec).appName() shouldBe "my app"

        }

    }
})