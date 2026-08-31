import 'package:frontend/data/datasource/chari_api.dart';

class IdentityRemoteDatasource {
  IdentityRemoteDatasource({ChariApi? api}) : _api = api ?? ChariApi();

  final ChariApi _api;

  Future<Map<String, dynamic>> document() => _api.nationalIdentityDocument();

  Future<bool> submit(String kind, {String? reason}) async {
    try {
      await _api.submitNationalIdentityRequest(kind, reason: reason);
      return true;
    } catch (_) {
      return false;
    }
  }
}
