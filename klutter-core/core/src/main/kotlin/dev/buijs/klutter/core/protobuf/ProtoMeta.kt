package dev.buijs.klutter.core.protobuf

data class KlutterApi(
    var name: String? = null,
    var functions: List<KlutterApiFunction>? = null,
)

data class KlutterApiFunction(
    var async: Boolean = false,
    var name: String? = null,
    var returnValue: String? = null,
    var parameters: List<KlutterMetaNamedField>? = null
)

data class KlutterDataClass(
    var name: String? = null,
    var fields: List<KlutterMetaNamedField>? = null,
)

data class KlutterEnumClass(
    var name: String? = null,
    var values: List<String>? = null,
)

data class KlutterMetaNamedField(
    var name: String? = null,
    var type: String? = null,
    var required: Boolean = false
)

data class KlutterMetaField(
    var type: String? = null
)

data class ProtoObjects(
    val messages: List<ProtoMessage>,
    val enumerations: List<ProtoEnum>
)

data class ProtoMessage(
    val name: String,
    val fields: List<ProtoField>
)

data class ProtoEnum(
    val name: String,
    val values: List<String>
)

data class ProtoField(
    val dataType: ProtoDataType,
    val name: String,
    val optional: Boolean,
    val repeated: Boolean,
    var customDataType: String? = null,
)