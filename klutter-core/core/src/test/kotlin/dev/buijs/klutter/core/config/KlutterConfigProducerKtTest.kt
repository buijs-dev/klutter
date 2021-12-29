package dev.buijs.klutter.core.config

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
class KlutterConfigProducerKtTest : WordSpec({

    "When dottie goes in camels" should { "come out" {
            val dottie = "foo.bar.baz.bazaar"
            val dedottie = dedotkey(dottie)
            dedottie shouldBe "fooBarBazBazaar"
    } }

})