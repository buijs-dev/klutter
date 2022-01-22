pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri("https://repsy.io/mvn/buijs-dev/klutter")
        }
    }
}

include(":klutter")