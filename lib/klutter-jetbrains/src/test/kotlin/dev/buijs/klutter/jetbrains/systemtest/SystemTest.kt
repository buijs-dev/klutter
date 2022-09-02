/* Copyright (c) 2021 - 2022 Buijs Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package dev.buijs.klutter.jetbrains.systemtest

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.utils.keyboard
import dev.buijs.klutter.jetbrains.systemtest.pages.InvalidUserInputFrame
import dev.buijs.klutter.jetbrains.systemtest.pages.NewProjectFrame
import dev.buijs.klutter.jetbrains.systemtest.pages.WelcomeFrame
import dev.buijs.klutter.jetbrains.systemtest.utils.RemoteRobotExtension
import dev.buijs.klutter.jetbrains.systemtest.utils.StepsLogger
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(RemoteRobotExtension::class)
class SystemTest {

    private val timeout = Duration.ofSeconds(10)

    /**
     * If the IDE is not running then do not run the test.
     */
    @BeforeAll
    fun beforeMethod() {
        val ideIsRunning = try {
            // Do some action and if an exception
            // occurs then IDE is not running.
            RemoteRobotExtension().remoteRobot
                .find(WelcomeFrame::class.java, timeout)

            true // Robot works so IDE is running!
        } catch (e: Exception) {

            // No Robot, no SystemTest... Abort!
            false
        }

        Assumptions.assumeTrue(ideIsRunning)
        StepsLogger.init()
    }

    @Test
    fun newPluginProjectTest(remoteRobot: RemoteRobot) {
        with(remoteRobot) {

            /**
             * From the Intellij Welcome Screen open the new project wizard.
             */
            find(WelcomeFrame::class.java, timeout).apply {
                createNewKlutterProjectLink.isShowing shouldBe  true
                createNewKlutterProjectLink.click()
            }

            /**
             * In the new project wizard:
             * - go to the klutter tab
             * - enter an invalid char in the app name field
             * - click on the next button
             */
            find(NewProjectFrame::class.java, timeout).apply {
                // Check Klutter Tab Button is visible
                true shouldBe klutterTabButton.isShowing

                // Check Klutter Tab is selected succesfully
                klutterTabButton.click().also {
                    true shouldBe appNameTextField.isShowing
                    true shouldBe appNameTextFieldValue.isShowing
                    true shouldBe groupNameTextField.isShowing
                    true shouldBe groupNameTextFieldValue.isShowing
                    true shouldBe projectTypeDropdown.isShowing
                }

                // Select field [Name: ]
                appNameTextFieldValue.click()
                appNameTextFieldValue.keyboard { enterText("!") }

                // Click on next should trigger a validation error
                nextButton.click()
            }

            /**
             * Verify an error message is displayed:
             * - Verify message contains: Invalid app name
             * - Click the OK button to close
             */
            find(InvalidUserInputFrame::class.java, timeout).apply {
                true shouldBe invalidAppNameMessage.isShowing
                true shouldBe okButton.isShowing
                okButton.click()
            }

            /**
             * Click the Cancel button to return to the Welcome screen.
             */
            find(NewProjectFrame::class.java, timeout).apply {
                true shouldBe cancelButton.isShowing
                cancelButton.click()
            }
        }
    }

}