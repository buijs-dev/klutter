plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

val file = File("${rootProject.projectDir.absolutePath}/dev.properties").normalize()

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

val appVersion = properties["app.version"]
    ?:throw GradleException("missing version in dev.properties")

version = appVersion

kotlin {
    android()

    sourceSets {
        val androidMain by getting
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
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