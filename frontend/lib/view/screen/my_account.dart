import 'package:flutter/material.dart';
import 'package:frontend/data/datasource/chari_api.dart';

class MyAccount extends StatefulWidget {
  const MyAccount({super.key});

  @override
  State<MyAccount> createState() => _MyAccount();
}

class _MyAccount extends State<MyAccount> {
  final ChariApi _api = ChariApi();
  late Future<Map<String, dynamic>> _profile = _api.profile();

  Future<void> _logout() async {
    await _api.logout();
    if (mounted) Navigator.of(context).pushNamedAndRemoveUntil('login', (_) => false);
  }

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
          title: const Text('حسابي'),
          centerTitle: true,
          leading: IconButton(
            tooltip: 'العودة للرئيسية',
            icon: const Icon(Icons.arrow_back),
            onPressed: () {
              final navigator = Navigator.of(context);
              if (navigator.canPop()) {
                navigator.pop();
              } else {
                navigator.pushReplacementNamed('home');
              }
            },
          ),
        ),
        body: FutureBuilder<Map<String, dynamic>>(
          future: _profile,
          builder: (context, snapshot) {
            if (snapshot.connectionState != ConnectionState.done) return const Center(child: CircularProgressIndicator());
            if (snapshot.hasError) return Center(child: TextButton(onPressed: () => setState(() => _profile = _api.profile()), child: const Text('تعذر تحميل الملف الشخصي')));
            final profile = snapshot.data ?? {};
            return ListView(
              padding: const EdgeInsets.all(16),
              children: [
                const CircleAvatar(radius: 36, child: Icon(Icons.person, size: 38)),
                const SizedBox(height: 20),
                _item('البريد الإلكتروني', '${profile['email'] ?? ''}'),
                _item('رقم الهوية', '${profile['maskedNationalId'] ?? ''}'),
                _item('رقم الهاتف', '${profile['maskedVerifiedPhone'] ?? ''}'),
                _item('حالة الهاتف', profile['phoneVerified'] == true ? 'موثّق' : 'غير موثّق'),
                const SizedBox(height: 24),
                ElevatedButton.icon(onPressed: _logout, icon: const Icon(Icons.logout), label: const Text('تسجيل الخروج')),
              ],
            );
          },
        ),
      );

  Widget _item(String label, String value) => Card(child: ListTile(title: Text(label), trailing: Text(value)));
}
