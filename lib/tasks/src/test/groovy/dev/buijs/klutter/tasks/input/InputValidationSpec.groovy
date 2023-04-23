package dev.buijs.klutter.tasks.input

// TODO
class InputValidationSpec {
    // "Verify validateAppName" should {
    //
    //        "Return true for valid app names" {
    //
    //            // given valid names
    //            val names = listOf(
    //                "plugin",
    //                "my_plugin",
    //                "my_awesome_plugin_2",
    //                "myawesome2plugins"
    //            )
    //
    //            // expect all to be valid
    //            for(name in names) {
    //                name.isValidAppName().also {
    //                    if(!it) println("expected app name to be valid => $name")
    //                    it shouldBe true
    //                }
    //            }
    //
    //        }
    //
    //        "Return false for invalid app names" {
    //
    //            // given valid names
    //            val names = listOf(
    //                "1plugin", // can not start with a number
    //                "_my_plugin", // can not start with an underscore
    //                "my_awesome_plugin!!!", // can not contain: !!
    //                "MYAWESOMEPLUGIN" // can not be uppercase
    //            )
    //
    //            // expect all to be invalid
    //            for(name in names) {
    //                name.isValidAppName().also {
    //                    if(it) println("expected app name to be invalid => $name")
    //                    it shouldBe false
    //                }
    //            }
    //
    //        }
    //    }
    //
    //    "Verify validateGroupName" should {
    //
    //        "Return true for valid group names" {
    //
    //            // given valid names
    //            val names = listOf(
    //                "com.example",
    //                "com.example.app",
    //                "awesome.group_1.name",
    //                "awesome.group1_1.name"
    //            )
    //
    //            // expect all to be valid
    //            for(name in names) {
    //                name.isValidGroupName().also {
    //                    if(!it) println("expected group name to be valid => $name")
    //                    it shouldBe true
    //                }
    //            }
    //
    //        }
    //
    //        "Return false for invalid group names" {
    //
    //            // given valid names
    //            val names = listOf(
    //                "1.dev", // can not start with a number
    //                "_.my.group", // can not start with an underscore
    //                "com_.group",
    //                "com._group",
    //                "groupie"
    //            )
    //
    //            // expect all to be invalid
    //            for(name in names) {
    //                name.isValidGroupName().also {
    //                    if(it) println("expected group to be invalid => $name")
    //                    it shouldBe false
    //                }
    //            }
    //
    //        }
    //    }
}
