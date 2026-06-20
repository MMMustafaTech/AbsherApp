import 'package:flutter/material.dart';

class CustomFeatureCard extends StatelessWidget {
  final String title; // النص المكتوب على الكارد
  final IconData icon; // الأيقونة
  final Color startColor; // بداية التدرج اللوني
  final Color endColor; // نهاية التدرج اللوني
  final Color textColor; // لون النص والأيقونة (افتراضياً أبيض)
  final VoidCallback onTap; // الوظيفة التي تحدث عند الضغط

  const CustomFeatureCard({
    super.key,
    required this.title,
    required this.icon,
    required this.startColor,
    required this.endColor,
    required this.onTap,
    this.textColor = Colors.white, // قيمة افتراضية بيضاء إذا لم تحددها
  });

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.transparent, // لجعل تأثير النقر (Ripple) يظهر فوق التدرج
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(
          24,
        ), // تدوير حواف النقر لتطابق الكارد
        splashColor: Colors.white.withOpacity(
          0.2,
        ), // لون تأثير الموجة عند الضغط
        child: Ink(
          width: double.infinity,
          height: 140, // ارتفاع الكارد، يمكنك جعله متغيراً أيضاً إذا أردت
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(24),
            gradient: LinearGradient(
              colors: [startColor, endColor],
              begin: Alignment.bottomLeft,
              end: Alignment.topRight,
            ),
            boxShadow: [
              BoxShadow(
                color: startColor.withOpacity(0.4),
                blurRadius: 12,
                offset: const Offset(0, 6),
              ),
            ],
          ),
          child: Padding(
            padding: const EdgeInsets.all(24.0),
            child: Stack(
              children: [
                // النص في أعلى اليمين (مناسب للعربي)
                Positioned(
                  top: 0,
                  right: 0,
                  child: Text(
                    title,
                    style: TextStyle(
                      color: textColor,
                      fontSize: 22,
                      fontWeight: FontWeight.bold,
                      fontFamily:
                          'Tajawal', // تأكد من إضافة الخط في pubspec لو تستخدمه
                    ),
                  ),
                ),
                // الأيقونة في أسفل اليسار
                Positioned(
                  bottom: 0,
                  left: 0,
                  child: Icon(
                    icon,
                    size: 55,
                    color: textColor.withOpacity(0.85),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
