import 'package:flutter/material.dart';
import 'package:frontend/core/constant/app_colors.dart';
import 'package:frontend/view/widget/button.dart';
// import 'package:frontend/view/widget/textfield.dart';
// import 'package:frontend/view/screen/login_screen.dart';

class WelcomeScreen extends StatelessWidget {
  const WelcomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        color: AppColors.background,
        padding: const EdgeInsets.symmetric(vertical: 15, horizontal: 20),
        child: ListView(
          children: [
            const SizedBox(height: 80),
            // هنا سنضع الشعار لاحقاً
            Column(
              children: [
                const Text(
                  " مرحبا بك في منصة",
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: 35,
                    fontWeight: FontWeight.bold,
                    color: Colors.white,
                  ),
                ),
                const Text(
                  "تشاد الرقمية",
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: 30,
                    fontWeight: FontWeight.bold,
                    color: Colors.red,
                  ),
                ),
              ],
            ),

            const SizedBox(height: 500),
            CustomButtonAuth(
              text: "تسجيل الدخول",
              onPressed: () {
                Navigator.of(context).pushReplacementNamed("login");
              },
            ),
          ],
        ),
      ),
    );
  }
}
