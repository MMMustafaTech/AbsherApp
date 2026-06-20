import 'package:flutter/material.dart';
import 'package:frontend/core/constant/app_colors.dart';

class CustomButtonAuth extends StatelessWidget {
  final String text;
  final void Function()? onPressed;
  final Color? color;
  final Color? textColor;

  const CustomButtonAuth({
    super.key,
    required this.text,
    this.onPressed,
    this.color,
    this.textColor,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      // margin: const EdgeInsets.only(top: 400),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(30),
        boxShadow: [
          BoxShadow(
            color: (color ?? Colors.black).withOpacity(0.5),
            spreadRadius: 0,
            blurRadius: 9,
            offset: const Offset(0, 5),
          ),
        ],
      ),
      child: MaterialButton(
        height: 50,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
        // padding: const EdgeInsets.symmetric(horizontal: 13),
        onPressed: onPressed,
        color: color ?? AppColors.buttonColor,
        // textColor: AppColors.textMain,
        elevation: 0,
        minWidth: double.infinity,
        child: Text(
          text,
          style: TextStyle(
            fontWeight: FontWeight.bold,
            fontSize: 30,
            color: textColor ?? AppColors.textMain,
          ),
        ),
      ),
    );
  }
}
