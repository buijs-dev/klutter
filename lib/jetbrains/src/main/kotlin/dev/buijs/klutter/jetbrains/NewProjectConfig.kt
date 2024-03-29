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
package dev.buijs.klutter.jetbrains

import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.project.FlutterDistribution
import dev.buijs.klutter.kore.project.compatibleFlutterVersions
import dev.buijs.klutter.kore.tasks.project.toGroupName
import dev.buijs.klutter.kore.tasks.project.toPluginName

const val klutterPluginDefaultName = "my_plugin"
const val klutterPluginDefaultGroup = "com.example"

/**
 * Wrapper for data used to create a new project.
 */
class NewProjectConfig(
    /**
     * Name of the app (or plugin).
     *
     * Will be set in the flutter pubspec.yaml.
     */
    var appName: String? = null,

    /**
     * Name of the group/organisation/package.
     *
     * Used as package name in Android.
     */
    var groupName: String? = null,

    /**
     * Flutter SDK version.
     */
    var flutterDistribution: FlutterDistribution? = null,

    /**
     * Get pub dependencies from Git or Pub.
     */
    var useGitForPubDependencies: Boolean? = false,

    /**
     * Klutter Gradle version to use.
     */
    var bomVersion: String? = null,
)

internal const val INVALID_APP_NAME = "Invalid app name"
internal const val MISSING_APP_NAME = "Missing app name"
internal const val INVALID_GROUP_NAME = "Invalid group name"
internal const val MISSING_GROUP_NAME = "Missing group name"
internal const val MISSING_FLUTTERR_DIST = "Missing Flutter SDK distribution"
internal const val INVALID_FLUTTER_DIST = "Invalid Flutter SDK distribution"

/**
 * Validate a [NewProjectConfig] instance.
 */
fun NewProjectConfig.validate(): ValidationResult {
    val messages = mutableListOf<String>()

    if(appName == null)
        messages.add(MISSING_APP_NAME)

    else if(toPluginName(appName!!) is EitherNok)
        messages.add(INVALID_APP_NAME)

    if(groupName == null)
        messages.add(MISSING_GROUP_NAME)

    else if(toGroupName(groupName!!) is EitherNok)
        messages.add(INVALID_GROUP_NAME)

    if(flutterDistribution == null) {
        messages.add(MISSING_FLUTTERR_DIST)
    } else {
        if(compatibleFlutterVersions.keys.none { version -> version == flutterDistribution }) {
            messages.add(INVALID_FLUTTER_DIST)
        }
    }

    return ValidationResult(messages)
}

/**
 * Result returned after validating user input.
 */
data class ValidationResult(val messages: List<String>) {
    val isValid: Boolean = messages.isEmpty()
}