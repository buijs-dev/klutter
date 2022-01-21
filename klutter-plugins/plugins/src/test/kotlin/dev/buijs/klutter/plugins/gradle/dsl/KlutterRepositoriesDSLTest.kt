package dev.buijs.klutter.plugins.gradle.dsl

import dev.buijs.klutter.core.KlutterConfigException
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import java.io.File

/**
 * @author Gillian Buijs
 */
class KlutterRepositoriesDSLTest: WordSpec({

    "Using KlutterRepositoriesDSL" should {

        val sut = KlutterRepositoriesDSL()

        "Create valid repo objects" {
            val metadata = sut.configure {

               maven {
                   url = secret("foo")
                   username = secret("bar")
                   password = secret("1234")
               }

                maven {
                    url = shared("public.repo.key")
                }
            }

            metadata.repositories.size shouldBe 2

            val repo1 = metadata.repositories[0]
            repo1.username shouldBe "bar"
            repo1.password shouldBe "1234"
            repo1.url shouldBe "foo"

            val repo2 = metadata.repositories[1]
            repo2.username shouldBe null
            repo2.password shouldBe null
            repo2.url shouldBe "public.repo.key"

        }

        "Throw an exception for incorrect repo credentials" {

            try {
                sut.configure {

                    //username without password no-can-do
                    maven {
                        url = secret("foo")
                        username = secret("bar")
                    }
                }
            } catch(e: KlutterConfigException) {
                e.message shouldBe "Can not configure a maven repository with incomplete credentials. Supply both an username and password."
            }

        }

        "Throw an exception for missing url" {

            try {
                sut.configure {

                    //no url, don't know where to look
                    maven { }
                }
            } catch(e: KlutterConfigException) {
                e.message shouldBe "Can not configure a maven repository without URL"
            }
        }
    }
})