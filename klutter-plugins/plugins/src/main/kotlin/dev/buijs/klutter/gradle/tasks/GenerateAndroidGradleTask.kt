package dev.buijs.klutter.gradle.tasks

import dev.buijs.klutter.core.flutter.AndroidBuildGradleGenerator
import org.gradle.internal.logging.text.StyledTextOutputFactory
import javax.inject.Inject

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
open class GenerateAndroidGradleTask
@Inject constructor(styledTextOutputFactory: StyledTextOutputFactory):
    KlutterGradleTask(styledTextOutputFactory) {

    override fun describe() {
        val logging = AndroidBuildGradleGenerator(
            android = android(),
            root = flutter().toPath()
        ).generate()
        logger.messages().addAll(logging.messages())
    }

}