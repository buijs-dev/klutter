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
package dev.buijs.klutter.ui

/**
 */
data class KomposeApp(
    val home: KomposeBody? = null,
    val showPerformanceOverlay: Boolean? = null,
    val checkerboardRasterCacheImages: Boolean? = null,
    val checkerboardOffscreenLayers: Boolean? = null,
    val showSemanticsDebugger: Boolean? = null,
    val debugShowCheckedModeBanner: Boolean? = null,
    val initialRoute: String? = null,
    val color: String? = null,
    val locale: KomposeLocale? = null,
    val supportedLocales: List<KomposeLocale>? = null,

    //TODO automatically apply locales in generated code based on
    //configured files etc.
    //example of configuration:
    //localizationsDelegates: [
    //        AppLocalizations.delegate,
    //        GlobalMaterialLocalizations.delegate,
    //        GlobalWidgetsLocalizations.delegate,
    //        GlobalCupertinoLocalizations.delegate,
    //      ],
    // some info:
    // https://phrase.com/blog/posts/flutter-localization/
    //
    // these fields in PlatformApp:
    // // final Iterable<LocalizationsDelegate<dynamic>>? localizationsDelegates;
    //// final LocaleListResolutionCallback? localeListResolutionCallback;
    //// final LocaleResolutionCallback? localeResolutionCallback;

    //TODO how to create keys and share them between widgets?
    //Create wrappers for different kind of keys?
    //fields:
    //// final Key? widgetKey;
    //// final GlobalKey<NavigatorState>? navigatorKey;

    //TODO collect all screens and add them to the routes.

    //TODO missing attributes:
    // final GenerateAppTitle? onGenerateTitle;
    // final Map<LogicalKeySet, Intent>? shortcuts;
    // final Map<Type, Action<Intent>>? actions;

): Kompose {

    fun asCode(): String =
        """|PlatformApp(
           |    title: 'Flutter Demo',
           |    showPerformanceOverlay: ${showPerformanceOverlay.toBooleanOrNull()},
           |    checkerboardRasterCacheImages: ${checkerboardRasterCacheImages.toBooleanOrNull()},
           |    checkerboardOffscreenLayers: ${checkerboardOffscreenLayers.toBooleanOrNull()},
           |    showSemanticsDebugger: ${showSemanticsDebugger.toBooleanOrNull()},
           |    debugShowCheckedModeBanner: ${showPerformanceOverlay.toBooleanOrNull()},
           |    color: ${color.toColorOrNull()},
           |    onGenerateRoute: KomposeNavigator(context).onGenerateRoute,
           |    initialRoute: KomposeNavigator.homeRoute,
           |);
           |
        """.trimMargin()
    //           |    supportedLocales: ${supportedLocales?.toLocalesOrNull() ?: "null"}
//           |    locale: ${locale?.asCode() ?: "null"},


    private fun Boolean?.toBooleanOrNull(): String {
        return if(this == null) "null" else "$this"
    }

    private fun String?.toColorOrNull(): String {
        return if(this == null) "null" else "Color($this)"
    }

    override fun hasChild(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasChildren(): Boolean {
        TODO("Not yet implemented")
    }

    override fun child(): Kompose {
        TODO("Not yet implemented")
    }

    override fun children(): List<Kompose> {
        TODO("Not yet implemented")
    }

    override fun print(): String {
        TODO("Not yet implemented")
    }

//    private fun List<KomposeLocale>?.toLocalesOrNull(): String {
//        return if (this==null) "null" else """|
//            |const [
//            |${this.joinToString("\n") { it.asCode() } }
//            |]
//        """.trimMargin()
//    }

}