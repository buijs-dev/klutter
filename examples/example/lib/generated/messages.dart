import 'dart:convert';
/// Autogenerated by Klutter
/// Do net edit directly, 
/// but recommended to store in VCS.
class ExtensiveGreetingInfo {
  
  ExtensiveGreetingInfo({
    required this.info,
    required this.type,
  });
  
 factory ExtensiveGreetingInfo.fromJson(dynamic message) {
  final json = jsonDecode(message);
   return ExtensiveGreetingInfo (
     info: json['info'].toString(),
     type: _GreetingType.fromJson(json['type']),
   );
 }

 final String info;
 final GreetingType type;

 Map<String, dynamic> toJson() {
   return {
     'info': info,
     'type': type.toJson()
   };
 }  
}

enum GreetingType {
  long,
  veryLong,
  exhaustive,
  leaveMeAlone,
  none
}


extension _GreetingType on GreetingType {

  static GreetingType fromJson(String value) {
    switch(value) {
      case "LONG": return GreetingType.long;
      case "VERY_LONG": return GreetingType.veryLong;
      case "EXHAUSTIVE": return GreetingType.exhaustive;
      case "LEAVE_ME_ALONE": return GreetingType.leaveMeAlone;
      default: return GreetingType.none;
    }
 }

  String? toJson() {
    switch(this) { 
      case GreetingType.long: return "LONG";
      case GreetingType.veryLong: return "VERY_LONG";
      case GreetingType.exhaustive: return "EXHAUSTIVE";
      case GreetingType.leaveMeAlone: return "LEAVE_ME_ALONE";
      default: return null;
    }
  }

}
