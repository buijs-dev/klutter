plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.6.10"
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
}

ksp {
    arg("klutterScanFolder", project.buildDir.absolutePath)
    arg("klutterOutputFolder", project.projectDir.parentFile.absolutePath)
    arg("klutterCopyAarFile", "true")
    arg("klutterCopyFramework", "true")
    arg("klutterGenerateAdapters", "true")
    arg("klutterInitialize", "false")
}

buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":lib:annotations"))
    implementation(project(":lib:kore"))
    implementation(project(":lib:kompose"))
    implementation(project(":lib:compiler"))
    ksp(project(":lib:compiler"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}