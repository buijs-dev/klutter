package dev.buijs.klutter.gradle.dsl

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 *
 */
class KlutterServicesDSLTest: WordSpec({

    "Using KlutterServicesDSL" should {
        val sut = KlutterServicesDSL()

        "Save the constructed API data" {
            val metadata = sut.configure {
                api("FooApi") {
                    func("footsie"){
                        takes {
                            parameter { String("key") }
                            parameter { IntList("listOfInts") }
                            parameter { Defined("Book" named "book") }
                            parameter { DefinedList("Book" named "bookList") }
                            parameter { DefinedMap(("Int" to "Book") named "map") }
                            parameter { name = "key"; type = "String" }
                            parameter { name = "value"; type = "String" }
                        }
                        gives { nothing() }
                    }
                }

                api("BarApi") {
                    func("search", async = true){
                        takes { parameter { name = "keyword"; type = "String" } }
                        gives { DefinedList("Book") }
                    }
                }

                api("Burgerking") {
                    func("buy"){
                        takes { parameter { Double("cheeseburger") } }
                        gives { DefinedMap("Product" to "Price") }
                    }
                }

                dataclass("Book") {
                    field { String("title") }
                    field { name = "author"; type = "String" }
                }

                dataclass("Car") {
                    field { name = "make"; type = "String" }
                    field { name = "bhp"; type = "String" }
                }

                enumclass("State") {
                    values("pending", "success", "error")
                }

            }

            /**
             * Assert API's have been created correctly
             */
            val apis = metadata.apis
            apis.size shouldBe 3

            val fooApi = apis[0]
            fooApi.name shouldBe "FooApi"
            fooApi.functions!![0].name shouldBe "footsie"
            fooApi.functions!![0].parameters!![0].name shouldBe "key"
            fooApi.functions!![0].parameters!![0].type shouldBe "String"
            fooApi.functions!![0].parameters!![1].name shouldBe "listOfInts"
            fooApi.functions!![0].parameters!![1].type shouldBe "List<int>"
            fooApi.functions!![0].parameters!![2].name shouldBe "book"
            fooApi.functions!![0].parameters!![2].type shouldBe "Book"
            fooApi.functions!![0].parameters!![3].name shouldBe "bookList"
            fooApi.functions!![0].parameters!![3].type shouldBe "List<Book>"
            fooApi.functions!![0].parameters!![4].name shouldBe "map"
            fooApi.functions!![0].parameters!![4].type shouldBe "Map<Int,Book>"
            fooApi.functions!![0].parameters!![5].name shouldBe "key"
            fooApi.functions!![0].parameters!![5].type shouldBe "String"
            fooApi.functions!![0].parameters!![6].name shouldBe "value"
            fooApi.functions!![0].parameters!![6].type shouldBe "String"
            fooApi.functions!![0].returnValue shouldBe "void"

            val barApi = apis[1]
            barApi.name shouldBe "BarApi"
            barApi.functions!![0].name shouldBe "search"
            barApi.functions!![0].parameters!![0].name shouldBe "keyword"
            barApi.functions!![0].parameters!![0].type shouldBe "String"
            barApi.functions!![0].returnValue shouldBe "List<Book>"

            val burgerApi = apis[2]
            burgerApi.functions!![0].returnValue shouldBe "Map<Product,Price>"

            /**
             * Assert data classes have been created correctly
             */
            val dataclasses = metadata.dataClasses
            2 shouldBe dataclasses.size

            val bookDataClass = dataclasses[0]
            bookDataClass.name shouldBe "Book"
            bookDataClass.fields?.size shouldBe 2
            bookDataClass.fields!![0].name shouldBe "title"
            bookDataClass.fields!![0].type shouldBe "String"
            bookDataClass.fields!![1].name shouldBe "author"
            bookDataClass.fields!![1].type shouldBe "String"

            /**
             * Assert enum classes have been created correctly
             */
            val enumclasses = metadata.enumClasses
            enumclasses.size shouldBe 1

            val stateEnumClass = enumclasses[0]
            stateEnumClass.name shouldBe  "State"
            stateEnumClass.values?.containsAll(listOf("pending", "success", "error")) shouldBe true
        }

    }
})