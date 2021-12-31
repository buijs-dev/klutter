import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.github.ben-manes.versions")
    id("dev.buijs.klutter.gradle")
}

klutter {

    val root = rootProject.projectDir

    multiplatform {
       source = "$root/kmp/common/src/commonMain"
    }

    flutter = File("$root")
    podspec = File("$root/kmp/common/common.podspec")

    modules {
        module("klutter")
        module("kmp")
        module("kmp/common")
        module("android")
        module("android/app")
    }

    services {
        api("PlatformApi") {
            func("version"){
                gives { String() }
            }
        }
    }
}

dependencies {
    val junitVersion: String by project.extra
    implementation(kotlin("test-junit"))
    implementation("junit:junit:$junitVersion")
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    checkForGradleUpdate = true
    outputFormatter = "json"
    outputDir = ".klutter/dependencyUpdates"
    reportfileName = "report"
}