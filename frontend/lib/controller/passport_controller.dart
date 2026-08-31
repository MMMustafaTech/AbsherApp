import 'package:flutter/material.dart';
import 'package:frontend/data/datasource/passport_remote_datasource.dart';
import 'package:frontend/data/model/passport_model.dart';

class PassportController {
  final PassportRemoteDatasource _datasource = PassportRemoteDatasource();
  PassportModel? passport;

  Future<PassportModel?> fetchPassport(BuildContext context) async {
    passport = await _datasource.getPassport();
    if (!context.mounted) return null;
    if (passport == null) {
      _showMessage(context, 'لا توجد وثيقة جواز مرتبطة بهذا الحساب');
    }
    return passport;
  }

  Future<bool> submitRequest(
    BuildContext context,
    String kind, {
    String? reason,
  }) async {
    final success = await _datasource.requestPassport(kind, reason: reason);
    if (!context.mounted) return false;
    _showMessage(
      context,
      success ? 'تم تقديم الطلب بنجاح' : 'تعذر تقديم الطلب. تحقق من حالة طلباتك وحاول لاحقًا.',
    );
    return success;
  }

  void _showMessage(BuildContext context, String message) {
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(message)));
  }
}
