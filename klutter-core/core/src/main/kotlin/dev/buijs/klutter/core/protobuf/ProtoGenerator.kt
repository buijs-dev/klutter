package dev.buijs.klutter.core.protobuf

import dev.buijs.klutter.core.*

/**
 * @author Gillian Buijs
 */
class ProtoGenerator(
    private val objects: ProtoObjects,
    private val klutter: Klutter
    ): KlutterFileGenerator() {

    override fun printer() = ProtoPrinter(objects)

    override fun writer() = ProtoWriter(printer().print(), klutter)

}