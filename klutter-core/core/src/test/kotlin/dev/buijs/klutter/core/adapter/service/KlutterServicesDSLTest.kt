package dev.buijs.klutter.core.adapter.service

import io.kotlintest.specs.WordSpec
import org.junit.Assert.*

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
            assertEquals("There should be 3 API's", 3, apis.size)

            val fooApi = apis[0]
            assertEquals("The first API should be named FooApi", "FooApi", fooApi.name)
            assertEquals("The function name is footsie", "footsie", fooApi.functions!![0].name)
            assertEquals("The function footsie's 1st argument name is key", "key", fooApi.functions!![0].parameters!![0].name)
            assertEquals("The function footsie's 1st argument type is String", "String", fooApi.functions!![0].parameters!![0].type)
            assertEquals("The function footsie's 3rd argument name is listOfInts", "listOfInts", fooApi.functions!![0].parameters!![1].name)
            assertEquals("The function footsie's 3rd argument type is List<int>", "List<int>", fooApi.functions!![0].parameters!![1].type)
            assertEquals("The function footsie's 2th argument name is book", "book", fooApi.functions!![0].parameters!![2].name)
            assertEquals("The function footsie's 2th argument type is Book", "Book", fooApi.functions!![0].parameters!![2].type)
            assertEquals("The function footsie's 4th argument name is bookList", "bookList", fooApi.functions!![0].parameters!![3].name)
            assertEquals("The function footsie's 4th argument type is List<Book>", "List<Book>", fooApi.functions!![0].parameters!![3].type)
            assertEquals("The function footsie's 5th argument name is map", "map", fooApi.functions!![0].parameters!![4].name)
            assertEquals("The function footsie's 5th argument type is Map<Int,Book>", "Map<Int,Book>", fooApi.functions!![0].parameters!![4].type)
            assertEquals("The function footsie's 6th argument name is key", "key", fooApi.functions!![0].parameters!![5].name)
            assertEquals("The function footsie's 6th argument type is String", "String", fooApi.functions!![0].parameters!![5].type)
            assertEquals("The function footsie's 7th argument name is value", "value", fooApi.functions!![0].parameters!![6].name)
            assertEquals("The function footsie's 7th argument type is String", "String", fooApi.functions!![0].parameters!![6].type)
            assertEquals("The function footsie's returnValue is void", "void", fooApi.functions!![0].returnValue)

            val barApi = apis[1]
            assertEquals("The second API should be named BarApi", "BarApi", barApi.name)
            assertEquals("The function name is search", "search", barApi.functions!![0].name)
            assertEquals("The function search's first argument name is keyword", "keyword", barApi.functions!![0].parameters!![0].name)
            assertEquals("The function search's first argument type is String", "String", barApi.functions!![0].parameters!![0].type)
            assertEquals("The function search's returnValue is List<Book>", "List<Book>", barApi.functions!![0].returnValue)

            val burgerApi = apis[2]
            assertEquals("The function buy returnValue is Map<Product,Price>", "Map<Product,Price>", burgerApi.functions!![0].returnValue)

            /**
             * Assert data classes have been created correctly
             */
            val dataclasses = metadata.dataClasses
            assertEquals("There should be 2 dataclasses", 2, dataclasses.size)

            val bookDataClass = dataclasses[0]
            assertEquals("The first Data Class should be named Book", "Book", bookDataClass.name)
            assertEquals("The first Data Class has 2 fields", 2, bookDataClass.fields?.size)
            assertEquals("The first Data Class field has name title", "title", bookDataClass.fields!![0].name)
            assertEquals("The first Data Class field has type String", "String", bookDataClass.fields!![0].type)
            assertEquals("The second Data Class field has name author", "author", bookDataClass.fields!![1].name)
            assertEquals("The second Data Class field has type String", "String", bookDataClass.fields!![1].type)

            /**
             * Assert enum classes have been created correctly
             */
            val enumclasses = metadata.enumClasses
            assertEquals("There should be 1 enum class", 1, enumclasses.size)

            val stateEnumClass = enumclasses[0]
            assertEquals("The Enum Class should be named State", "State", stateEnumClass.name)
            assertEquals("The Enum Class should contain 3 values: pending, success, error", true,
                stateEnumClass.values?.containsAll(listOf("pending", "success", "error")))
        }

    }
})