import 'package:flutter/material.dart';
import 'package:frontend/view/screen/birt_certificate_services/issuance_of_birth_certificate.dart';
import 'package:frontend/view/screen/birt_certificate_services/newborn_registration.dart';
import 'package:frontend/view/screen/birt_certificate_services/request_for_data_correction.dart';
import 'package:frontend/view/screen/home_screen.dart';
import 'package:frontend/view/screen/my_account.dart';
import 'package:frontend/view/screen/my_requests.dart';
import 'package:frontend/view/screen/national_id_services/national_id_services.dart';
import 'package:frontend/view/screen/national_id_services/national_id_show_screen.dart';
import 'package:frontend/view/screen/other_services.dart';
import 'package:frontend/view/screen/passport_services/passport_screen.dart';
import 'package:frontend/view/screen/welcome_screen.dart';
import 'package:frontend/view/screen/login_screen.dart';
import 'package:frontend/view/screen/signup_screen.dart';
import 'package:frontend/view/screen/birt_certificate_services/birth_certificate_services_screen.dart';
import 'package:frontend/view/screen/passport_services/passport_services_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,

      home: WelcomeScreen(),
      routes: {
        "login": (context) => LoginScreen(),
        "welcome": (context) => WelcomeScreen(),
        "signup": (context) => SignupScreen(),
        "home": (context) => HomeScreen(),
        "showpassport": (context) => PassportScreen(),
        "passportServices": (context) => PassportServicesScreen(),
        "birthCertificateServicesScreen": (context) =>
            BirthCertificateServicesScreen(),
        "NationalIdServices": (context) => NationalIdServices(),
        "NationalIdShowScreen": (context) => NationalIdShowScreen(),
        "RequestForDataCorrection": (context) => RequestForDataCorrection(),
        "NewbornRegistration": (context) => NewbornRegistration(),
        "IssuanceOfBirthCertificate": (context) => IssuanceOfBirthCertificate(),
        "OtherServices": (context) => OtherServices(),
        "MyRequests": (context) => MyRequests(),
        "MyAccount": (context) => MyAccount(),
      },
    );
  }
}
