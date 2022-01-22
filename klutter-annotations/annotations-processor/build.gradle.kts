plugins {
    id("maven-publish")
    id("java-library")
    kotlin("jvm")
}

sourceSets {
    main {
        java {
            srcDirs("${projectDir.absolutePath}/src/main/kotlin")
        }
    }
}

dependencies {

    val file = File("${rootDir.absolutePath}/../publish/_publish.properties").normalize()

    if(!file.exists()) {
        throw GradleException("missing _publish.properties file in ${file.absolutePath}")
    }

    val properties = HashMap<String, String>()

    file.forEachLine {
        val pair = it.split("=")
        if(pair.size == 2){
            properties[pair[0]] = pair[1]
        }
    }

    val annotationsVersion = properties["annotations.version"]
        ?:throw GradleException("missing annotations.version")

    val coreVersion = properties["core.version"]
        ?:throw GradleException("missing core.version")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
    implementation("org.jetbrains.kotlin:kotlin-compiler:1.6.10")
    implementation("dev.buijs.klutter:annotations-jvm:$annotationsVersion")
    implementation("dev.buijs.klutter:core:$coreVersion")

    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.10")

}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    val file = File("${rootDir.absolutePath}/../publish/_publish.properties").normalize()

    if(!file.exists()) {
        throw GradleException("missing _publish.properties file in ${file.absolutePath}")
    }

    val properties = HashMap<String, String>()

    file.forEachLine {
        val pair = it.split("=")
        if(pair.size == 2){
            properties[pair[0]] = pair[1]
        }
    }

    val user = properties["repo.username"]
        ?:throw GradleException("missing repo.username in _publish.properties")

    val pass = properties["repo.password"]
        ?:throw GradleException("missing repo.password in _publish.properties")

    val endpoint = properties["repo.url"]
        ?:throw GradleException("missing repo.url in _publish.properties")

    repositories {
        maven {
            credentials {
                username = user
                password = pass
            }

            url = uri(endpoint)
        }
    }

    publications {
        create<MavenPublication>("maven") {

            groupId = "dev.buijs.klutter"
            artifactId = "annotations-processor"
            version = properties["annotations.version"]
                ?:throw GradleException("annotations.version in _publish.properties")

            artifact("$projectDir/build/libs/annotations-processor.jar")

        }
    }

}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}