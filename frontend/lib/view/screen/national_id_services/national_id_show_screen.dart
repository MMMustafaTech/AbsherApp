import 'package:flutter/material.dart';

class NationalIdShowScreen extends StatefulWidget {
  const NationalIdShowScreen({super.key});

  @override
  State<NationalIdShowScreen> createState() => _NationalIdShowScreenState();
}

class _NationalIdShowScreenState extends State<NationalIdShowScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF5F7FA),
      appBar: AppBar(
        backgroundColor: const Color(0xFF002F6C),
        foregroundColor: Colors.white,
        title: const Text("عرض الهوية الوطنية"),
        centerTitle: true,
        elevation: 0,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(vertical: 40, horizontal: 16),
        child: Column(
          children: [
            // عرض الكرت مباشرة
            _buildPassportCard(context),
            const SizedBox(height: 30),
            const Text(
              "نسخة رقمية معتمدة",
              style: TextStyle(color: Colors.grey, fontWeight: FontWeight.bold),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildPassportCard(BuildContext context) {
    return AspectRatio(
      aspectRatio: 1.42,
      child: LayoutBuilder(
        builder: (context, constraints) {
          final w = constraints.maxWidth;
          final h = constraints.maxHeight;

          return Stack(
            children: [
              // ===== صورة القالب الخلفية =====
              Positioned.fill(
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(12),
                  child: Image.asset(
                    "images/national_id_tamplate.jpeg",
                    fit: BoxFit.fill,
                  ),
                ),
              ),

              // النوع + الكود + الرقم
              Positioned(
                top: h * 0.12,
                left: w * 0.29,
                child: Row(
                  children: [
                    _buildText("P", w * 0.03, bold: true),
                    SizedBox(width: w * 0.10),
                    _buildText("TCD", w * 0.03, bold: true),
                    SizedBox(width: w * 0.21),
                    _buildText("P12345678", w * 0.03, bold: true),
                  ],
                ),
              ),

              // الاسم
              Positioned(
                top: h * 0.19,
                left: w * 0.29,
                child: _buildText(
                  "MAHAMAT SALEH MAHAMAT",
                  w * 0.031,
                  bold: true,
                ),
              ),

              // اللقب
              Positioned(
                top: h * 0.26,
                left: w * 0.29,
                child: _buildText("ZAIN", w * 0.031, bold: true),
              ),

              // الجنسية
              Positioned(
                top: h * 0.35,
                left: w * 0.29,
                child: _buildText("CHAD", w * 0.03, bold: true),
              ),

              // رقم الهوية NNI
              Positioned(
                top: h * 0.35,
                right: w * 0.10,
                child: _buildText("1234567890", w * 0.03, bold: true),
              ),

              // تاريخ الميلاد
              Positioned(
                top: h * 0.42,
                left: w * 0.29,
                child: _buildText("26/09/2002", w * 0.03, bold: true),
              ),

              // الجنس
              Positioned(
                top: h * 0.42,
                right: w * 0.21,
                child: _buildText("M", w * 0.03, bold: true),
              ),

              // مكان الميلاد
              Positioned(
                top: h * 0.49,
                left: w * 0.29,
                child: _buildText("SAUDI ARABIA", w * 0.03, bold: true),
              ),

              // المهنة
              Positioned(
                top: h * 0.50,
                right: w * 0.25,
                child: _buildText("STUDENT", w * 0.03, bold: true),
              ),

              // تاريخ الإصدار
              Positioned(
                top: h * 0.58,
                left: w * 0.29,
                child: _buildText("25/03/2022", w * 0.03, bold: true),
              ),

              // مكان الإصدار
              Positioned(
                top: h * 0.57,
                right: w * 0.25,
                child: _buildText("CHAD", w * 0.03, bold: true),
              ),

              // تاريخ الانتهاء
              Positioned(
                top: h * 0.65,
                left: w * 0.29,
                child: _buildText("23/03/2026", w * 0.03, bold: true),
              ),

              // سلطة الإصدار
              Positioned(
                top: h * 0.65,
                right: w * 0.05,
                child: _buildText(
                  "DG DE LA POLICE NATIONALE",
                  w * 0.022,
                  bold: true,
                ),
              ),
            ],
          );
        },
      ),
    );
  }

  Widget _buildText(String text, double size, {bool bold = false}) {
    return Text(
      text,
      style: TextStyle(
        fontSize: size,
        fontFamily: 'monospace',
        fontWeight: bold ? FontWeight.bold : FontWeight.normal,
        color: Colors.black,
      ),
    );
  }
}
