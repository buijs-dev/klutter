package dev.buijs.klutter.core.tasks.shared

import dev.buijs.klutter.core.KlutterCodeGenerationException
import dev.buijs.klutter.core.KlutterWriter
import java.io.File

internal class DefaultWriter(
    private val path: File,
    private val content: String,
)
    : KlutterWriter
{

    override fun write() {

        if(path.exists()) path.delete()

        path.createNewFile().also { created ->
            if(!created) {
                throw KlutterCodeGenerationException(
                    "Unable to create folder in the given path $path."
                )
            }
        }

        path.writeText(content)

    }
}