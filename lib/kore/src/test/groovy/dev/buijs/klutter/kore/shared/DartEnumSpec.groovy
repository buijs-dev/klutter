package dev.buijs.klutter.kore.shared

import dev.buijs.klutter.kore.KlutterException
import spock.lang.Specification

class DartEnumSpec extends Specification {

    def "Processing a blank String throws an exception"() {
        when:
        DartEnumKt.toDartEnum("", "EnumClassName")

        then:
        KlutterException e = thrown()
        e.message == "Unable to process enumeration: Enum has no values"
    }

    def "If there are no JSON values then values and valuesJSON are identical"() {
        when:
        def denum = DartEnumKt.toDartEnum(content, "Dog")

        then:
        !denum.values.isEmpty()
        !denum.valuesJSON.isEmpty()
        denum.values.eachWithIndex{ entry, i ->
            entry == denum.valuesJSON[i]
        }

        where:
        content = '''
            enum class Dog {
                SLEEPY_DOG, 
                ANNOYING_BARKER, 
                SMELLY_DOG, 
                BEST_FRIEND
            }
        '''
    }

    def "If there are JSON values then values and valuesJSON are NOT identical"() {
        given:
        def dog = ["Snorker", "SU", "TakeABath", "IWillCryWhenYOuDie"]
        def tag = ["SLEEPY_DOG", "ANNOYING_BARKER", "SMELLY_DOG", "BEST_FRIEND"]

        and: "enum class with sloppy formatting"
        def content = """
            enum class Dog {
                @Serializable("${dog[0]}")
                ${tag[0]}, 
                
                @Serializable(" ${dog[1]}")
                ${tag[1]}  , 
                
                @Serializable(" ${dog[2]} ") ${tag[2]}   , 
                
                @Serializable( "  ${dog[3]} "  ) ${tag[3]}  , 
            }
        """

        when:
        def denum = DartEnumKt.toDartEnum(content, "Dog")

        then:
        denum.values.eachWithIndex{ entry, i ->
            entry == tag[i]
        }

        and:
        denum.valuesJSON.eachWithIndex{ entry, i ->
            entry == dog[i]
        }

    }

}