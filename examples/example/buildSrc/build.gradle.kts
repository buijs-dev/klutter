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

    val properties = HashMap<String, String>()

    File("${project.projectDir}/../klutter.gradle").let { file ->
        if(file.exists()) {
            file.forEachLine { line ->
                line.filter {
                    !it.isWhitespace()
                }.split("=").let { pair ->
                    if(pair.size == 2) {
                        properties[pair[0]] = pair[1].filter { it != '"' }
                    }
                }
            }
        } else throw GradleException("missing file: $file")
    }

    val klutter = properties["klutterVersion"]
        ?: throw GradleException("missing klutterVersion in klutter.gradle")

    val kotlinDsl = properties["kotlinDslVersion"]
        ?: throw GradleException("missing kotlinDslVersion in klutter.gradle")

    val kotlin = properties["kotlinVersion"]
        ?: throw GradleException("missing kotlinVersion in klutter.gradle")

    val androidGradle = properties["androidGradleVersion"]
        ?: throw GradleException("missing androidGradleVersion in klutter.gradle")

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("dev.buijs.klutter:core:$klutter")
    implementation("dev.buijs.klutter:plugins:$klutter")
    implementation("org.gradle.kotlin:gradle-kotlin-dsl-plugins:$kotlinDsl")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin")
    implementation("com.android.tools.build:gradle:$androidGradle")

}