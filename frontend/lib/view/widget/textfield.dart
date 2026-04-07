import 'package:flutter/material.dart';

class CustomTextFormAuth extends StatefulWidget {
  final String hinttext;
  final bool isPassword;
  final TextEditingController controller;

  const CustomTextFormAuth({
    super.key,
    required this.hinttext,
    this.isPassword = false,
    required this.controller,
  });

  @override
  State<CustomTextFormAuth> createState() => _CustomTextFormAuthState();
}

class _CustomTextFormAuthState extends State<CustomTextFormAuth> {
  late bool obscureText;

  @override
  void initState() {
    obscureText = widget.isPassword;
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 15),
      child: TextFormField(
        controller: widget.controller,
        obscureText: obscureText,
        decoration: InputDecoration(
          hintText: widget.hinttext,
          // contentPadding: const EdgeInsets.symmetric(
          //   vertical: 18,
          //   horizontal: 25,
          // ),
          filled: true,
          fillColor: Colors.grey[300],
          suffixIcon: widget.isPassword
              ? IconButton(
                  icon: Icon(
                    obscureText ? Icons.visibility_off : Icons.visibility,
                  ),
                  onPressed: () {
                    setState(() {
                      obscureText = !obscureText;
                    });
                  },
                )
              : null,
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(20),
            borderSide: BorderSide.none,
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(20),
            borderSide: BorderSide.none,
          ),
        ),
      ),
    );
  }
}
