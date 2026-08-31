import 'package:flutter/material.dart';
import 'package:frontend/data/datasource/birth_certificate_remote_datasource.dart';
import 'package:frontend/data/model/birth_certificate_model.dart';

class BirthCertificateController {
  final BirthCertificateRemoteDatasource _datasource =
      BirthCertificateRemoteDatasource();

  Future<BirthCertificateModel?> fetchBirthCertificate(BuildContext context) async {
    final document = await _datasource.getBirthCertificate();
    if (document == null && context.mounted) {
      _showMessage(context, 'لا توجد شهادة ميلاد مرتبطة بهذا الحساب');
    }
    return document;
  }

  Future<bool> issuanceOfBirthCertificate(BuildContext context) =>
      _submit(context, _datasource.issuanceOfBirthCertificate(), 'تم تقديم طلب الشهادة');

  Future<bool> newBornRegistration(
    BuildContext context,
    Map<String, dynamic> details,
  ) => _submit(
        context,
        _datasource.newBornRegistration(details),
        'تم تقديم طلب تسجيل المولود',
      );

  Future<bool> requestForDataCorrection(BuildContext context, String reason) =>
      _submit(
        context,
        _datasource.requestForDataCorrection(reason),
        'تم تقديم طلب تعديل البيانات',
      );

  Future<bool> _submit(BuildContext context, Future<bool> operation, String successMessage) async {
    final success = await operation;
    if (context.mounted) {
      _showMessage(context, success ? successMessage : 'تعذر تقديم الطلب. حاول لاحقًا.');
    }
    return success;
  }

  void _showMessage(BuildContext context, String message) {
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(message)));
  }
}
