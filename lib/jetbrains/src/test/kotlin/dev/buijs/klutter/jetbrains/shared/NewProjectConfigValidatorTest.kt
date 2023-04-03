package dev.buijs.klutter.jetbrains.shared

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

internal class NewProjectConfigValidatorTest: WordSpec({

    "Verify validate" should {

        "Return !isValid if group and name are not set" {
            NewProjectConfig().validate().isValid shouldBe  false
        }

        "Use app- and groupname from KlutterTaskConfig if not null" {
            NewProjectConfig(
                appName = "my_plugin_project",
                groupName = "com.example.my_plugin.project"
            ).validate().isValid shouldBe  true
        }

        "Return false if one or more validations fail" {

            // given an invalid app name and valid group name
            var config = NewProjectConfig(
                appName = "_invalid_project.name!!!",
                groupName = "com.example.my_plugin.project"
            )

            // expect validation to fail
            config.validate().isValid shouldBe  false

            // given a valid app name and invalid group name
            config = NewProjectConfig(
                appName = "my_plugin_project",
                groupName = "com_._!example.my_plugin.project"
            )

            // expect validation to fail
            config.validate().isValid shouldBe false

            // given an invalid app name and invalid group name
            config = NewProjectConfig(
                appName = "_invalid_project!!!",
                groupName = "com_._!example.my_plugin.project"
            )

            // expect validation to fail
            config.validate().isValid shouldBe false

        }

    }

    "Verify validateAppName" should {

        "Return true for valid app names" {

            // given valid names
            val names = listOf(
                "plugin",
                "my_plugin",
                "my_awesome_plugin_2",
                "myawesome2plugins"
            )

            // expect all to be valid
            for(name in names) {
                name.isValidAppName().also {
                    if(!it) println("expected app name to be valid => $name")
                    it shouldBe true
                }
            }

        }

        "Return false for invalid app names" {

            // given valid names
            val names = listOf(
                "1plugin", // can not start with a number
                "_my_plugin", // can not start with an underscore
                "my_awesome_plugin!!!", // can not contain: !!
                "MYAWESOMEPLUGIN" // can not be uppercase
            )

            // expect all to be invalid
            for(name in names) {
                name.isValidAppName().also {
                    if(it) println("expected app name to be invalid => $name")
                    it shouldBe false
                }
            }

        }
    }

    "Verify validateGroupName" should {

        "Return true for valid group names" {

            // given valid names
            val names = listOf(
                "com.example",
                "com.example.app",
                "awesome.group_1.name",
                "awesome.group1_1.name"
            )

            // expect all to be valid
            for(name in names) {
                name.isValidGroupName().also {
                    if(!it) println("expected group name to be valid => $name")
                    it shouldBe true
                }
            }

        }

        "Return false for invalid group names" {

            // given valid names
            val names = listOf(
                "1.dev", // can not start with a number
                "_.my.group", // can not start with an underscore
                "com_.group",
                "com._group",
                "groupie"
            )

            // expect all to be invalid
            for(name in names) {
                name.isValidGroupName().also {
                    if(it) println("expected group to be invalid => $name")
                    it shouldBe false
                }
            }

        }
    }

})