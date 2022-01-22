buildscript {
    apply(from = ".klutter/klutter.gradle.kts").also {
        repositories {
            google()
            gradlePluginPortal()
            mavenCentral()
            maven {
                url = uri("https://repsy.io/mvn/buijs-dev/klutter")
            }
        }

        dependencies {
            classpath("com.github.ben-manes:gradle-versions-plugin:${project.extra["gradleDependencyUpdates"]}")
            classpath("dev.buijs.klutter.gradle:dev.buijs.klutter.gradle.gradle.plugin:${project.extra["klutterGradlePluginVersion"]}")
            classpath("org.gradle.kotlin:gradle-kotlin-dsl-plugins:${project.extra["gradleKotlinDslVersion"]}")
        }
    }
}

allprojects {
    apply(from = ".klutter/klutter.gradle.kts").also {
        repositories {
            google()
            gradlePluginPortal()
            mavenCentral()
            maven {
                url = uri("https://repsy.io/mvn/buijs-dev/klutter")
            }
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

