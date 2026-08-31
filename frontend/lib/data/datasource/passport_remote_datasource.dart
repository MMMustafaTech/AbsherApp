import 'package:frontend/data/datasource/chari_api.dart';
import 'package:frontend/data/model/passport_model.dart';

class PassportRemoteDatasource {
  PassportRemoteDatasource({ChariApi? api}) : _api = api ?? ChariApi();

  final ChariApi _api;

  Future<PassportModel?> getPassport() async {
    try {
      return PassportModel.fromJson(await _api.passportDocument());
    } catch (_) {
      return null;
    }
  }

  Future<bool> requestPassport(String kind, {String? reason}) async {
    try {
      await _api.submitPassportRequest(kind, reason: reason);
      return true;
    } catch (_) {
      return false;
    }
  }

  Future<List<Map<String, dynamic>>> requests() => _api.passportRequests();
}
