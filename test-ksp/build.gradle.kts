plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
}

ksp {
    arg("klutterScanFolder", project.buildDir.absolutePath)
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
    implementation(project(":lib:klutter-annotations"))
    implementation(project(":lib:klutter-kore"))
    implementation(project(":lib:klutter-kompose"))
    ksp(project(":lib:klutter-kore"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}