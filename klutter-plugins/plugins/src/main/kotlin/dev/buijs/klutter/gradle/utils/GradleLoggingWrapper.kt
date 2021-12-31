package dev.buijs.klutter.gradle.utils

import dev.buijs.klutter.core.log.KlutterLogLevel
import dev.buijs.klutter.core.log.KlutterLogger
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutput.Style

/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
class GradleLoggingWrapper(
    private val logging: KlutterLogger,
    private val ouput: StyledTextOutput)
{

    fun sout() {
        logging.messages().forEach {
            ouput.style(color(it.level)).println(it.message)
        }
    }

    private fun color(lvl: KlutterLogLevel): Style =
        when(lvl){
            KlutterLogLevel.INFORMATIVE -> Style.Normal
            KlutterLogLevel.NEEDS_ATTENTION -> Style.Info
            KlutterLogLevel.HAPPY_FEET -> Style.Success
            KlutterLogLevel.CALL_BATMAN -> Style.FailureHeader
        }

}