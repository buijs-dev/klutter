buildscript {
    apply(from = ".klutter/klutter.gradle.kts").also {
        repositories {
            gradlePluginPortal()
            google()
            mavenCentral()
        }

        dependencies {
            classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${project.extra["kotlinVersion"]}")
            classpath("com.android.tools.build:gradle:${project.extra["gradleVersion"]}")
        }
    }
}

allprojects {
    repositories {
        maven {
            gradlePluginPortal()
            google()
            mavenCentral()
            url = uri("https://repsy.io/mvn/buijs-dev/klutter")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}