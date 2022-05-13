plugins {
    id("groovy")
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

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.21")
    implementation("org.jetbrains.kotlin:kotlin-compiler:1.6.21")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.13.2")

    //JUnit
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.10")

    //Spock
    testImplementation("org.codehaus.groovy:groovy-all:3.0.9")
    testImplementation("org.spockframework:spock-core:2.2-M1-groovy-3.0")

    //Gradle
    implementation(gradleApi())
    testImplementation(gradleTestKit())

    //Mock
    testImplementation("org.mockito:mockito-core:4.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")

}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


publishing {


    val user = extra.has("repo.username").let {
        if (it) extra.get("repo.username") as String else {
            throw GradleException("missing repo.username in gradle.properties")
        }
    }

    val pass = extra.has("repo.password").let {
        if (it) extra.get("repo.password") as String else {
            throw GradleException("missing repo.password in gradle.properties")
        }
    }

    val endpoint = extra.has("repo.url").let {
        if (it) extra.get("repo.url") as String else {
            throw GradleException("missing repo.url in gradle.properties")
        }
    }

    val libversion = extra.has("core.version").let {
        if (it) extra.get("core.version") as String else {
            throw GradleException("missing core.version in gradle.properties")
        }
    }


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
            version = libversion
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