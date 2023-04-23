plugins {
    kotlin("jvm")
    id("klutter")
    id("groovy")
    application
}

application {
    mainClass.set("dev.buijs.klutter.kommand.MainKt")
}

sourceSets {
    main {
        java {
            srcDirs("${projectDir.absolutePath}/src/main/kotlin")
        }
    }

    test {
        java {
            srcDirs(
                "${projectDir.absolutePath}/src/test/kotlin",
                "${projectDir.absolutePath}/src/test/groovy"
            )
        }

    }
}

repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // Klutter
    implementation(project(":lib:kore"))
    implementation(project(":lib:tasks"))

    // CLI
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    implementation("com.github.kotlin-inquirer:kotlin-inquirer:0.1.0")

    // Jackson for XML and YAML
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.14.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("io.github.microutils:kotlin-logging:3.0.5")

    // Test
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")

    // T-t-t-t-testing !
    testImplementation(project(":lib-test"))
    testImplementation("org.spockframework:spock-core")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<CreateStartScripts> {
    applicationName = "klutter"
}
