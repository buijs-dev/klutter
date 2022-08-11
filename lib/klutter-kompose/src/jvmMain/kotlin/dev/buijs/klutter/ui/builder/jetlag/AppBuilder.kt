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
package dev.buijs.klutter.ui.builder.jetlag

import dev.buijs.klutter.kore.shared.KlutterDSLBuilder
import dev.buijs.klutter.kore.shared.KlutterDSL
import dev.buijs.klutter.ui.KomposeApp
import dev.buijs.klutter.ui.KomposeBody
import dev.buijs.klutter.ui.KomposeLocale

@DslMarker
internal annotation class KomposeAppDSLMarker

@KomposeAppDSLMarker
class KomposeAppDSL: KlutterDSL<KomposeAppBuilder> {
    override fun configure(lambda: KomposeAppBuilder.() -> Unit): KomposeApp {
        return KomposeAppBuilder().apply(lambda).build()
    }
}

@KomposeAppDSLMarker
class KomposeAppBuilder: KlutterDSLBuilder {

    /**
     * Used as initialRoute.
     */
    lateinit var home: KomposeBody

    var showPerformanceOverlay: Boolean? = null
    var checkerboardRasterCacheImages: Boolean? = null
    var checkerboardOffscreenLayers: Boolean? = null
    var showSemanticsDebugger: Boolean? = null
    var debugShowCheckedModeBanner: Boolean? = null

    var initialRoute: String? = null
    var color: String? = null
    var locale: KomposeLocale? = null
    var supportedLocales: List<KomposeLocale>? = null

    override fun build() = KomposeApp(
        home = home,
        showPerformanceOverlay = showPerformanceOverlay,
        checkerboardRasterCacheImages = checkerboardRasterCacheImages,
        checkerboardOffscreenLayers = checkerboardOffscreenLayers,
        showSemanticsDebugger = showSemanticsDebugger,
        debugShowCheckedModeBanner = debugShowCheckedModeBanner,
        initialRoute = initialRoute,
        color = color, //TODO verify validity of input
        locale = locale, //TODO verify validitiy of input
        supportedLocales = supportedLocales, //TODO verify validitiy of input
    )

}