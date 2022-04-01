package dev.buijs.klutter.core.tasks.fastlane

import dev.buijs.klutter.core.Android
import dev.buijs.klutter.core.IOS
import dev.buijs.klutter.core.KlutterTestProject
import dev.buijs.klutter.core.Root
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

/**
 * @author Gillian Buijs
 */
class InitializeFastlaneTaskTest: WordSpec({

    "Using the InitFastlane command" should {

        "create a fastlane folder in the android folder" {

            //given
            val temp = KlutterTestProject()

            //Write content to manifest file
            temp.androidAppManifest.writeText("""
                <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                    package="com.example.app">
                   <application
                        android:label="app"
                        android:name="xD">
                        <activity>
                        </activity>
                        <meta-data
                            android:name="flutterEmbedding"
                            android:value="2" />
                    </application>
                </manifest>
            """.trimIndent())

            //and a task to test
            val sut = InitializeFastlaneTask(
                android = Android(temp.androidDir, Root(temp.flutterDir)),
                ios = IOS(temp.iosDir, Root(temp.flutterDir))
            )

            //when
            sut.run()

            //then
            temp.androidDir.resolve("fastlane").exists() shouldBe true
            temp.androidDir.resolve("google_keys.json").exists() shouldBe true
            temp.androidDir.resolve("Gemfile").exists() shouldBe true

        }

    }

})
