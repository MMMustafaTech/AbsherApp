import 'package:flutter/material.dart';

class CustomQuickAcsessCard extends StatelessWidget {
  final String title; // النص المكتوب على الكارد
  final IconData icon; // الأيقونة
  final Color startColor; // بداية التدرج اللوني
  final Color endColor; // نهاية التدرج اللوني
  final Color textColor;
  final double height; // لون النص والأيقونة (افتراضياً أبيض)
  final VoidCallback onTap; // الوظيفة التي تحدث عند الضغط

  const CustomQuickAcsessCard({
    super.key,
    required this.title,
    required this.icon,
    required this.startColor,
    required this.endColor,
    required this.onTap,
    this.height = 100.0,
    this.textColor = Colors.white, // قيمة افتراضية بيضاء إذا لم تحددها
  });

  @override
  Widget build(BuildContext context) {
    final cardWidth = height * 0.85;

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
          height: height, // ارتفاع الكارد، يمكنك جعله متغيراً أيضاً إذا أردت
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
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.end,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Icon(icon, size: 55, color: textColor.withOpacity(0.85)),

                Text(
                  title,
                  textAlign: TextAlign.right,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                  style: TextStyle(
                    color: textColor,
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                    fontFamily: 'Tajawal',
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
