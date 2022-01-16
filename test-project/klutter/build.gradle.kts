import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.github.ben-manes.versions")
    id("dev.buijs.klutter.gradle")
}

klutter {
    modules {
        module("klutter")
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