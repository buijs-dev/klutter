package dev.buijs.klutter.jetbrains.shared

import dev.buijs.klutter.jetbrains.NewProjectConfig
import dev.buijs.klutter.jetbrains.validate
import dev.buijs.klutter.kore.project.FlutterDistributionFolderName
import dev.buijs.klutter.kore.project.flutterDistribution
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

internal class NewProjectConfigValidatorTest: WordSpec({

    "Verify validate" should {

        val dist = FlutterDistributionFolderName("3.0.5.macos.arm64").flutterDistribution

        "Return !isValid if group and name are not set" {
            NewProjectConfig().validate().isValid shouldBe  false
        }

        "Use app- and groupname from KlutterTaskConfig if not null" {
            NewProjectConfig(
                appName = "my_plugin_project",
                groupName = "com.example.my_plugin.project",
                flutterDistribution = dist
            ).validate().isValid shouldBe  true
        }

        "Return false if one or more validations fail" {

            // given an invalid app name and valid group name
            var config = NewProjectConfig(
                appName = "_invalid_project.name!!!",
                groupName = "com.example.my_plugin.project",
                flutterDistribution = dist
            )

            // expect validation to fail
            config.validate().isValid shouldBe  false

            // given a valid app name and invalid group name
            config = NewProjectConfig(
                appName = "my_plugin_project",
                groupName = "com_._!example.my_plugin.project",
                flutterDistribution = dist
            )

            // expect validation to fail
            config.validate().isValid shouldBe false

            // given an invalid app name and invalid group name
            config = NewProjectConfig(
                appName = "_invalid_project!!!",
                groupName = "com_._!example.my_plugin.project",
                flutterDistribution = dist
            )

            // expect validation to fail
            config.validate().isValid shouldBe false

        }

    }

})