plugins {
    id("org.gradle.kotlin.kotlin-dsl") version "2.1.7"
    id("dev.buijs.klutter.gradle")
}

klutter {
    val root = rootProject.projectDir
    sources = listOf(File("$root/kmp/common/src/commonMain"))
    flutter = File("$root")
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