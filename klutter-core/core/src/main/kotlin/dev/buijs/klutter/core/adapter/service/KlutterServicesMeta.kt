package dev.buijs.klutter.core.adapter.service

/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 *
 * DTO for storing the data configured created by the KlutterServicesDSL.
 * The KlutterServicesDSL is used by the KlutterGeneratePigeonsTask.
 *
 */
data class KlutterServiceDTO(
    val apis: List<KlutterApi>,
    val dataClasses: List<KlutterDataClass>,
    val enumClasses: List<KlutterEnumClass>
)

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
    var type: String? = null
)

data class KlutterMetaField(
    var type: String? = null
)