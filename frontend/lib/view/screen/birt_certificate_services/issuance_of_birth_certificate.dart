import 'package:flutter/material.dart';
import 'package:frontend/controller/birth_certificate_controller.dart';
import 'package:frontend/core/constant/app_colors.dart';

class IssuanceOfBirthCertificate extends StatefulWidget {
  const IssuanceOfBirthCertificate({super.key});

  @override
  State<IssuanceOfBirthCertificate> createState() => _IssuanceOfBirthCertificate();
}

class _IssuanceOfBirthCertificate extends State<IssuanceOfBirthCertificate> {
  final BirthCertificateController _controller = BirthCertificateController();
  bool _submitting = false;

  Future<void> _submit() async {
    setState(() => _submitting = true);
    await _controller.issuanceOfBirthCertificate(context);
    if (mounted) setState(() => _submitting = false);
  }

  @override
  Widget build(BuildContext context) => Scaffold(
        backgroundColor: AppColors.primary,
        appBar: AppBar(
          backgroundColor: AppColors.background,
          title: const Text('إصدار شهادة الميلاد'),
          foregroundColor: AppColors.primary,
          centerTitle: true,
        ),
        body: Center(
          child: Padding(
            padding: const EdgeInsets.all(24),
            child: Column(mainAxisSize: MainAxisSize.min, children: [
              const Icon(Icons.description_outlined, size: 72, color: Colors.white),
              const SizedBox(height: 20),
              const Text('تقديم طلب نسخة من شهادة الميلاد', style: TextStyle(color: Colors.white, fontSize: 20), textAlign: TextAlign.center),
              const SizedBox(height: 12),
              const Text('سيُرسل الطلب للمراجعة وتظهر حالته في صفحة طلباتي.', style: TextStyle(color: Colors.white70), textAlign: TextAlign.center),
              const SizedBox(height: 28),
              ElevatedButton.icon(
                onPressed: _submitting ? null : _submit,
                icon: _submitting ? const SizedBox.square(dimension: 18, child: CircularProgressIndicator(strokeWidth: 2)) : const Icon(Icons.send),
                label: const Text('تقديم الطلب'),
              ),
            ]),
          ),
        ),
      );
}
