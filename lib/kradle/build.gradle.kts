@file:Suppress("VulnerableLibrariesLocal")
plugins {
    kotlin("jvm")
    id("klutter")
    id("groovy")
    id("org.jetbrains.kotlin.plugin.allopen") version "1.8.21"
}

group = "dev.buijs.klutter"

allOpen {
    annotation("dev.buijs.klutter.kradle.Open4Test")
}

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
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

dependencies {
    // Klutter
    implementation(project(":lib:kore"))

    // CLI
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    implementation("com.github.kotlin-inquirer:kotlin-inquirer:0.1.0")

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
    applicationName = "kradlew"
}

tasks.register("kradleWrapperJar", Jar::class) {
    archiveBaseName.set("${project.name}-wrapper")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "dev.buijs.klutter.kradle.MainKt"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

tasks.register("copyKradleWrapperJarToRoot", Copy::class) {
    from(project.projectDir.resolve("build/libs/kradle-wrapper.jar"))
    into(project.rootProject.rootDir.resolve("kradle/lib"))
}

tasks.register("copyKradleWrapperJarToResources", Copy::class) {
    from(project.projectDir.resolve("build/libs/kradle-wrapper.jar"))
    into(project.projectDir.resolve("src/main/resources"))
}

tasks.named("build") {
    setFinalizedBy(setOf("kradleWrapperJar"))
}

tasks.named("kradleWrapperJar") {
    setFinalizedBy(setOf("copyKradleWrapperJarToRoot", "copyKradleWrapperJarToResources"))
}