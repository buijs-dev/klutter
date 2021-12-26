plugins {
    id("maven-publish")
    id("java-library")
    kotlin("jvm")
}

buildscript {
//    apply(".klutter/klutter.gradle.kts")

//    val kotlinVersion: String by project.extra
}

sourceSets {
    main {
        java {
            srcDirs("${projectDir.absolutePath}/src/main/kotlin")
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
    implementation("org.jetbrains.kotlin:kotlin-compiler:1.6.10")

    testImplementation("junit:junit:4.13.2")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.10")
    testImplementation("org.mockito:mockito-core:4.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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

    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.buijs.klutter"
            artifactId = "core"
            version = "0.2.41"
            artifact("$projectDir/build/libs/core.jar")
        }
    }

}

tasks.named<Test>("test") {
    useJUnitPlatform()
}