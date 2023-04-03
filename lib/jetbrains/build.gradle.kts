plugins {
    id("org.jetbrains.intellij") version "1.9.0"
    id("java")
    id("maven-publish")
    id("klutter")
    kotlin("jvm")
}

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

// For UI tests
val robotVersion = "0.11.16"

group = "dev.buijs.klutter"
version = dev.buijs.klutter.ProjectVersions.jetbrains

intellij {
    version.set("2022.2.1")
    type.set("IC") // Intellij Community Edition
    plugins.set(listOf("java", "com.intellij.gradle","android"))
}

sonarqube {
    isSkipProject = true
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
        sinceBuild.set("213.*")
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

    downloadRobotServerPlugin {
        version.set(robotVersion)
    }

    runIdeForUiTests {
        //    In case your Idea is launched on remote machine you can enable public port and enable encryption of JS calls
        //    systemProperty("robot-server.host.public", "true")
        //    systemProperty("robot.encryption.enabled", "true")
        //    systemProperty("robot.encryption.password", "my super secret")
        systemProperty("robot-server.port", "8082")
        systemProperty("ide.mac.message.dialogs.as.sheets", "false")
        systemProperty("jb.privacy.policy.text", "<!--999.999-->")
        systemProperty("jb.consents.confirmation.enabled", "false")
        systemProperty("ide.mac.file.chooser.native", "false")
        systemProperty("jbScreenMenuBar.enabled", "false")
        systemProperty("apple.laf.useScreenMenuBar", "false")
        systemProperty("idea.trust.all.projects", "true")
        systemProperty("ide.show.tips.on.startup.default.value", "false")
    }
}

repositories {
    maven { url = uri("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies") }
    mavenCentral()
}

dependencies {
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.1")
    implementation("io.github.microutils:kotlin-logging:3.0.0")

    // Project
    implementation(project(":lib:tasks"))
    implementation(project(":lib:kore"))

    // Kotlin Test
    @Suppress("GradleDependency") // 30-07-2022 newest 3.4.2 throws exceptions
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

    // Plugin UI Test
    testImplementation("com.intellij.remoterobot:remote-robot:$robotVersion")
    testImplementation("com.intellij.remoterobot:remote-fixtures:$robotVersion")
    testImplementation("com.intellij.remoterobot:ide-launcher:$robotVersion")
    testImplementation("com.squareup.okhttp3:okhttp:4.10.0")

}
