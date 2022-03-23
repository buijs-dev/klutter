plugins {
    kotlin("jvm")
    id("dev.buijs.klutter.gradle")
}

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
    maven { url = uri("https://repsy.io/mvn/buijs-dev/klutter") }
}

allprojects {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://repsy.io/mvn/buijs-dev/klutter") }
    }
}

tasks.register("installPlatform") {

    //Install the platform module.
    //After the build is done, the created .aar files will be copied to the android folder/.klutter folder.
    exec {
        commandLine("bash", "./gradlew", "clean", "build", "-p", "platform")
    }

    //Run flutter pub get to make sure the iOS project is fully setup.
    exec {
        commandLine("bash", "flutter", "pub", "get")
    }

    //Run podupdate script which does a pod install/update to get the latest platform podspec.
    exec {
        commandLine("bash", "./gradlew", "podupdate", "-p", "ios")
    }

}