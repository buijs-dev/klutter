plugins {
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.intellij") version "1.8.0"
    id("java")
    id("maven-publish")
    id("groovy")
    id("klutter")
}

group = "dev.buijs.klutter"
version = dev.buijs.klutter.ProjectVersions.jetbrains

repositories {
    mavenCentral()
}

intellij {
    version.set("2022.1.1")
    type.set("IC") // Intellij Community Edition
    plugins.set(listOf("com.intellij.gradle"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
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
}

dependencies {
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.0-alpha7")
    implementation("io.github.microutils:kotlin-logging:2.1.23")

    // Project
    implementation(project(":lib:klutter-kore"))
}