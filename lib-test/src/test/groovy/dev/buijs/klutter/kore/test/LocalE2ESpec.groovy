package dev.buijs.klutter.kore.test

import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Timeout

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

/**
 * This E2E verifies the complete Klutter framework consisting
 * of this klutter project and the klutter-dart project by
 * publishing all modules to local maven, creating a local
 * copy of klutter-dart and executing all tasks through the CLI.
 *
 * Manual trigger only!
 *
 * Prerequisite: Publish modules to mavenLocal first.
 */
@Ignore // Not sure about this test
//@IgnoreIf({ !Files.exists(Path.of("rune2e.properties")) })
@Stepwise // Because it's one large test
@Timeout(1200) // 20 minutes because it takes a while to publish
class LocalE2ESpec extends Specification {

    @Shared
    def rootFolder = Files.createTempDirectory("klutter_e2e").toFile()

    @Shared
    def klutterFolder = new File("${rootFolder.absolutePath}/klutter")

    @Shared
    def klutterDartFolder = new File("${rootFolder.absolutePath}/klutter-dart")

    def "Setup: Create local copy of klutter and klutter-dart"() {
        given:
        def localKlutter = Path.of("", "..", "..", "klutter")
                .normalize()
                .toFile()
                .absolutePath

        def localKlutterDart = Path.of("", "..", "..", "klutter-dart")
                .normalize()
                .toFile()
                .absolutePath

        expect:
        copy(localKlutter, klutterFolder.absolutePath)
        copy(localKlutterDart, klutterDartFolder.absolutePath)
    }

    def "Setup: Replace pub references in klutter to klutter-dart with local path"() {
        when:
        def file = new File("${klutterFolder.absolutePath}/lib/klutter-tasks/src/main/kotlin/dev/buijs/klutter/tasks/GenerateKlutterPluginProjectTask.kt")

        then:
        file.exists()

        when:
        def currentLines = file.readLines() as List<String>
        def outputLines = new ArrayList<String>()
        for(String line in currentLines) {
            if(line.trim().startsWith("updated.add(\"  klutter: ^\$klutterPubVersion\")")) {
                outputLines.add("updated.add(\"  klutter: \n        path: \"${klutterDartFolder.absolutePath}\"\n")
            } else {
                outputLines.add(line)
                outputLines.add("\n")
            }
        }

        file.write("")
        outputLines.each { line -> file.append(line) }

        then:
        file.text.contains(klutterDartFolder.absolutePath)
        println(file.text)
    }

    def "Setup: Publish klutter Gradle modules to mavenLocal"() {
        expect:
        execute("""./gradlew clean build publishToMavenLocal""", klutterFolder)
    }

    def "Setup: Add mavenLocal() repository to all build gradle files in klutter-dart"() {
        given:
        def files = klutterDartFolder.listFiles()

        when:
        for (File file : files) {
            String s = file.text
            s = s.replace("maven { url = uri(\"https://repsy.io/mvn/buijs-dev/klutter\") }", "mavenLocal()")
            file.write(s)
        }

        then:
        files.every { file ->
            !file.text.contains("https://repsy.io/mvn/buijs-dev/klutter")
        }
    }

    def "Verify plugin: Create plugin project"() {

    }

    private static Boolean copy(String sourceDirectoryLocation, String destinationDirectoryLocation) {
        Files.walk(Paths.get(sourceDirectoryLocation)).forEach(source -> {
            Path destination = Paths.get(
                    destinationDirectoryLocation,
                    source.toString().substring(sourceDirectoryLocation.length())
            )

            try {
                Files.copy(source, destination)
            } catch (IOException e) {
                e.printStackTrace()
                return false
            }

        })

        return true
    }

    private static Boolean execute(String command, File folder) {
        def process = new ProcessBuilder()
        process.command(command.split(" "))
        process.directory(folder)
        def inProgress = process.start()
        inProgress.waitFor(15 * 60, TimeUnit.SECONDS)

        if(inProgress.exitValue() != 0) {
            println(inProgress.errorStream.text)
            return false
        }

        println(inProgress.inputStream.text)
        return true
    }
}
