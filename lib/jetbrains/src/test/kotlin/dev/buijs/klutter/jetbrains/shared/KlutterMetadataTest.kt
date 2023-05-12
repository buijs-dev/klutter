package dev.buijs.klutter.jetbrains.shared

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

internal class KlutterMetadataTest: WordSpec({

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