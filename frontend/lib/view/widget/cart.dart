import 'package:flutter/material.dart';

class CustomFeatureCard extends StatelessWidget {
  final String title; // النص المكتوب على الكارد
  final IconData icon; // الأيقونة
  final Color startColor; // بداية التدرج اللوني
  final Color endColor; // نهاية التدرج اللوني
  final Color textColor; // لون النص والأيقونة
  final VoidCallback onTap; // الوظيفة التي تحدث عند الضغط
  final double height; // الارتفاع (قيمة اختيارية، افتراضياً أصغر)
  final double width;
  final double padding; // حشوة داخلية (اختيارية)
  final double iconSize; // حجم الأيقونة (اختيارية)
  final double fontSize; // حجم الخط (اختيارية)

  const CustomFeatureCard({
    super.key,
    required this.title,
    required this.icon,
    required this.startColor,
    required this.endColor,
    required this.onTap,
    this.textColor = Colors.white,
    this.height = 100.0,
    this.width = 40, // ارتفاع أصغر بشكل افتراضي
    this.padding = 16.0, // حشوة داخلية أقل بشكل افتراضي
    this.iconSize = 32.0, // حجم أيقونة أصغر
    this.fontSize = 16.0, // حجم خط أصغر
  });

  @override
  Widget build(BuildContext context) {
    // تحديد عرض البطاقة بناءً على ارتفاعها للحفاظ على نسبة مظهر متناسقة (مثلاً، العرض = الارتفاع * 0.8)
    final cardWidth = height * 0.85;

    return Material(
      color: Colors.transparent,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(
          20,
        ), // زوايا منحنية (borderRadius أقل بقليل)
        splashColor: Colors.white.withOpacity(0.15),
        child: Ink(
          width: 200, // عرض يتناسب مع الارتفاع (وليس double.infinity)
          height: height,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(20),
            gradient: LinearGradient(
              colors: [startColor, endColor],
              begin: Alignment.bottomLeft,
              end: Alignment.topRight,
            ),
            boxShadow: [
              BoxShadow(
                color: startColor.withOpacity(0.2),
                blurRadius: 8,
                offset: const Offset(0, 4),
              ),
            ],
          ),
          child: Padding(
            padding: EdgeInsets.all(padding),
            child: Column(
              // استخدام Column في المنتصف للحجم الأصغر
              mainAxisAlignment:
                  MainAxisAlignment.center, // محاذاة العناصر عمودياً في المنتصف
              crossAxisAlignment:
                  CrossAxisAlignment.center, // محاذاة العناصر أفقياً في المنتصف
              children: [
                // الأيقونة في المنتصف
                Icon(icon, size: iconSize, color: textColor.withOpacity(0.9)),
                const SizedBox(height: 8), // مسافة بين الأيقونة والنص
                // النص في المنتصف (أو يمكن وضعه في stack كما في الصورة ولكن column أفضل في الصغر)
                Text(
                  title,
                  textAlign: TextAlign.right, // محاذاة النص في المنتصف
                  style: TextStyle(
                    color: textColor,
                    fontSize: fontSize,
                    fontWeight: FontWeight.bold,
                    fontFamily:
                        'Tajawal', // تأكد من إضافة الخط في pubspec لو تستخدمه
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
