buildscript {
    apply(from = ".klutter/klutter.gradle.kts").also {
        repositories {
            gradlePluginPortal()
            google()
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
            classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${project.extra["kotlinVersion"]}")
            classpath("com.android.tools.build:gradle:${project.extra["gradleVersion"]}")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}