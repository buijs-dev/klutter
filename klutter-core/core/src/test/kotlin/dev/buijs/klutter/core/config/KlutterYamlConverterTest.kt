package dev.buijs.klutter.core.config

import dev.buijs.klutter.core.config.yaml.YamlPropertyType
import dev.buijs.klutter.core.config.yaml.YamlReader
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import java.nio.file.Files

/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
class KlutterYamlConverterTest: WordSpec({

    "Using the KlutterYamlConverter" should {
        "Create read the klutter.yaml and create a List of KlutterYamlProperties" {

            val projectDir = Files.createTempDirectory("")
            val yaml = projectDir.resolve("klutter.yaml").toFile()

            yaml.createNewFile()
            yaml.writeText(
                """
                app:
                  - version:
                    - code: 2
                    - name: 1.0.1
                  - id: "dev.buijs.klutter.example"

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
                  - version: "7.0.2"

                junit:
                  - version: "4.3.12"

                okhttp:
                  - version: 4.10.0-RC1
                  
                klutter:
                  - gradle:
                    - plugin:
                      - version: "0.3.23-pre-alpha"
                  - annotations:
                    - kmp:
                      - version: "0.2.49"

                foo:
                  - bar:
                    - baz:
                      - alfa:
                        - beta:
                          - zeta:
                            - youdoneyetbruh: "yes"

            """.trimIndent()
            )

            /**
             * When producer does some actions
             */
            val sut = YamlReader()
            val actual = sut.read(yaml)

            /**
             * And the klutter.properties contains all the properties
             */
            val gradle = actual.find { it.key == "gradle.version" }
            gradle shouldNotBe null
            gradle?.value shouldBe "7.0.2"
            gradle?.type  shouldBe YamlPropertyType.String

            val androidSdkTarget = actual.find { it.key == "android.sdk.target" }
            androidSdkTarget shouldNotBe null
            androidSdkTarget?.value shouldBe "31"
            androidSdkTarget?.type  shouldBe YamlPropertyType.Int

            val klutterAnnotationsKmpVersion = actual.find { it.key == "klutter.annotations.kmp.version" }
            klutterAnnotationsKmpVersion shouldNotBe null
            klutterAnnotationsKmpVersion?.value shouldBe "0.2.49"
            klutterAnnotationsKmpVersion?.type  shouldBe YamlPropertyType.String

            val appId = actual.find { it.key == "app.id" }
            appId shouldNotBe null
            appId?.value shouldBe "dev.buijs.klutter.example"
            appId?.type  shouldBe YamlPropertyType.String

            actual.find { it.key == "foo.bar.baz.alfa.beta.zeta.youdoneyetbruh" } shouldNotBe null

        }
    }
})
