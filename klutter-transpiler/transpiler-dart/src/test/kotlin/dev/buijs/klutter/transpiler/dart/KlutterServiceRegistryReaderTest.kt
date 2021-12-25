package dev.buijs.klutter.transpiler.dart


class KlutterServiceRegistryReaderTest {

}

//package dev.buijs.klutter.services
//
//import dev.buijs.klutter.annotations.jvm.KlutterApi
//import dev.buijs.klutter.annotations.jvm.KlutterAsync
//import dev.buijs.klutter.annotations.jvm.KlutterEnum
//import dev.buijs.klutter.annotations.jvm.KlutterResponse
//
////@HostApi()
//////abstract class Api2Host {
//////    @async
//////    Value calculate(Value value);
//////}
//
//@KlutterResponse
//data class Value(val value: String)
//
//@KlutterApi
//abstract class Api2Host {
//    @KlutterAsync
//    abstract fun calculate(value: Value): Value
//}
//
//
////class Book {
////    String? title;
////    String? author;
////}
//
//data class Book(
//    val title: String?,
//    val author: String?
//)
//
////@HostApi()
////abstract class BookApi {
////    List<Book?> search(String keyword);
////}
//
//@KlutterApi
//abstract class BookApi {
//    abstract fun search(keyword: String): List<Book?>
//}
//
////enum State {
////    pending,
////    success,
////    error,
////}
//
//@KlutterEnum
//enum class State {
//    PENDING, SUCCESS, ERROR
//}
//
////class StateResult {
////    String? errorMessage;
////    State? state;
////}
//
//@KlutterResponse
//data class StateResult(
//    val errorMessage: String?,
//    val state: State?
//)
//
////@HostApi()
////abstract class Api {
////    StateResult queryState();
////}
//
//
//@KlutterApi
//abstract class Api {
//    abstract fun queryState(): StateResult
//}