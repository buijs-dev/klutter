package dev.buijs.klutter.plugins.gradle.tasks.config

import dev.buijs.klutter.plugins.gradle.tasks.config.dedotkey
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

/**
 * @author Gillian Buijs
 */
class KlutterGradleProducerTest : WordSpec({

    "When dottie goes in camels" should { "come out" {
            val dottie = "foo.bar.baz.bazaar"
            val dedottie = dedotkey(dottie)
            dedottie shouldBe "fooBarBazBazaar"
    } }

})