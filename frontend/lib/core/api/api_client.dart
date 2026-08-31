import 'dart:convert';
import 'dart:typed_data';

import 'package:frontend/core/api/session_store.dart';
import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';

class ApiException implements Exception {
  ApiException(this.statusCode, this.message);

  final int statusCode;
  final String message;

  factory ApiException.fromResponse(http.Response response) {
    String message = 'تعذر إكمال الطلب. حاول مرة أخرى.';
    try {
      final decoded = jsonDecode(response.body);
      if (decoded is Map && decoded['message'] is String) {
        message = decoded['message'] as String;
      }
    } catch (_) {}
    return ApiException(response.statusCode, message);
  }

  @override
  String toString() => message;
}

class ApiClient {
  ApiClient({http.Client? client}) : _client = client ?? http.Client();

  static const baseUrl = String.fromEnvironment(
    'API_BASE_URL',
    defaultValue: 'https://chari-api.onrender.com',
  );

  final http.Client _client;

  Future<http.Response> request(
    String method,
    String path, {
    Map<String, dynamic>? body,
    bool authenticated = false,
  }) async {
    var response = await _send(
      method,
      path,
      body: body,
      authenticated: authenticated,
    );

    if (authenticated && response.statusCode == 401 && await _refreshTokens()) {
      response = await _send(
        method,
        path,
        body: body,
        authenticated: true,
      );
    }

    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw ApiException.fromResponse(response);
    }
    return response;
  }

  Future<http.Response> _send(
    String method,
    String path, {
    Map<String, dynamic>? body,
    required bool authenticated,
  }) async {
    final request = http.Request(method, Uri.parse('$baseUrl$path'));
    request.headers['Accept'] = 'application/json';
    if (body != null) {
      request.headers['Content-Type'] = 'application/json';
      request.body = jsonEncode(body);
    }
    final accessToken = SessionStore.tokens?.accessToken;
    if (authenticated && accessToken != null) {
      request.headers['Authorization'] = 'Bearer $accessToken';
    }
    return http.Response.fromStream(await _client.send(request));
  }

  Future<bool> _refreshTokens() async {
    final refreshToken = SessionStore.tokens?.refreshToken;
    if (refreshToken == null) return false;
    try {
      final response = await _send(
        'POST',
        '/api/v1/auth/refresh',
        body: {'refreshToken': refreshToken},
        authenticated: false,
      );
      if (response.statusCode != 200) {
        SessionStore.clear();
        return false;
      }
      SessionStore.save(
        SessionTokens.fromJson(jsonDecode(response.body) as Map<String, dynamic>),
      );
      return true;
    } catch (_) {
      SessionStore.clear();
      return false;
    }
  }

  Future<http.Response> upload(
    String path, {
    required Uint8List bytes,
    required String fileName,
    required String contentType,
  }) async {
    final request = http.MultipartRequest('POST', Uri.parse('$baseUrl$path'));
    final token = SessionStore.tokens?.accessToken;
    if (token == null) throw ApiException(401, 'يرجى تسجيل الدخول أولاً.');
    request.headers['Authorization'] = 'Bearer $token';
    request.files.add(http.MultipartFile.fromBytes(
      'file',
      bytes,
      filename: fileName,
      contentType: _mediaType(contentType),
    ));
    final response = await http.Response.fromStream(await request.send());
    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw ApiException.fromResponse(response);
    }
    return response;
  }

  MediaType _mediaType(String contentType) {
    final parts = contentType.split('/');
    return parts.length == 2
        ? MediaType(parts.first, parts.last)
        : MediaType('application', 'octet-stream');
  }
}
