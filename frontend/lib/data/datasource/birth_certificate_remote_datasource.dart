import 'package:frontend/data/datasource/chari_api.dart';
import 'package:frontend/data/model/birth_certificate_model.dart';

class BirthCertificateRemoteDatasource {
  BirthCertificateRemoteDatasource({ChariApi? api}) : _api = api ?? ChariApi();

  final ChariApi _api;

  Future<BirthCertificateModel?> getBirthCertificate() async {
    try {
      return BirthCertificateModel.fromJson(await _api.birthCertificateDocument());
    } catch (_) {
      return null;
    }
  }

  Future<bool> issuanceOfBirthCertificate() => _submit('CERTIFICATE_EXTRACT');

  Future<bool> newBornRegistration(Map<String, dynamic> details) =>
      _submit('NEWBORN_REGISTRATION', newbornRegistration: details);

  Future<bool> requestForDataCorrection(String reason) =>
      _submit('DATA_CORRECTION', reason: reason);

  Future<bool> _submit(
    String kind, {
    String? reason,
    Map<String, dynamic>? newbornRegistration,
  }) async {
    try {
      await _api.submitBirthCertificateRequest(
        kind,
        reason: reason,
        newbornRegistration: newbornRegistration,
      );
      return true;
    } catch (_) {
      return false;
    }
  }

  Future<List<Map<String, dynamic>>> requests() => _api.birthCertificateRequests();
}
