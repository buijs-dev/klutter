plugins {
    `kotlin-dsl`
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

        maven {
            url = uri("https://repsy.io/mvn/buijs-dev/klutter")
        }

    }

}

//Any dependency added here will be added to the classpath of this project.
dependencies {

    val properties = HashMap<String, String>().also { map ->
        File("${rootProject.rootDir}/buildsrc.properties").let { file ->
            if(file.exists()) {
                file.forEachLine {
                    it.split("=").let { pair ->
                        if(pair.size == 2) map[pair[0]] = pair[1]
                    }
                }
            } else throw GradleException("missing file: $file")
        }
    }

    val klutter = properties["klutter"]
        ?:throw GradleException("missing klutter in buildsrc.properties")

    val kotlinDsl = properties["kotlin.dsl"]
        ?:throw GradleException("missing kotlin.dsl in buildsrc.properties")

    val kotlin = properties["kotlin"]
        ?:throw GradleException("missing kotlin in buildsrc.properties")

    val androidGradle = properties["android.gradle"]
        ?:throw GradleException("missing android.gradle in buildsrc.properties")

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("dev.buijs.klutter:core:$klutter")
    implementation("dev.buijs.klutter:plugins:$klutter")
    implementation("org.gradle.kotlin:gradle-kotlin-dsl-plugins:$kotlinDsl")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin")
    implementation("com.android.tools.build:gradle:$androidGradle")

}

tasks.named("compileKotlin") {
    doFirst {
        copy {
            from(layout.projectDirectory.file("buildsrc.properties"))
            into(layout.projectDirectory.dir("src/main/resources"))
        }
    }
}