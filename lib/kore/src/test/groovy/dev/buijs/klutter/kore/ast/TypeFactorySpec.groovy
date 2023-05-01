package dev.buijs.klutter.kore.ast

import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk
import spock.lang.Specification

class TypeFactorySpec extends Specification {

    def "If regex finds no match then result is EitherNok with error message"() {
        expect:
        with(TypeFactoryKt.toMapType([], false)) {
            it instanceof EitherNok<String, AbstractType>
            it.data == "MapType could not be processed: '[]'"
        }
    }

    def "If key is invalid then map processing returns EitherNok with error message"() {
        expect:
        with(TypeFactoryKt.toAbstractType(new TypeData("Map<!key,Boolean>"))) {
            it instanceof EitherNok<String, AbstractType>
            it.data == "KeyType of Map could not be processed: '!key'"
        }
    }

    def "If value is invalid then map processing returns EitherNok with error message"() {
        expect:
        with(TypeFactoryKt.toAbstractType(new TypeData("Map<String?,_!value!>"))) {
            it instanceof EitherNok<String, AbstractType>
            it.data == "ValueType of Map could not be processed: '_!value!'"
        }
    }

    def "If Map is valid then processing returns EitherOk with StandardType"() {
        expect:
        with(TypeFactoryKt.toAbstractType(new TypeData("Map<String?, Boolean>"))) {
            it instanceof EitherOk<String, AbstractType>
            with(it.data as MapType) { map ->
                map.key instanceof NullableStringType
                map.value instanceof BooleanType
            }
        }
    }

    def "If Nullabble Map is valid then processing returns EitherOk with StandardType"() {
        expect:
        with(TypeFactoryKt.toAbstractType(new TypeData("Map<String?, Boolean>?"))) {
            it instanceof EitherOk<String, AbstractType>
            with(it.data as NullableMapType) { map ->
                map.key instanceof NullableStringType
                map.value instanceof BooleanType
            }
        }
    }

    def "If Nullabble String is valid then processing returns EitherOk with StandardType"() {
        expect:
        with(TypeFactoryKt.toAbstractType(new TypeData("String?"))) {
            it instanceof EitherOk<String, AbstractType>
            it.data instanceof NullableStringType
        }
    }

    def "If regex finds no match then result is EitherNok with error message"() {
        expect:
        with(TypeFactoryKt.toListType([], false)) {
            it instanceof EitherNok<String, AbstractType>
            it.data == "ListType could not be processed: '[]'"
        }
    }

    def "If value is invalid then List processing returns EitherNok with error message"() {
        expect:
        with(TypeFactoryKt.toAbstractType(new TypeData("List<_!value!>"))) {
            it instanceof EitherNok<String, AbstractType>
            it.data == "ValueType of List could not be processed: '_!value!'"
        }
    }

    def "If List is valid then processing returns EitherOk with StandardType"() {
        expect:
        with(TypeFactoryKt.toAbstractType(new TypeData("List<Boolean?>"))) {
            it instanceof EitherOk<String, AbstractType>
            with(it.data as ListType) { list ->
                list.child instanceof NullableBooleanType
            }
        }
    }

    def "If Nullabble List is valid then processing returns EitherOk with StandardType"() {
        expect:
        with(TypeFactoryKt.toAbstractType(new TypeData("List<Double>?"))) {
            it instanceof EitherOk<String, AbstractType>
            with(it.data as NullableListType) { list ->
                list.child instanceof DoubleType
            }
        }
    }

    def "If CustomType is valid then processing returns EitherOk with CustomType"() {
        expect:
        with(TypeFactoryKt.toAbstractType(new TypeData("MyNullableType?"))) {
            it instanceof EitherOk<String, AbstractType>
            with(it.data as CustomType) { type ->
                type.className == "MyNullableType"
                type.members.isEmpty()
            }
        }
    }

    def "If List has no type parameter then processing returns EitherOk with StandardType List without parameters"() {
        expect:
        with(TypeFactoryKt.toAbstractType(new TypeData("List?"))) {
            it instanceof EitherOk<String, AbstractType>
            with(it.data as ListType) { list ->
                list.child == null
            }
        }
    }

    def "If Map has no type parameters then processing returns EitherOk with StandardType Map without parameters"() {
        expect:
        with(TypeFactoryKt.toAbstractType(new TypeData("Map"))) {
            it instanceof EitherOk<String, AbstractType>
            with(it.data as MapType) { map ->
                map.key == null
                map.value == null
            }
        }
    }

    def "Verify secondary TypeData constuctor"() {
        expect:
        with(TypeFactoryKt.toAbstractType(new TypeData("List", ["String"], true))) {
            it instanceof EitherOk<String, AbstractType>
            with(it.data as NullableListType) {
                it.child instanceof StringType
            }
        }
    }
}
