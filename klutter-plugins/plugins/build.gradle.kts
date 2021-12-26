import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "dev.buijs.klutter"
version = "0.2.43-pre-alpha"

plugins {
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.16.0"
    kotlin("jvm")
}

pluginBundle {
    website = "https://buijs.dev/klutter/"
    vcsUrl = "https://github.com/buijs-dev/klutter"
    tags = listOf("flutter", "kotlin", "multiplatform", "klutter")
}

gradlePlugin {
    plugins {
        create("klutterPlugin") {
            id = "dev.buijs.klutter.gradle"
            displayName = "Klutter plugin to generate boilerplate for connecting Flutter and Kotlin Multiplatform"
            description = "The klutterPlugin generates the MethodChannel code used by the Flutter frontend to communicate with Kotlin Multiplatform backend."
            implementationClass = "dev.buijs.klutter.gradle.KlutterAdapterPlugin"
        }
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

}

dependencies {
    implementation("dev.buijs.klutter:annotations-jvm:0.2.32")
    implementation("dev.buijs.klutter:core:0.2.41")
    implementation(kotlin("stdlib", "1.6.10"))
    implementation("org.jetbrains.kotlin:kotlin-compiler:1.6.10")

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
