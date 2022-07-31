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
package dev.buijs.klutter.test.integration

import io.appium.java_client.android.options.UiAutomator2Options
import io.appium.java_client.ios.options.XCUITestOptions
import io.appium.java_client.remote.MobileCapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import java.io.File
import java.net.URL
import java.time.Duration

/**
 *
 */
internal class Capabilities {
    companion object {
        fun android(app: URL): UiAutomator2Options {
            val options = UiAutomator2Options()
            options.setApp(app.toURI().path)
            options.setNewCommandTimeout(Duration.ofMinutes(2L))
            return options
        }
        fun ios(app: URL): XCUITestOptions {
            val options = XCUITestOptions()
            options.setApp(app.toURI().path)
            options.setDeviceName("iPhone Simulator")
            options.setPlatformVersion("15.5")
            options.setNoReset(true)
            options.setShowIosLog(true)
            options.setCapability("settings[snapshotMaxDepth]", 100)
            return options
        }
    }
}