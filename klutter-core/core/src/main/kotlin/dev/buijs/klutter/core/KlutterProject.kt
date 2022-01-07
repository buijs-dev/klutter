package dev.buijs.klutter.core

import dev.buijs.klutter.core.Klutter.KlutterYaml.*
import java.io.File

/**
 * A representation of the structure of a project made with the Klutter Framework.
 * Each property of this object represents a folder containing one or more folders
 * and/or files wich are in some way used or needed by Klutter.
 *
 * @property root is the top level of the project.
 * @property ios is the folder containing the iOS frontend code, basically the iOS folder from a standard Flutter project.
 * @property android is the folder containing the Android frontend code, basically the iOS folder from a standard Flutter project.
 * @property kmp is the folder containing the native backend code, basically a Kotlin Multiplatform library module.
 * @property flutter is the lib folder containing the main.dart, the starting point of the Android/iOS application.
 * @property klutter is the folder containing all Klutter configuration and the configured Klutter Plugin.
 *
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
data class KlutterProject(
    val root: Root,
    val ios: IOS,
    val android: Android,
    val flutter: Flutter,
    val kmp: KMP,
    val klutter: Klutter
)

/**
 * Factory to create a KlutterProject.
 */
class KlutterProjectFactory() {

    /**
     * @return a KlutterProject basing all module paths from the given root.
     */
    fun fromRoot(root: Root) = KlutterProject(
        root = root,
        ios = IOS(root = root),
        kmp = KMP(root = root),
        android = Android(root = root),
        flutter = Flutter(root = root),
        klutter = Klutter(root = root),
    )

}

/**
 * @property folder path to the top level of the project.
 */
class Root(file: File) {

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
 * If no custom path is given, Klutter assumes the path to the klutter folder is [root]/klutter.
 *
 * @property file path to the klutter folder.
 */
class Klutter(file: File? = null, root: Root) : KlutterFolder(
    root, file, "Klutter directory", root.resolve("klutter")
) {

    enum class KlutterYaml { PUBLIC, LOCAL, SECRETS }

    /**
     * Assumes all yaml files for klutter configuration are stored in [root]/klutter.
     * @throws KlutterConfigException if file(s) do not exist.
     * @return absolute path to a requested yaml file.
     */
    fun yaml(which: KlutterYaml) = when (which) {
        LOCAL -> {
            getFileSafely(
                file.resolve("klutter-local.yaml"),
                file.absolutePath,
                "root-project/klutter/klutter-local.yaml"
            )
        }

        PUBLIC -> {
            getFileSafely(
                file.resolve("klutter.yaml"),
                file.absolutePath,
                "root-project/klutter/klutter-local.yaml"
            )
        }

        SECRETS -> {
            getFileSafely(
                file.resolve("klutter-secrets.yaml"),
                file.absolutePath,
                "root-project/klutter/klutter-local.yaml")
        }
    }
}

/**
 * If no custom path is given, Klutter assumes the path to the flutter lib folder is [root]/lib.
 *
 * @property file path to the (flutter) lib folder.
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
 * If no custom path is given, Klutter assumes the path to the KMP module is [root]/kmp.
 *
 * @property file path to the KMP folder.
 */
class KMP(
    root: Root,
    file: File? = null,
    private val podspecName: String = "common.podspec",
    private val moduleName: String = "common",
    private val moduleMainName: String = "commonMain",
) : KlutterFolder(root, file, "KMP directory", root.resolve("kmp")) {

    /**
     * If no custom path is given, Klutter assumes the path to the KMP sourcecode is root-project/kmp/common/src/commonMain.
     *
     * @throws KlutterConfigException if file(s) do not exist.
     * @return the absolute path to the common source code.
     */
    fun source(): File {
        val commonFolder = getFileSafely(
            file.resolve(moduleName),
            file.absolutePath, "root-project/kmp/common"
        )

        return getFileSafely(
            commonFolder.resolve("src/$moduleMainName"),
            commonFolder.absolutePath, "root-project/kmp/common/src/commonMain"
        )

    }

    /**
     * If no custom path is given, Klutter assumes the path to the KMP sourcecode is root-project/kmp/common/commmon.podspec.
     *
     * @throws KlutterConfigException if file(s) do not exist.
     * @return the absolute path to the podspec file.
     */
    fun podspec(): File {
        val commonFolder = module()

        return getFileSafely(
            commonFolder.resolve(if (podspecName.endsWith(".podspec")) podspecName else "$podspecName.podspec"),
            commonFolder.absolutePath, "root-project/kmp/common/common.podspec"
        )
    }

    /**
     * If no custom path is given, Klutter assumes the path to the KMP module is root-project/kmp/common.
     *
     * @throws KlutterConfigException if file(s) do not exist.
     * @return the absolute path to the podspec file.
     */
    fun module() = getFileSafely(file.resolve(moduleName), file.absolutePath, "root-project/kmp/common")

    /**
     * If no custom path is given, Klutter assumes the path to the KMP build folder is root-project/kmp/common/build.
     *
     * @throws KlutterConfigException if file(s) do not exist.
     * @return the absolute path to the common source code.
     */
    fun build() = getFileSafely(
        file.resolve("$moduleName/build"),
        file.absolutePath,
        "root-project/kmp/common/build"
    )

    fun moduleName() = moduleName

}

/**
 * If no custom path is given, Klutter assumes the path to the iOS module is [root]/ios.
 *
 * @property file path to the iOS folder.
 */
class IOS(file: File? = null, root: Root) :
    KlutterFolder(root, file, "IOS directory", root.resolve("ios")) {

    /**
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
     * If no custom path is given, Klutter assumes the path to the iOS AppDelegate.swfit is root-project/ios/Runner/AppDelegate.swift.
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
 * If no custom path is given, Klutter assumes the path to the Android module is [root]/android.
 *
 * @property file path to the Android folder.
 */
class Android(file: File? = null, root: Root) : KlutterFolder(root, file, "Android directory", root.resolve("android")) {

    /**
     * If no custom path is given, Klutter assumes the path to the android app folder is root-project/android/app.
     *
     * @throws KlutterConfigException if file(s) do not exist.
     * @return the absolute path to the ios Podfile.
     */
    fun app() = getFileSafely(file.resolve("app"), file.absolutePath, "root-project/android/app")

    /**
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
}

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
}

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