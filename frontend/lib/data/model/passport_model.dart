class PassportModel {
  final String passportNumber;
  final String firstName;
  final String lastName;
  final String birthDate;
  final String birthPlace;
  final String issueDate;
  final String expiryDate;
  final String issuePlace;
  final String issueingAuthority;
  final String profession;
  final String nationality;
  final String gender;

  PassportModel({
    required this.passportNumber,
    required this.firstName,
    required this.lastName,
    required this.birthDate,
    required this.birthPlace,
    required this.issueDate,
    required this.expiryDate,
    required this.issuePlace,
    required this.issueingAuthority,
    required this.profession,
    required this.nationality,
    required this.gender,
  });

  factory PassportModel.fromJson(Map<String, dynamic> json) => PassportModel(
    passportNumber: json["passportNumber"] ?? "",
    firstName: json["firstName"] ?? "",
    lastName: json["lastName"] ?? "",
    birthDate: json["dateOfBirth"] ?? json["birthDate"] ?? "",
    birthPlace: json["placeOfBirth"] ?? json["birthPlace"] ?? "",
    issueDate: json["issuedOn"] ?? json["issueDate"] ?? "",
    expiryDate: json["expiresOn"] ?? json["expiryDate"] ?? "",
    issuePlace: json["placeOfIssue"] ?? json["issuePlace"] ?? "",
    issueingAuthority: json["issuingAuthority"] ?? json["issueingAuthority"] ?? "",
    profession: json["profession"] ?? "",
    nationality: json["nationality"] ?? "",
    gender: json["sex"] ?? json["gender"] ?? "",
  );

  Map<String, dynamic> toJson() => {
    "passportNumber": passportNumber,
    "firstName": firstName,
    "lastName": lastName,
    "birthDate": birthDate,
    "birthPlace": birthPlace,
    "issueDate": issueDate,
    "expiryDate": expiryDate,
    "issuePlace": issuePlace,
    "issueingAuthority": issueingAuthority,
    "profession": profession,
    "nationality": nationality,
    "gender": gender,
  };
}
