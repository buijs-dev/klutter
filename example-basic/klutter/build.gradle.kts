plugins {
    id("org.gradle.kotlin.kotlin-dsl") version "1.4.9"
    id("dev.buijs.klutter.gradle")
}

klutter {
    val root = rootProject.projectDir
    android = File("$root/android/app")
    sources = listOf(File("$root/kmp/common/src/commonMain"))
    flutter = File("$root")
    ios = File("$root/ios")
    podspec = File("$root/kmp/common/common.podspec")

    services {
        api("PlatformApi") {
            func("version"){
                gives { String() }
            }
        }
    }
}

dependencies {
    implementation(kotlin("test-junit"))
    implementation("junit:junit:4.13.2")
}