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

package dev.buijs.klutter.core

import java.io.File

/**
 * A representation of the structure of a project made with the Klutter Framework.
 * Each property of this object represents a folder containing one or more folders
 * and/or files wich are in some way used or needed by Klutter.
 *
 * @property root is the top level of the project.
 * @property ios is the folder containing the iOS frontend code, basically the iOS folder from a standard Flutter project.
 * @property android is the folder containing the Android frontend code, basically the iOS folder from a standard Flutter project.
 * @property platform is the folder containing the native backend code, basically a Kotlin Multiplatform library module.
 * @property flutter is the lib folder containing the main.dart, the starting point of the Android/iOS application.
 * @property buildSrc is the folder containing all Klutter configuration and the configured Klutter Plugin.
 *
 * @author Gillian Buijs
 */
data class KlutterProject(
    val root: Root,
    val ios: IOS,
    val android: Android,
    val flutter: Flutter,
    val platform: Platform,
    val buildSrc: BuildSrc,
)

/**
 * Factory to create a KlutterProject.
 *
 * @author Gillian Buijs
 */
object KlutterProjectFactory {

    /**
     * @param validate check if all required folders are present and return null if not.
     * If validate is false then validation is skipped and a [KlutterConfigException] is thrown
     * when a folder that should be present is not.
     *
     * @return a KlutterProject basing all module paths from the given root.
     */
    fun create(root: Root, validate: Boolean = false): KlutterProject? {

        val project = KlutterProject(
            root = root,
            ios = IOS(root = root),
            platform = Platform(root = root),
            android = Android(root = root),
            flutter = Flutter(root = root),
            buildSrc = BuildSrc(root = root),
        )

        if(validate) {

            //If the root itself does not exist then there is no KlutterProject
            if(!root.folder.exists()) return null

            val folders = listOf(
                project.ios,
                project.platform,
                project.android,
                project.flutter,
                project.buildSrc,
            )

            //If any of the folders does not exist return null
            if(folders.any { !it.exists }) return null

        }

        return project

    }

    fun create(location: String, validate: Boolean = false) = create(Root(location), validate)

    fun create(location: File, validate: Boolean = false) = create(Root(location), validate)

}

/**
 * @property folder path to the top level of the project.
 *
 * @author Gillian Buijs
 */
class Root(file: File) {

    constructor(location: String) : this(File(location))

    @Suppress("private")
    val folder: File = if (file.exists()) {
        file.absoluteFile
    } else throw KlutterConfigException(
        """
      The root folder does not exist.
            
      If no location is provided, Klutter determines the root folder by getting the rootProject.projectDir
      from the build.gradle.kts where the Klutter Plugin is applied. For a standard Klutter project 
      this means the root folder has a settings.gradle.kts which includes the :klutter module. 
      The klutter module build.gradle.kts applies the Klutter Plugin which will return the correct root folder.
      
      This behaviour can be overwritten by configuring the root folder directly in the Klutter Plugin.
      If this is done, check the configuration in Klutter Plugin to see if the root folder is correct.
      
      IMPORTANT: Setting the root is done with a path relative to your current directory, e.g. relative to 
      where the build.gradle.kts containing the Klutter Plugin is placed.
      
      Example for setting the root manually: 
      
      klutter {
            root("/../../my-root")
      }
      
      If this looks like a bug please file an issue at: https://github.com/buijs-dev/klutter/issues
      """.trimIndent()
    )

    fun resolve(to: String): File = folder.resolve(to).normalize().absoluteFile
}


/**
 * Wrapper class with a file instance pointing to the buildSrc sub-module.
 * If no custom path is given, Klutter assumes the path to the buildSrc folder is [root]/buildSrc.
 *
 * @property file path to the buildSrc folder.
 *
 * @author Gillian Buijs
 */
class BuildSrc(file: File? = null, root: Root) : KlutterFolder(
    root, file, "BuildSrc directory", root.resolve("buildSrc")
)

/**
 * Wrapper class with a file instance pointing to the flutter/lib sub-module.
 * If no custom path is given, Klutter assumes the path to the flutter lib folder is [root]/lib.
 *
 * @property file path to the (flutter) lib folder.
 *
 * @author Gillian Buijs
 */
class Flutter(file: File? = null, root: Root) :
    KlutterFolder(root, file, "Flutter lib directory", root.resolve("lib")) {

    /**
     * @throws KlutterConfigException if file(s) do not exist.
     * @return the absolute path to the flutter main.dart file.
     */
    fun mainDartFile() = getFileSafely(
        file.resolve("main.dart"),
        file.absolutePath,
        "root-project/lib/main.dart")

}

/**
 * Wrapper class with a file instance pointing to the kmp sub-module.
 * If no custom path is given, Klutter assumes the path to the Platform module is [root]/platform.
 *
 * @property file path to the Platform folder.
 *
 * @author Gillian Buijs
 */
class Platform(
    root: Root,
    file: File? = null,
    private val podspecName: String = "platform.podspec",
    private val moduleName: String = "commonMain",
) : KlutterFolder(root, file, "Platform directory", root.resolve("platform")) {

    /**
     * Function to return the location of the src module containing the common/shared platform code.
     * If no custom path is given, Klutter assumes the path to the KMP sourcecode is root-project/kmp/common/src/commonMain.
     *
     * @throws KlutterConfigException if file(s) do not exist.
     * @return the absolute path to the common source code.
     */
    fun source() = getFileSafely(
        file.resolve("src/$moduleName"),
        file.absolutePath, "root-project/platform/src/$moduleName"
    )

    /**
     * Function to return the location of the podspec file in the kmp sub-module.
     * If no custom path is given, Klutter assumes the path to the Platform sourcecode is root-project/platform/platform.podspec.
     *
     * @throws KlutterConfigException if file(s) do not exist.
     * @return the absolute path to the podspec file.
     */
    fun podspec() = getFileSafely(
        file.resolve(if (podspecName.endsWith(".podspec")) podspecName else "$podspecName.podspec"),
        file.absolutePath, "root-project/platform/platform.podspec"
    )

    /**
     * Function to return the location of build folder in the Platform module.
     * If no custom path is given, Klutter assumes the path to the Platform build folder is root-project/platform/build.
     *
     * @throws KlutterConfigException if file(s) do not exist.
     * @return the absolute path to the common source code.
     */
    fun build() = getFileSafely(
        file.resolve("build"),
        file.absolutePath,
        "root-project/platform/build"
    )

    fun moduleName() = moduleName

}

/**
 * Wrapper class with a file instance pointing to the ios sub-module.
 * If no custom path is given, Klutter assumes the path to the ios module is [root]/ios.
 *
 * @property file path to the iOS folder.
 *
 * @author Gillian Buijs
 */
class IOS(file: File? = null, root: Root) :
    KlutterFolder(root, file, "IOS directory", root.resolve("ios")) {

    /**
     * Function to return the location of the PodFile in the ios sub-module.
     * If no custom path is given, Klutter assumes the path to the iOS Podfile is root-project/ios/PodFile.
     *
     * @throws KlutterConfigException if file(s) do not exist.
     * @return the absolute path to the ios Podfile.
     */
    fun podfile() = getFileSafely(
        file.resolve("Podfile"),
        file.absolutePath,
        "root-project/ios/Podfile")

    /**
     * Function to return the location of the (optional) fastlane folder in the ios sub-module.
     * If no custom path is given, Klutter assumes the path to the iOS folder is root-project/ios/fastlane.
     *
     * @return the absolute path to the ios fastlane folder.
     */
    fun fastlane() = file.resolve("fastlane")

    /**
     * Function to return the location of the AppDelegate.swift file in the ios folder.
     * If no custom path is given, Klutter assumes the path to the iOS AppDelegate.swift is root-project/ios/Runner/AppDelegate.swift.
     *
     * @throws KlutterConfigException if file(s) do not exist.
     * @return the absolute path to the ios AppDelegate.
     */
    fun appDelegate(): File {
        val runner = getFileSafely(
            file.resolve("Runner"),
            file.absolutePath,
            "root-project/ios/Runner")
        return getFileSafely(
            runner.resolve("AppDelegate.swift"),
            runner.absolutePath,
            "root-project/ios/Runner/AppDelegate.swift"
        )
    }
}

/**
 * Wrapper class with a file instance pointing to the android sub-module.
 * If no custom path is given, Klutter assumes the path to the Android module is [root]/android.
 *
 * @property file path to the Android folder.
 *
 * @author Gillian Buijs
 */
class Android(file: File? = null, root: Root) : KlutterFolder(root, file, "Android directory", root.resolve("android")) {

    /**
     * Function to return the location of the app sub-module in the android folder.
     * If no custom path is given, Klutter assumes the path to the android app folder is root-project/android/app.
     *
     * @throws KlutterConfigException if file(s) do not exist.
     * @return the absolute path to the ios Podfile.
     */
    @Suppress("private")
    fun app() = getFileSafely(file.resolve("app"), file.absolutePath, "root-project/android/app")

    /**
     * Function to return the location of the AndroidManifest.xml file in the android/app sub-module.
     * If no custom path is given, Klutter assumes the path to the android app manifest file is root-project/android/app/src/main/AndroidManifest.xml.
     *
     * @throws KlutterConfigException if file(s) do not exist.
     * @return the absolute path to the ios Podfile.
     */
    fun manifest(): File {
        val mainFolder = getFileSafely(
            app().resolve("src/main"),
            file.absolutePath,
            "root-project/android/app/src/main"
        )
        return getFileSafely(
            mainFolder.resolve("AndroidManifest.xml"),
            mainFolder.absolutePath,
            "root-project/android/app/src/main/AndroidManifest.xml"
        )
    }

    /**
     * Function to return the location of the (optional) fastlane folder in the android sub-module.
     * If no custom path is given, Klutter assumes the path to the fastlane folder is root-project/android/fastlane.
     *
     * @return the absolute path to the android fastlane folder.
     */
    fun fastlane() = file.resolve("fastlane")
}

/**
 * A wrapper class which holds a reference to the Klutter project root folder.
 * This class is used to safely get file instances by setting default locations for mandatory folders.
 * These defaults can be overwritten with custom values and when those do not exist the KlutterFolder
 * falls back to the default value.
 *
 * @throws KlutterConfigException if both given <b>maybeFile</b> and <b>defaultLocation</b> do not exist.
 *
 * @author Gillian Buijs
 */
abstract class KlutterFolder(
    val root: Root,
    maybeFile: File?,
    whichFolder: String,
    defaultLocation: File
) {

    val file: File =
        when {
            maybeFile?.absoluteFile?.exists() == true -> {
                maybeFile.absoluteFile
            }

            defaultLocation.absoluteFile.exists() -> {
                defaultLocation.absoluteFile
            }

            else -> throw KlutterConfigException(
                """
              A folder which should be present is not found.
              
              Check configuration in Klutter Plugin for: $whichFolder.
              
              If no location is provided, Klutter assumes the correct path is: $defaultLocation.
              
              If this looks like a bug please file an issue at: https://github.com/buijs-dev/klutter/issues
              """.trimIndent()
            )
        }

    val exists = maybeFile?.absoluteFile?.exists() ?: defaultLocation.absoluteFile.exists()

}

/**
 * Helper method to get a file safely.
 * @throws KlutterConfigException if [file] is null or does not exists.
 *
 * @author Gillian Buijs
 */
internal fun getFileSafely(file: File?, whichFolder: String, defaultLocation: String): File {
    if (file == null) {
        throw KlutterConfigException(
            """
            A file which should be present is null.
            
            Check configuration in Klutter Plugin for: $whichFolder.
            
            If no location is provided, Klutter assumes the correct path is: $defaultLocation.
            
            If this looks like a bug please file an issue at: https://github.com/buijs-dev/klutter/issues
            """.trimIndent()
        )
    }

    if (!file.exists()) {
        throw KlutterConfigException(
            """
            A file which should be present does not exist:
            
            ${file.absolutePath}
                        
            Try one of the following:
            Check configuration in Klutter Plugin for: $whichFolder.
            Use Klutter task [generate adapter] to create any missing boilerplate.
            
            If this looks like a bug please file an issue at: https://github.com/buijs-dev/klutter/issues
            """.trimIndent()
        )
    }

    return file.absoluteFile
}