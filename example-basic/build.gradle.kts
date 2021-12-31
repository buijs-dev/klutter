buildscript {
    apply(from = ".klutter/klutter.gradle.kts").also {
        repositories {
            google()
            gradlePluginPortal()
            mavenCentral()
            maven {
                url = uri(project.extra["privateRepoUrl"] as String)
                credentials {
                    username = project.extra["privateRepoUsername"] as String
                    password = project.extra["privateRepoPassword"] as String
                }
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
                url = uri(project.extra["privateRepoUrl"] as String)
                credentials {
                    username = project.extra["privateRepoUsername"] as String
                    password = project.extra["privateRepoPassword"] as String
                }
            }
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

