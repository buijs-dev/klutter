plugins {
    id("java-library")
    kotlin("jvm") version "1.6.0"
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
    }
}

sourceSets {
    main {
        java {
            srcDirs("${projectDir.absolutePath}/src/main/kotlin")
        }
    }
}

dependencies {
    implementation("dev.buijs.klutter.annotations:klutter-annotations:0.0.20")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
    implementation(kotlin("stdlib", "1.6.0"))
    implementation("org.jetbrains.kotlin:kotlin-compiler:1.6.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.10")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    val javadocJar by creating(Jar::class) {
        dependsOn.add(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc)
    }

    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
        archives(jar)
    }
}