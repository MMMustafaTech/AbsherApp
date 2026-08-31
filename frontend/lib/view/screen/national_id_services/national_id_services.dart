import 'package:flutter/material.dart';
import 'package:frontend/core/constant/app_colors.dart';
import 'package:frontend/data/datasource/identity_remote_datasource.dart';

class NationalIdServices extends StatefulWidget {
  const NationalIdServices({super.key});

  @override
  State<NationalIdServices> createState() => _NationalIdServices();
}

class _NationalIdServices extends State<NationalIdServices> {
  final IdentityRemoteDatasource _datasource = IdentityRemoteDatasource();
  bool _submitting = false;

  Future<void> _submit(String kind, {String? reason}) async {
    if (_submitting) return;
    setState(() => _submitting = true);
    final success = await _datasource.submit(kind, reason: reason);
    if (!mounted) return;
    setState(() => _submitting = false);
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(success ? 'تم تقديم الطلب بنجاح' : 'تعذر تقديم الطلب. حاول لاحقًا.')),
    );
  }

  Widget _buildServiceCard(String title, IconData icon, VoidCallback onTap) => Container(
        margin: const EdgeInsets.only(bottom: 16),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(15),
          boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 10)],
        ),
        child: ListTile(
          contentPadding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
          onTap: _submitting ? null : onTap,
          leading: const Icon(Icons.arrow_back_ios_new, size: 18, color: Colors.grey),
          title: Text(title, textAlign: TextAlign.right, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF2D3142))),
          trailing: Icon(icon, color: const Color(0xFF002F6C)),
        ),
      );

  @override
  Widget build(BuildContext context) => Scaffold(
        backgroundColor: const Color(0xFFF8FAFC),
        appBar: AppBar(backgroundColor: const Color(0xFF002F6C), foregroundColor: Colors.white, title: const Text('خدمات الهوية الوطنية'), centerTitle: true),
        body: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Column(crossAxisAlignment: CrossAxisAlignment.end, children: [
            const SizedBox(height: 30),
            const Text('مرحباً بك', style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold, color: AppColors.textMain)),
            const SizedBox(height: 30),
            _buildServiceCard('تجديد الهوية الوطنية', Icons.fiber_new_rounded, () => _submit('RENEWAL')),
            _buildServiceCard('إصدار هوية وطنية', Icons.assignment_turned_in_rounded, () => _submit('ISSUANCE')),
            _buildServiceCard('طلب تعديل بيانات', Icons.edit_note_rounded, () => _submit('DATA_CORRECTION', reason: 'طلب تعديل بيانات الهوية')),
            _buildServiceCard('بدل فاقد / تالف', Icons.replay_rounded, () => _submit('LOST', reason: 'طلب بدل فاقد أو تالف')),
          ]),
        ),
      );
}
