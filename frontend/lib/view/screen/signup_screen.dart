import 'package:flutter/material.dart';
import 'package:frontend/controller/auth_controller.dart';
import 'package:frontend/core/constant/app_colors.dart';
import 'package:frontend/view/widget/button.dart';
import 'package:frontend/view/widget/textfield.dart';

class SignupScreen extends StatefulWidget {
  const SignupScreen({super.key});

  @override
  State<SignupScreen> createState() => _SignupScreenState();
}

class _SignupScreenState extends State<SignupScreen> {
  final AuthController controller = AuthController();
  bool _otpRequested = false;

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      resizeToAvoidBottomInset: false,
      backgroundColor: AppColors.background,
      body: SafeArea(
        child: LayoutBuilder(
          builder: (context, constraints) {
            return SingleChildScrollView(
              child: ConstrainedBox(
                constraints: BoxConstraints(minHeight: constraints.maxHeight),
                child: IntrinsicHeight(
                  child: Column(
                    children: [
                      const SizedBox(height: 90),
                      Expanded(
                        child: Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 20,
                            vertical: 50,
                          ),
                          decoration: const BoxDecoration(
                            color: Colors.white,
                            borderRadius: BorderRadius.only(
                              topLeft: Radius.circular(55),
                              topRight: Radius.circular(55),
                            ),
                          ),
                          child: Column(
                            children: [
                              const SizedBox(height: 20),
                              const Text(
                                "انشاء حساب",
                                style: TextStyle(
                                  color: Colors.black,
                                  fontSize: 30,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                              const SizedBox(height: 10),
                              CustomTextFormAuth(
                                hinttext: "رقم الهوية",
                                controller: controller.idController,
                                isPassword: false,
                              ),
                              const SizedBox(height: 12),
                              CustomTextFormAuth(
                                hinttext: "الايميل",
                                controller: controller.emailController,
                                isPassword: false,
                              ),
                              const SizedBox(height: 12),
                              CustomTextFormAuth(
                                hinttext: "كلمة المرور",
                                controller: controller.passController,
                                isPassword: true,
                              ),
                              const SizedBox(height: 12),
                              CustomTextFormAuth(
                                hinttext: "تاكيد كلمة المرور",
                                controller: controller.confirmPassController,
                                isPassword: true,
                              ),
                              if (_otpRequested) ...[
                                const SizedBox(height: 12),
                                CustomTextFormAuth(
                                  hinttext: "رمز التحقق المكوّن من 6 أرقام",
                                  controller: controller.otpController,
                                  isPassword: false,
                                ),
                              ],
                              const SizedBox(height: 10),
                              CustomButtonAuth(
                                text: _otpRequested
                                    ? "تأكيد الرمز وإنشاء الحساب"
                                    : "إرسال رمز التحقق",
                                onPressed: () async {
                                  final success = _otpRequested
                                      ? await controller.completeSignup(context)
                                      : await controller.requestSignupOtp(context);
                                  if (success) {
                                    if (_otpRequested) {
                                      if (context.mounted) {
                                        Navigator.of(context).pushNamed("login");
                                      }
                                    } else {
                                      setState(() => _otpRequested = true);
                                    }
                                  }
                                },
                              ),
                              SizedBox(height: 30),
                              CustomButtonAuth(
                                text: "تسجيل الدخول",
                                textColor: Colors.white,
                                color: Colors.grey[400],
                                onPressed: () {
                                  Navigator.of(context).pushNamed("login");
                                },
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}
