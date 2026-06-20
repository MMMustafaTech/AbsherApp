class UserModel {
  final String id;
  final String email;
  final String password;
  final String? name;

  UserModel({
    required this.id,
    required this.email,
    required this.password,
    this.name,
  });

  Map<String, dynamic> toJson() => {
    "nationalId": id,
    "email": email,
    "password": password,
    "name": name,
  };

  factory UserModel.fromJson(Map<String, dynamic> json) => UserModel(
    id: json["nationalId"] ?? "",
    email: json["email"] ?? "",
    password: json["password"] ?? "",
    name: json["name"] ?? "",
  );
}
