package dev.buijs.klutter.jetbrains.shared

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.nio.file.Files
import kotlin.io.path.absolutePathString

internal class NewProjectConfigValidatorTest: WordSpec({

    "Verify validate" should {

        val flutterSDK = Files.createTempDirectory("flutterbin")

        "Return !isValid if group and name are not set" {
            NewProjectConfig().validate().isValid shouldBe  false
        }

        "Use app- and groupname from KlutterTaskConfig if not null" {
            NewProjectConfig(
                appName = "my_plugin_project",
                groupName = "com.example.my_plugin.project",
                flutterPath = flutterSDK.absolutePathString()
            ).validate().isValid shouldBe  true
        }

        "Return false if one or more validations fail" {

            // given an invalid app name and valid group name
            var config = NewProjectConfig(
                appName = "_invalid_project.name!!!",
                groupName = "com.example.my_plugin.project",
                flutterPath = flutterSDK.absolutePathString()
            )

            // expect validation to fail
            config.validate().isValid shouldBe  false

            // given a valid app name and invalid group name
            config = NewProjectConfig(
                appName = "my_plugin_project",
                groupName = "com_._!example.my_plugin.project",
                flutterPath = flutterSDK.absolutePathString()
            )

            // expect validation to fail
            config.validate().isValid shouldBe false

            // given an invalid app name and invalid group name
            config = NewProjectConfig(
                appName = "_invalid_project!!!",
                groupName = "com_._!example.my_plugin.project",
                flutterPath = flutterSDK.absolutePathString()
            )

            // expect validation to fail
            config.validate().isValid shouldBe false

        }

    }

})