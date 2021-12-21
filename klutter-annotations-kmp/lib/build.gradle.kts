plugins {
    kotlin("multiplatform")
//    kotlin("native.cocoapods")
    id("maven-publish")
    id("com.android.library")
}

group = "dev.buijs.klutter"
version = "0.2.12"

kotlin {

    jvm()
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

//    cocoapods {
//        summary = "Klutter module for annotations"
//        homepage = "https://buijs.dev"
//        ios.deploymentTarget = "13"
//        framework {
//            baseName = "Annotations"
//        }
//    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting
        val jvmMain by getting
        val androidMain by getting
//        val iosX64Main by getting
//        val iosArm64Main by getting
//        val iosSimulatorArm64Main by getting
//        val iosMain by creating {
//            dependsOn(commonMain)
//            iosX64Main.dependsOn(this)
//            iosArm64Main.dependsOn(this)
//            iosSimulatorArm64Main.dependsOn(this)
//        }
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
}

publishing {

    val file = File("${rootDir.absolutePath}/dev.properties").normalize()

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

    val user = properties["private.repo.username"]
        ?:throw GradleException("missing private.repo.username in dev.properties")

    val pass = properties["private.repo.password"]
        ?:throw GradleException("missing private.repo.password in dev.properties")

    val endpoint = properties["private.repo.url"]
        ?:throw GradleException("missing private.repo.url in dev.properties")

    repositories {
        maven {
            url = uri(endpoint)
            credentials {
                username = user
                password = pass
            }
        }
    }

    publishing {
        publications.withType<MavenPublication> {
            artifactId = "annotations-kmp"
        }
    }

}

tasks.register("buildAndPublish") {
    doLast {
        project.exec {
            commandLine("bash", "./publish.sh")
        }
    }
}