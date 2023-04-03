package dev.buijs.klutter.jetbrains.shared

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

internal class KlutterMetadataTest: WordSpec({

    "Verify KlutterIcons" should {

        "Return correct logo icon" {
            KlutterIcons.logo16x16.iconHeight shouldBe 16
            KlutterIcons.logo16x16.iconWidth shouldBe 16
            KlutterIcons.logo20x20.iconHeight shouldBe 20
            KlutterIcons.logo20x20.iconWidth shouldBe 20
        }

    }

    "Verify KlutterBundle" should {

        "Return correct information" {
            KlutterBundle.moduleId shouldBe "KLUTTER_MODULE"
            KlutterBundle.bundleId shouldBe "buijs_software_klutter"
            KlutterBundle.presentableName shouldBe "Klutter"
            KlutterBundle.groupName shouldBe "Klutter Framework"
            KlutterBundle.descriptionShort shouldBe "Add support for the Klutter Framework"
            KlutterBundle.descriptionLong shouldBe "Klutter is a framework which interconnects Flutter and Kotlin Multiplatform. " +
                    "It can be used to create Flutter plugins or standalone apps."

        }

    }

})