package dev.buijs.klutter.core.config

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.nio.file.Files

/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
class KlutterYamlConverterTest: WordSpec({

    "Using the KlutterYamlConverter" should {
        "Create read the klutter.yaml and create a klutter.properties file" {

            val projectDir = Files.createTempDirectory("")
            val yaml = projectDir.resolve("klutter.yaml").toFile()

            yaml.createNewFile()
            yaml.writeText(
                """
                app:
                  - version:
                      - code: 2
                      - name: 1.0.1
                  - id: dev.buijs.klutter.example

                android:
                  - sdk:
                      - minimum: 21
                      - compile: 31
                      - target: 31

                ios:
                  - version: 13.0

                flutter:
                  - sdk:
                      - version: 2.5.3
                      
                appcompat:
                  - version: 1.0.2

                kotlin:
                  - version: 1.6.10

                gradle:
                  - version: 7.0.2

                junit:
                  - version: 4.3.12

                okhttp:
                  - version: 4.10.0-RC1

            """.trimIndent()
            )

            /**
             * When producer does some actions
             */
            val sut = KlutterYamlReader()
            val actual = sut.read(yaml)

            /**
             * And the klutter.properties contains all the properties
             */
            actual["gradle.version"] shouldBe "7.0.2"
            actual["app.version.name"] shouldBe "1.0.1"
            actual["flutter.sdk.version"] shouldBe "2.5.3"

        }
    }
})
