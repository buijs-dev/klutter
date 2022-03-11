package dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.nio.file.Files

/**
 * @author Gillian Buijs
 */
class IosInfoPlistVisitorTest: WordSpec({

    "Using the IosInfoPlistVisitor" should {

        val projectDir = Files.createTempDirectory("")
        val frameworkinfo = projectDir.resolve("Info.plist").toFile()

        frameworkinfo.createNewFile()

        "Set app name and display name" {
            frameworkinfo.writeText(
                """
              <?xml version="1.0" encoding="UTF-8"?>
                    <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
                    <plist version="1.0">
                    <dict>
                        <key>CFBundleDevelopmentRegion</key>
                        <string>{'$'}(DEVELOPMENT_LANGUAGE)</string>
                        <key>CFBundleDisplayName</key>
                        <string>App Name</string>
                        <key>CFBundleExecutable</key>
                        <string>{'$'}(EXECUTABLE_NAME)</string>
                        <key>CFBundleIdentifier</key>
                        <string>{'$'}(PRODUCT_BUNDLE_IDENTIFIER)</string>
                        <key>CFBundleInfoDictionaryVersion</key>
                        <string>6.0</string>
                        <key>CFBundleName</key>
                        <string>app_name</string>
                        <key>CFBundlePackageType</key>
                        <string>APPL</string>
                        <key>CFBundleShortVersionString</key>
                        <string>{'$'}(FLUTTER_BUILD_NAME)</string>
                        <key>CFBundleSignature</key>
                        <string>????</string>
                        <key>CFBundleVersion</key>
                        <string>{'$'}(FLUTTER_BUILD_NUMBER)</string>
                        <key>LSRequiresIPhoneOS</key>
                        <true/>
                        <key>UILaunchStoryboardName</key>
                        <string>LaunchScreen</string>
                        <key>UIMainStoryboardFile</key>
                        <string>Main</string>
                        <key>UISupportedInterfaceOrientations</key>
                        <array>
                            <string>UIInterfaceOrientationPortrait</string>
                            <string>UIInterfaceOrientationLandscapeLeft</string>
                            <string>UIInterfaceOrientationLandscapeRight</string>
                        </array>
                        <key>UISupportedInterfaceOrientations~ipad</key>
                        <array>
                            <string>UIInterfaceOrientationPortrait</string>
                            <string>UIInterfaceOrientationPortraitUpsideDown</string>
                            <string>UIInterfaceOrientationLandscapeLeft</string>
                            <string>UIInterfaceOrientationLandscapeRight</string>
                        </array>
                        <key>UIViewControllerBasedStatusBarAppearance</key>
                        <false/>
                    </dict>
                    </plist>
            """.trimIndent()
            )

            /**
             * When visitor comes to visit
             */
            IosInfoPlistVisitor(frameworkinfo, "My awesome app").visit()

            frameworkinfo.readText().filter { !it.isWhitespace() } shouldBe  """
                <?xml version="1.0" encoding="UTF-8"?>
                    <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
                    <plist version="1.0">
                    <dict>
                        <key>CFBundleDevelopmentRegion</key>
                        <string>{'$'}(DEVELOPMENT_LANGUAGE)</string>
                        <key>CFBundleDisplayName</key>
                        <string>My Awesome App</string>
                        <key>CFBundleExecutable</key>
                        <string>{'$'}(EXECUTABLE_NAME)</string>
                        <key>CFBundleIdentifier</key>
                        <string>{'$'}(PRODUCT_BUNDLE_IDENTIFIER)</string>
                        <key>CFBundleInfoDictionaryVersion</key>
                        <string>6.0</string>
                        <key>CFBundleName</key>
                        <string>my_awesome_app</string>
                        <key>CFBundlePackageType</key>
                        <string>APPL</string>
                        <key>CFBundleShortVersionString</key>
                        <string>{'$'}(FLUTTER_BUILD_NAME)</string>
                        <key>CFBundleSignature</key>
                        <string>????</string>
                        <key>CFBundleVersion</key>
                        <string>{'$'}(FLUTTER_BUILD_NUMBER)</string>
                        <key>LSRequiresIPhoneOS</key>
                        <true/>
                        <key>UILaunchStoryboardName</key>
                        <string>LaunchScreen</string>
                        <key>UIMainStoryboardFile</key>
                        <string>Main</string>
                        <key>UISupportedInterfaceOrientations</key>
                        <array>
                            <string>UIInterfaceOrientationPortrait</string>
                            <string>UIInterfaceOrientationLandscapeLeft</string>
                            <string>UIInterfaceOrientationLandscapeRight</string>
                        </array>
                        <key>UISupportedInterfaceOrientations~ipad</key>
                        <array>
                            <string>UIInterfaceOrientationPortrait</string>
                            <string>UIInterfaceOrientationPortraitUpsideDown</string>
                            <string>UIInterfaceOrientationLandscapeLeft</string>
                            <string>UIInterfaceOrientationLandscapeRight</string>
                        </array>
                        <key>UIViewControllerBasedStatusBarAppearance</key>
                        <false/>
                    </dict>
                    </plist>
            """.trimIndent().filter { !it.isWhitespace() }

        }

    }
})