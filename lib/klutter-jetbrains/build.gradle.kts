plugins {
    id("org.jetbrains.intellij") version "1.8.0"
    id("java")
    id("maven-publish")
    id("klutter")
    kotlin("jvm")
}

buildscript {
    repositories {
        gradlePluginPortal()
    }
}

group = "dev.buijs.klutter"
version = dev.buijs.klutter.ProjectVersions.jetbrains

intellij {
    version.set("2022.1.1")
    type.set("IC") // Intellij Community Edition
    plugins.set(listOf("com.intellij.gradle","android"))
}

tasks {

    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    withType<Test> {
        useJUnitPlatform()
    }

    patchPluginXml {
        sinceBuild.set("212")
        untilBuild.set("222.*")
    }

    signPlugin {
        certificateChain.set(dev.buijs.klutter.Signing.certificateChain)
        privateKey.set(dev.buijs.klutter.Signing.privateKey)
        password.set(dev.buijs.klutter.Signing.privateKeyPassword)
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    buildSearchableOptions {
        enabled = false
    }

    runIde {
        ideDir.set(file(
            "/Users/buijs/Library/Application Support" +
                    "/JetBrains/Toolbox/apps/AndroidStudio" +
                    "/ch-0/212.5712.43.2112.8815526/Android Studio.app/Contents"))
    }

}

repositories {
    mavenCentral()
}

dependencies {
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.0-alpha7")
    implementation("io.github.microutils:kotlin-logging:2.1.23")

    // Project
    implementation(project(":lib:klutter-tasks"))
    implementation(project(":lib:klutter-kore"))

    // Kotlin Test
    @Suppress("GradleDependency") // 30-07-2022 newest 3.4.2 throws exceptions
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.0")
}
