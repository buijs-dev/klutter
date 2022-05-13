package dev.buijs.klutter.core

import spock.lang.Specification

import java.lang.reflect.Field
import java.nio.file.Files

class SecretsSpec extends Specification {

    def "Verify creating an instance of Secrets by supplying a location"() {

        given:
        def temp = Files.createTempDirectory("buildsrc")
        def properties = temp.resolve("klutter.secrets").toFile()
        properties.createNewFile()
        properties.write(
                "store.file.uri=x\nstore.password=y\nkey.alias=me\nkey.password=pass\n"
        )

        when:
        def sut = Klutter.secrets(temp.toAbsolutePath().toFile().absolutePath)

        then:
        sut["store.file.uri"] == "x"
        sut["store.password"] == "y"
        sut["key.alias"] == "me"
        sut["key.password"] == "pass"

    }

    def "Verify creating an instance of Secrets without klutter.secrets being present"() {

        given:
        def temp = Files.createTempDirectory("buildsrc")

        and: "set env variable"
        setEnv("STORE_FILE_URI","x")

        when:
        def sut = Klutter.secrets(temp.toAbsolutePath().toFile().absolutePath)

        then:
        sut["store.file.uri"] == "x"

        cleanup:
        unsetEnv("STORE_FILE_URI")

    }

    def static setEnv(String key, String value) {
        try {
            Map<String, String> env = System.getenv()
            Class<?> cl = env.getClass()
            Field field = cl.getDeclaredField("m")
            field.setAccessible(true)
            Map<String, String> writableEnv = (Map<String, String>) field.get(env)
            writableEnv.put(key, value)
        } catch (Exception e) {
            throw new IllegalStateException("Failed to set environment variable", e)
        }
    }

    def static unsetEnv(String key) {
        try {
            Map<String, String> env = System.getenv()
            Class<?> cl = env.getClass()
            Field field = cl.getDeclaredField("m")
            field.setAccessible(true)
            Map<String, String> writableEnv = (Map<String, String>) field.get(env)
            writableEnv.remove(key)
        } catch (Exception e) {
            throw new IllegalStateException("Failed to set environment variable", e)
        }
    }

}