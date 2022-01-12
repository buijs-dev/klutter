package dev.buijs.klutter.annotations.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.google.devtools.ksp.visitor.KSEmptyVisitor

/**
 * @author Gillian Buijs
 */
class KlutterResponseProcessor(
    private val logger: KSPLogger
): SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver
            .getSymbolsWithAnnotation("dev.buijs.klutter.annotations.kmp.KlutterResponse")
            .filterIsInstance<KSClassDeclaration>()

        if (!symbols.iterator().hasNext()) return emptyList()

        symbols.forEach { it.accept(Visitor(), it) }

        return symbols.filterNot { it.validate() }.toList()

    }

    inner class Visitor : KSEmptyVisitor<KSClassDeclaration, DartClass?>() {

        override fun defaultHandler(node: KSNode, data: KSClassDeclaration): DartClass? {

            if(data.classKind != ClassKind.CLASS) {
                logger.error("KlutterResponse annotation can only be used on (data) classes.")
                return null
            }

            //todo
            return null

        }

    }

}


data class DartClass(
    val className: String,
)