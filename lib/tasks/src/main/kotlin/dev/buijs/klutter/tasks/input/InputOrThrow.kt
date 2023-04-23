package dev.buijs.klutter.tasks.input

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.common.Either
import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk

fun <T,R> Either<T, R>.inputOrThrow(
    ok: (EitherOk<T,R>) -> R,
    nok: (EitherNok<T,R>) -> String
): R {
    return when(this) {
        is EitherOk ->
            ok.invoke(this)
        is EitherNok ->
            throw KlutterException(nok.invoke(this))
    }
}

fun RootFolder.validRootFolderOrThrow() = inputOrThrow(
    ok = {
        it.data
    },
    nok = {
        it.data
    }
)

fun PluginName.validPluginNameOrThrow() = inputOrThrow(
    ok = {
        it.data
    },
    nok = {
        it.data
    }
)

fun GroupName.validGroupNameOrThrow()= inputOrThrow(
    ok = {
        it.data
    },
    nok = {
        it.data
    }
)