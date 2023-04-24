plugins {
    kotlin("jvm") version "1.7.10"
    id("java-gradle-plugin")
}

gradlePlugin {
    plugins.register("klutter") {
        id = "klutter"
        implementationClass = "dev.buijs.klutter.KlutterInternalPlugin"
    }
}

buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        classpath("com.android.tools.build:gradle:7.2.2")
    }
}

allprojects {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    implementation("org.gradle.kotlin:gradle-kotlin-dsl-plugins:2.4.0")
    implementation(gradleApi())
}