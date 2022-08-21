package dev.buijs.klutter.jetbrains

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

internal class KlutterIconsTest: WordSpec({

    "Verify KlutterIcons" should {

        "Returns correct logo icon" {
            KlutterIcons.logo16x16.iconHeight shouldBe 16
            KlutterIcons.logo16x16.iconWidth shouldBe 16
            KlutterIcons.logo20x20.iconHeight shouldBe 20
            KlutterIcons.logo20x20.iconWidth shouldBe 20
        }

    }

})