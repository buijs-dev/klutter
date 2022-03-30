package dev.buijs.klutter.core.tasks.adapter.flutter

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.nio.file.Files

/**
 * @author Gillian Buijs
 */
class IosAppFrameworkInfoPlistVisitorTest: WordSpec({

    "Using the AppFrameworkInfoPlistVisitor" should {

        val projectDir = Files.createTempDirectory("")
        val frameworkinfo = projectDir.resolve("AppFrameworkInfo.plist").toFile()

        frameworkinfo.createNewFile()

        "Set iOS version if needed" {
            frameworkinfo.writeText(
                """
              <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
                <plist version="1.0">
                <dict>
                  <key>CFBundleDevelopmentRegion</key>
                  <string>en</string>
                  <key>CFBundleExecutable</key>
                  <string>App</string>
                  <key>CFBundleIdentifier</key>
                  <string>io.flutter.flutter.app</string>
                  <key>CFBundleInfoDictionaryVersion</key>
                  <string>6.0</string>
                  <key>CFBundleName</key>
                  <string>App</string>
                  <key>CFBundlePackageType</key>
                  <string>FMWK</string>
                  <key>CFBundleShortVersionString</key>
                  <string>1.0</string>
                  <key>CFBundleSignature</key>
                  <string>????</string>
                  <key>CFBundleVersion</key>
                  <string>1.0</string>
                  <key>MinimumOSVersion</key>
                  <string>9.0</string>
                </dict>
                </plist>
            """.trimIndent()
            )

            /**
             * When visitor comes to visit
             */
            IosAppFrameworkInfoPlistVisitor(frameworkinfo, "13.0").visit()

            /**
             * The AndroidManifest XML has the android:exported="true" attribute
             */

            frameworkinfo.readText().filter { !it.isWhitespace() } shouldBe  """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
                <plist version="1.0">
                <dict>
                  <key>CFBundleDevelopmentRegion</key>
                  <string>en</string>
                  <key>CFBundleExecutable</key>
                  <string>App</string>
                  <key>CFBundleIdentifier</key>
                  <string>io.flutter.flutter.app</string>
                  <key>CFBundleInfoDictionaryVersion</key>
                  <string>6.0</string>
                  <key>CFBundleName</key>
                  <string>App</string>
                  <key>CFBundlePackageType</key>
                  <string>FMWK</string>
                  <key>CFBundleShortVersionString</key>
                  <string>1.0</string>
                  <key>CFBundleSignature</key>
                  <string>????</string>
                  <key>CFBundleVersion</key>
                  <string>1.0</string>
                  <key>MinimumOSVersion</key>
                  <string>13.0</string>
                </dict>
                </plist>
            """.trimIndent().filter { !it.isWhitespace() }

        }

        "Add nothing if iOS version is correct" {
            frameworkinfo.writeText(
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
                <plist version="1.0">
                <dict>
                  <key>CFBundleDevelopmentRegion</key>
                  <string>en</string>
                  <key>CFBundleExecutable</key>
                  <string>App</string>
                  <key>CFBundleIdentifier</key>
                  <string>io.flutter.flutter.app</string>
                  <key>CFBundleInfoDictionaryVersion</key>
                  <string>6.0</string>
                  <key>CFBundleName</key>
                  <string>App</string>
                  <key>CFBundlePackageType</key>
                  <string>FMWK</string>
                  <key>CFBundleShortVersionString</key>
                  <string>1.0</string>
                  <key>CFBundleSignature</key>
                  <string>????</string>
                  <key>CFBundleVersion</key>
                  <string>1.0</string>
                  <key>MinimumOSVersion</key>
                  <string>13.0</string>
                </dict>
                </plist>
            """.trimIndent()
            )

            frameworkinfo.readText().filter { !it.isWhitespace() } shouldBe  """
         <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
                <plist version="1.0">
                <dict>
                  <key>CFBundleDevelopmentRegion</key>
                  <string>en</string>
                  <key>CFBundleExecutable</key>
                  <string>App</string>
                  <key>CFBundleIdentifier</key>
                  <string>io.flutter.flutter.app</string>
                  <key>CFBundleInfoDictionaryVersion</key>
                  <string>6.0</string>
                  <key>CFBundleName</key>
                  <string>App</string>
                  <key>CFBundlePackageType</key>
                  <string>FMWK</string>
                  <key>CFBundleShortVersionString</key>
                  <string>1.0</string>
                  <key>CFBundleSignature</key>
                  <string>????</string>
                  <key>CFBundleVersion</key>
                  <string>1.0</string>
                  <key>MinimumOSVersion</key>
                  <string>13.0</string>
                </dict>
                </plist>
         
            """.trimIndent().filter { !it.isWhitespace() }

        }
    }
})