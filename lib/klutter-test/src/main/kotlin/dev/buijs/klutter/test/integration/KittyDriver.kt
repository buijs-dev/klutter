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

import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.options.UiAutomator2Options
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.ios.options.XCUITestOptions
import mu.KotlinLogging
import org.openqa.selenium.WebElement
import java.io.File
import java.net.URL
import java.time.Duration

/**
 *
 */
class KittyDriver(
    internal var androidDriver: AndroidDriver? = null,
    internal var iosDriver: IOSDriver? = null,
    internal val pathToScreenshotFolder: File? = null,
) {

    private val log = KotlinLogging.logger { }

    internal var lastFoundIosWebElement: WebElement? = null
    internal var lastFoundAndroidWebElement: WebElement? = null

    fun click(locator: KittyLocator) {
        androidDriver = androidDriver?.execute { it.click(locator) }
        iosDriver = iosDriver?.execute { it.click(locator) }
    }

    fun find(locator: KittyLocator) {
        androidDriver = androidDriver?.execute { lastFoundAndroidWebElement = it.find(locator) }
        iosDriver = iosDriver?.execute { lastFoundIosWebElement = it.find(locator) }
    }

    private fun AndroidDriver.execute(
        action: (AndroidDriver) -> Unit,
    ): AndroidDriver = try {
        action.invoke(this)
        androidDriver!!
    } catch(e: Exception) {
        androidDriver?.quit()
        iosDriver?.quit()
        throw KittyException("Android Test Failure!", e)
    }

    private fun IOSDriver.execute(
        action: (IOSDriver) -> Unit,
    ): IOSDriver = try {
        action.invoke(this)
        iosDriver!!
    } catch(e: Exception) {
        androidDriver?.quit()
        iosDriver?.quit()
        throw KittyException("IOS Test Failure!", e)
    }

    fun close() {
        try {
            iosDriver?.close()
        } catch (e: Exception) {
            log.info("Failed to close IOS driver", e)
        }

        try {
            androidDriver?.close()
        } catch (e: Exception) {
            log.info("Failed to close Android driver", e)
        }
    }

    companion object {

        fun init(
            pathToScreenshotFolder: File? = null
        ): KittyDriver {
            val url = URL("http://localhost:4723/wd/hub")

            val apk: URL = Resources.stream("app.apk")
            val androidOptions: UiAutomator2Options = Capabilities.android(apk)
            val androidDriver: AndroidDriver = AndroidDriver(url, androidOptions).also {
                it.manage().timeouts().implicitlyWait(Duration.ofSeconds(30))
            }

            val app: URL = Resources.stream("Runner.zip")
            val iosOptions: XCUITestOptions = Capabilities.ios(app)
            val iosDriver: IOSDriver = IOSDriver(url, iosOptions).also {
                it.manage().timeouts().implicitlyWait(Duration.ofSeconds(30))
            }

            return KittyDriver(
                androidDriver = androidDriver,
                iosDriver = iosDriver,
                pathToScreenshotFolder = pathToScreenshotFolder,
            )
        }

    }
}

infix fun AppiumDriver.find(
    locator: KittyLocator,
): WebElement = if(this is AndroidDriver) {
    findElement(locator.element.byAndroid())
} else {
    findElement(locator.element.byIOS())
}

infix fun AppiumDriver.click(locator: KittyLocator) {
    find(locator).click()
}