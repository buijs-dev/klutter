plugins {
    id("maven-publish")
    id("java-library")
    kotlin("jvm")
}

java {
    withJavadocJar()
    withSourcesJar()
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
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.13.1")

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
            artifactId = "core"
            version = properties["core.version"]
                ?:throw GradleException("core.version in _publish.properties")

            artifact("$projectDir/build/libs/core.jar")

            pom {
                name.set("Klutter: Core")
                description.set("Klutter Framework core module")
                url.set("https://buijs.dev/klutter/")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/buijs-dev/klutter/blob/main/LICENSE")
                    }
                }

                developers {
                    developer {
                        id.set("buijs-dev")
                        name.set("Gillian Buijs")
                        email.set("info@buijs.dev")
                    }
                }

                scm {
                    connection.set("git@github.com:buijs-dev/klutter.git")
                    developerConnection.set("git@github.com:buijs-dev/klutter.git")
                    url.set("https://github.com/buijs-dev/klutter")
                }
            }
        }
    }
}




tasks.named<Test>("test") {
    useJUnitPlatform()
}