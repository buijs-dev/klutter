package dev.buijs.klutter.jetbrains.systemtest.pages

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.CommonContainerFixture
import com.intellij.remoterobot.fixtures.DefaultXpath
import com.intellij.remoterobot.fixtures.FixtureName
import com.intellij.remoterobot.search.locators.byXpath

@FixtureName("Welcome Frame")
@DefaultXpath("type", "//div[@class='FlatWelcomeFrame']")
class WelcomeFrame(
    remoteRobot: RemoteRobot,
    remoteComponent: RemoteComponent,
) : CommonContainerFixture(remoteRobot, remoteComponent) {
    val createNewKlutterProjectLink get() = actionLink(
        locator = byXpath(
            byDescription = "New Klutter Project",
            xpath = "//div[(@class='MainButton' and @text='New Klutter Project') " +
                    "or (@accessiblename='New Klutter Project' and @class='JButton')]"))

}

@FixtureName("New Project Frame")
@DefaultXpath("type", "//*[contains(@title.key, 'title.new.project')]")
class NewProjectFrame(
    remoteRobot: RemoteRobot,
    remoteComponent: RemoteComponent,
) : CommonContainerFixture(remoteRobot, remoteComponent) {

    val klutterTabButton get() = actionLink(
        locator = byXpath("Klutter", "//div[@class='JBList']"))

    val appNameTextField get() = actionLink(
        locator = byXpath("Name", "//div[@text='Name: ']"))

    val appNameTextFieldValue get() = actionLink(
        locator = byXpath("Name value", "//div[@visible_text='my_plugin']"))

    val groupNameTextField get() = actionLink(
        locator = byXpath("Group", "//div[@text='Group: ']"))

    val groupNameTextFieldValue get() = actionLink(
        locator = byXpath("Group value", "//div[@visible_text='com.example']"))

    val projectTypeDropdown get() = actionLink(
        locator = byXpath("Project", "//div[@text='Project:']"))

    val cancelButton get() = actionLink(
        locator = byXpath("Cancel button", "//div[@text.key='button.cancel']")
    )

    val nextButton get() = actionLink(
        locator = byXpath("Next button", "//div[@text.key='button.wizard.next']")
    )
}


@FixtureName("Invalid Input Popup")
@DefaultXpath("type", "//*[contains(@title.key, 'cannot.save.settings.default.dialog.title')]")
class InvalidUserInputFrame(
    remoteRobot: RemoteRobot,
    remoteComponent: RemoteComponent,
) : CommonContainerFixture(remoteRobot, remoteComponent) {

    val invalidAppNameMessage get() = actionLink(
        locator = byXpath("Invalid App Name", "//div[@visible_text='- Invalid app name']"))

    val okButton get() = actionLink(
        locator = byXpath("Ok button", "//div[@text.key='button.ok']"))

}