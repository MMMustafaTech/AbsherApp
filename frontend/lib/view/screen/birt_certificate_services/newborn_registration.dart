import 'package:flutter/material.dart';
import 'package:frontend/core/constant/app_colors.dart';
import 'package:frontend/controller/birth_certificate_controller.dart';

class NewbornRegistration extends StatefulWidget {
  const NewbornRegistration({super.key});

  @override
  State<NewbornRegistration> createState() => _NewbornRegistration();
}

class _NewbornRegistration extends State<NewbornRegistration> {
  String? _selectedField;
  String? _selectedGender;

  final List<Map<String, String>> _fields = [
    {"title": "الأب", "subtitle": "طلب تسجيل مولود"},
    {"title": "الأم", "subtitle": "طلب تسجيل مولود"},
  ];

  final List<Map<String, String>> _genders = [
    {"title": "ذكر"},
    {"title": "أنثى"},
  ];

  final TextEditingController _fatherIdController = TextEditingController();
  final TextEditingController _motherIdController = TextEditingController();
  final TextEditingController _fatherNameController = TextEditingController();
  final TextEditingController _motherNameController = TextEditingController();
  final TextEditingController _babyNameController = TextEditingController();
  final TextEditingController _hospitalController = TextEditingController();
  final TextEditingController _cityController = TextEditingController();
  final TextEditingController _countryController = TextEditingController();
  final TextEditingController _birthDateController = TextEditingController();
  bool _submitting = false;

  @override
  void dispose() {
    _fatherIdController.dispose();
    _motherIdController.dispose();
    _fatherNameController.dispose();
    _motherNameController.dispose();
    _babyNameController.dispose();
    _hospitalController.dispose();
    _cityController.dispose();
    _countryController.dispose();
    _birthDateController.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    final childName = _babyNameController.text.trim().split(RegExp(r'\\s+'));
    if (_selectedField == null ||
        _selectedGender == null ||
        childName.length < 2 ||
        _fatherNameController.text.trim().isEmpty ||
        _motherNameController.text.trim().isEmpty ||
        _fatherIdController.text.trim().isEmpty ||
        _motherIdController.text.trim().isEmpty ||
        _hospitalController.text.trim().isEmpty ||
        _birthDateController.text.trim().isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text(
            'أكمل جميع الحقول المطلوبة، واكتب اسم المولود الأول والأخير.',
          ),
        ),
      );
      return;
    }
    if (!RegExp(
      r'^\\d{4}-\\d{2}-\\d{2}$',
    ).hasMatch(_birthDateController.text.trim())) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('اكتب تاريخ الميلاد بصيغة YYYY-MM-DD.')),
      );
      return;
    }

    setState(() => _submitting = true);
    final success = await BirthCertificateController()
        .newBornRegistration(context, {
          'childFirstName': childName.first,
          'childLastName': childName.skip(1).join(' '),
          'dateOfBirth': _birthDateController.text.trim(),
          'placeOfBirth': _hospitalController.text.trim(),
          'gender': _selectedGender == 'ذكر' ? 'MALE' : 'FEMALE',
          'fatherFullName': _fatherNameController.text.trim(),
          'fatherNationalId': _fatherIdController.text.trim(),
          'motherFullName': _motherNameController.text.trim(),
          'motherNationalId': _motherIdController.text.trim(),
        });
    if (!mounted) return;
    setState(() => _submitting = false);
    if (success) Navigator.of(context).pop();
  }

  Widget _buildSectionHeader({
    required String title,
    required String subtitle,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.end,
      children: [
        Text(
          title,
          style: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
            color: Color(0xFF1a1a2e),
          ),
        ),
        if (subtitle.isNotEmpty)
          Text(
            subtitle,
            style: const TextStyle(fontSize: 12, color: Colors.grey),
          ),
      ],
    );
  }

  Widget _buildSectionCard({required Widget child}) {
    return Container(
      width: double.infinity,
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: child,
    );
  }

  Widget _buildChip({
    required String title,
    required bool isSelected,
    required VoidCallback onTap,
    String subtitle = "",
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        margin: const EdgeInsets.only(left: 8),
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
        decoration: BoxDecoration(
          color: isSelected ? const Color(0xFF002F6C) : Colors.white,
          border: Border.all(
            color: isSelected ? const Color(0xFF002F6C) : Colors.grey.shade300,
          ),
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Icon(
                  isSelected
                      ? Icons.radio_button_checked
                      : Icons.radio_button_off,
                  size: 14,
                  color: isSelected ? Colors.white : Colors.grey,
                ),
                const SizedBox(width: 6),
                Text(
                  title,
                  style: TextStyle(
                    color: isSelected ? Colors.white : const Color(0xFF1a1a2e),
                    fontWeight: FontWeight.bold,
                    fontSize: 13,
                  ),
                ),
              ],
            ),
            if (subtitle.isNotEmpty)
              Text(
                subtitle,
                style: TextStyle(
                  color: isSelected ? Colors.white70 : Colors.grey,
                  fontSize: 10,
                ),
              ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.primary,
      appBar: AppBar(
        foregroundColor: AppColors.primary,
        backgroundColor: AppColors.background,
        title: const Text(
          "تسجيل مولود",
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            // ===== مقدم الطلب =====
            _buildSectionCard(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  _buildSectionHeader(
                    title: "مقدم الطلب",
                    subtitle: "اختر من يقوم بتقديم الطلب",
                  ),
                  const SizedBox(height: 16),
                  SizedBox(
                    height: 70,
                    child: ListView.builder(
                      scrollDirection: Axis.horizontal,
                      reverse: true,
                      itemCount: _fields.length,
                      itemBuilder: (context, index) {
                        final field = _fields[index];
                        return _buildChip(
                          title: field["title"]!,
                          subtitle: field["subtitle"]!,
                          isSelected: _selectedField == field["title"],
                          onTap: () =>
                              setState(() => _selectedField = field["title"]),
                        );
                      },
                    ),
                  ),
                ],
              ),
            ),

            // ===== هوية الوالدين =====
            _buildSectionCard(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  _buildSectionHeader(
                    title: "هوية الوالدين",
                    subtitle: "أدخل أرقام هوية الوالدين",
                  ),
                  const SizedBox(height: 12),
                  _buildInputField(
                    hint: "اسم الأب الكامل",
                    icon: Icons.person_outline,
                    controller: _fatherNameController,
                  ),
                  _buildInputField(
                    hint: "رقم هوية الأب",
                    icon: Icons.person_outline,
                    controller: _fatherIdController,
                  ),
                  _buildInputField(
                    hint: "اسم الأم الكامل",
                    icon: Icons.person_outline,
                    controller: _motherNameController,
                  ),
                  _buildInputField(
                    hint: "رقم هوية الأم",
                    icon: Icons.person_outline,
                    controller: _motherIdController,
                  ),
                ],
              ),
            ),

            // ===== بيانات المولود (كل البيانات في كونتينر واحد) =====
            _buildSectionCard(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  _buildSectionHeader(
                    title: "بيانات المولود",
                    subtitle: "أدخل جميع بيانات المولود",
                  ),
                  const SizedBox(height: 16),

                  // الاسم
                  _buildInputField(
                    hint: "اسم المولود الأول والأخير",
                    icon: Icons.person,
                    controller: _babyNameController,
                  ),
                  const SizedBox(height: 10),
                  // الجنس
                  const Align(
                    alignment: Alignment.centerRight,
                    child: Text(
                      "الجنس",
                      style: TextStyle(
                        fontSize: 13,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFF1a1a2e),
                      ),
                    ),
                  ),
                  const SizedBox(height: 8),
                  SizedBox(
                    height: 50,
                    child: ListView.builder(
                      scrollDirection: Axis.horizontal,
                      reverse: true,
                      itemCount: _genders.length,
                      itemBuilder: (context, index) {
                        final gender = _genders[index];
                        return _buildChip(
                          title: gender["title"]!,
                          isSelected: _selectedGender == gender["title"],
                          onTap: () =>
                              setState(() => _selectedGender = gender["title"]),
                        );
                      },
                    ),
                  ),

                  const SizedBox(height: 25),

                  // المستشفى
                  _buildInputField(
                    hint: "المستشفى",
                    icon: Icons.local_hospital_outlined,
                    controller: _hospitalController,
                  ),

                  // المدينة
                  _buildInputField(
                    hint: "المدينة",
                    icon: Icons.location_city_outlined,
                    controller: _cityController,
                  ),

                  // الدولة
                  _buildInputField(
                    hint: "الدولة",
                    icon: Icons.flag_outlined,
                    controller: _countryController,
                  ),

                  // تاريخ الميلاد
                  _buildInputField(
                    hint: "تاريخ الميلاد",
                    icon: Icons.calendar_today_outlined,
                    controller: _birthDateController,
                  ),
                ],
              ),
            ),

            // ===== زر التقديم =====
            SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                onPressed: _submitting ? null : _submit,
                label: const Text(
                  "تقديم طلب التسجيل",
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.containerBackground,
                  foregroundColor: AppColors.background,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(14),
                  ),
                ),
              ),
            ),

            const SizedBox(height: 12),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(Icons.lock_outline, size: 14, color: Colors.grey.shade500),
                const SizedBox(width: 4),
                Text(
                  "سيتم مراجعة الطلب وإشعارك بالنتيجة",
                  style: TextStyle(color: Colors.grey.shade500, fontSize: 12),
                ),
              ],
            ),
            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }

  Widget _buildInputField({
    required String hint,
    required IconData icon,
    required TextEditingController controller,
  }) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 8),
      child: TextFormField(
        controller: controller,
        textAlign: TextAlign.right,
        decoration: InputDecoration(
          hintText: hint,
          hintStyle: const TextStyle(color: Colors.grey, fontSize: 12),
          prefixIcon: Icon(icon, color: const Color(0xFF002F6C), size: 18),
          filled: true,
          fillColor: const Color(0xFFF5F7FA),
          contentPadding: const EdgeInsets.symmetric(
            vertical: 12,
            horizontal: 12,
          ),
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(12),
            borderSide: BorderSide(color: Colors.grey.shade200),
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(12),
            borderSide: BorderSide(color: Colors.grey.shade200),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(12),
            borderSide: const BorderSide(color: Color(0xFF002F6C)),
          ),
        ),
      ),
    );
  }
}
