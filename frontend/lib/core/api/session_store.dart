class SessionTokens {
  const SessionTokens({
    required this.accessToken,
    required this.refreshToken,
    required this.expiresIn,
  });

  final String accessToken;
  final String refreshToken;
  final int expiresIn;

  factory SessionTokens.fromJson(Map<String, dynamic> json) => SessionTokens(
        accessToken: json['accessToken'] as String,
        refreshToken: json['refreshToken'] as String,
        expiresIn: (json['expiresIn'] as num?)?.toInt() ?? 0,
      );
}

/// Keeps credentials only while the app is running.
/// Add platform secure storage before releasing the app publicly.
class SessionStore {
  SessionStore._();

  static SessionTokens? _tokens;

  static SessionTokens? get tokens => _tokens;
  static bool get isAuthenticated => _tokens != null;

  static void save(SessionTokens tokens) => _tokens = tokens;
  static void clear() => _tokens = null;
}
