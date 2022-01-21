package dev.buijs.klutter.plugins.gradle.dsl

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import java.io.File

/**
 * @author Gillian Buijs
 */
class KlutterModulesDSLTest: WordSpec({

    "Using KlutterModulesDSL" should {
        val currentPath = File("").toPath()
        val sut = KlutterModulesDSL(currentPath.resolve("foo").toFile())

        "Resolve to project root is relative" {
            val metadata = sut.configure {
                module("kmp")
                module("android/app")
                module("klutter", absolute = false)
            }

            /**
             * Assert paths have been resolved correctly
             */
            val expectedRoot = currentPath.toAbsolutePath().toString()
            val modules = metadata.modules
            modules.size shouldBe 3
            modules[0] shouldBe "$expectedRoot/foo/kmp"
            modules[1] shouldBe "$expectedRoot/foo/android/app"
            modules[2] shouldBe "$expectedRoot/foo/klutter"
        }


        "Save path as-is if path is absolute" {
            val metadata = sut.configure {
                module("bar/foo/super/mario", absolute = true)
            }

            /**
             * Assert paths have been resolved correctly
             */
            val modules = metadata.modules
            modules.size shouldBe 1
            modules[0] shouldBe "bar/foo/super/mario"
        }


        "Use custom root as absolute path if set" {
            val metadata = sut.configure {
                root("/foo/bar/baz/shazaam", absolute = true)
                module("kmp")
            }

            /**
             * Assert paths have been resolved correctly
             */
            val modules = metadata.modules
            modules.size shouldBe 1
            modules[0] shouldBe "/foo/bar/baz/shazaam/kmp"
        }


        "Resolve project root to custom root if set" {
            val metadata = sut.configure {
                root("/../../", absolute = false)
                module("kmp")
            }

            /**
             * Assert paths have been resolved correctly
             */
            val nonResolvedRoot = currentPath.toAbsolutePath().toString()
            val expectedRoot = currentPath.resolve("/../../").toAbsolutePath().toString()
            val modules = metadata.modules
            modules.size shouldBe 1
            modules[0] shouldNotBe "$nonResolvedRoot/kmp"
            modules[0] shouldBe "$expectedRoot/kmp"
        }

    }
})