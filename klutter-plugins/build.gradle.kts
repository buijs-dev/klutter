import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "dev.buijs.klutter.plugins"
version = "0.0.18"

plugins {
    `java-gradle-plugin`
    `maven-publish`
    idea
    kotlin("jvm") version "1.6.0"
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
        classpath("com.android.tools.build:gradle:7.0.4")
    }
}

publishing {
    val file = File("${projectDir.absolutePath}/dev.properties").normalize()

    if(!file.exists()) {
        throw GradleException("missing dev.properties file in ${file.absolutePath}")
    }

    val properties = HashMap<String, String>()

    file.forEachLine {
        val pair = it.split("=")
        if(pair.size == 2){
            properties[pair[0]] = pair[1]
        }
    }

//    val appGroupId = properties["app.group.id"]
//        ?:throw GradleException("missing groupId in dev.properties")
//
//    val appArtifactId = properties["app.artifact.id"]
//        ?:throw GradleException("missing artifactId in dev.properties")
//
//    val appVersion = properties["app.version"]
//        ?:throw GradleException("missing version in dev.properties")

//    publications {
//        create<MavenPublication>("maven") {
//
//            groupId = appGroupId
//            artifactId = appArtifactId
//            version = appVersion
//
//            artifact("$projectDir/build/libs/klutter-${appArtifactId}-$appVersion.jar")
//        }
//    }

    val repsyUsername = properties["repsy.username"]
        ?:throw GradleException("missing repsy.username in dev.properties")

    val repsyPassword = properties["repsy.password"]
        ?:throw GradleException("missing repsy.password in dev.properties")

    val repsyUrl = properties["repsy.url"]
        ?:throw GradleException("missing repsy.url in dev.properties")

    repositories {
        maven {
            name = "localPluginRepository"
            url = uri("../local-plugin-repository")
        }

        maven {
            url = uri(repsyUrl)
            credentials {
                username = repsyUsername
                password = repsyPassword
            }
        }
    }

}

gradlePlugin {
    plugins {
        create("klutterAdapterPlugin") {
            id = "dev.buijs.klutter.plugins"
            implementationClass = "dev.buijs.klutter.plugins.adapter.gradle.KlutterAdapterPlugin"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib", "1.6.0"))
    implementation("org.jetbrains.kotlin:kotlin-compiler:1.6.0")

    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(gradleTestKit())
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.10")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.register("buildAndPublish") {
    doLast {
        project.exec {
            commandLine("bash", "./publish.sh")
        }
    }
}
