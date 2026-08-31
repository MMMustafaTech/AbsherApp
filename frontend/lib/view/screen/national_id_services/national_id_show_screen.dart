import 'package:flutter/material.dart';
import 'package:frontend/data/datasource/identity_remote_datasource.dart';

class NationalIdShowScreen extends StatefulWidget {
  const NationalIdShowScreen({super.key});

  @override
  State<NationalIdShowScreen> createState() => _NationalIdShowScreenState();
}

class _NationalIdShowScreenState extends State<NationalIdShowScreen> {
  final IdentityRemoteDatasource _datasource = IdentityRemoteDatasource();
  late final Future<Map<String, dynamic>> _document = _datasource.document();

  @override
  Widget build(BuildContext context) => Scaffold(
        backgroundColor: const Color(0xFFF5F7FA),
        appBar: AppBar(backgroundColor: const Color(0xFF002F6C), foregroundColor: Colors.white, title: const Text('عرض الهوية الوطنية'), centerTitle: true),
        body: FutureBuilder<Map<String, dynamic>>(
          future: _document,
          builder: (context, snapshot) {
            if (snapshot.connectionState != ConnectionState.done) return const Center(child: CircularProgressIndicator());
            if (snapshot.hasError || !snapshot.hasData) return const Center(child: Text('لا توجد هوية وطنية مرتبطة بهذا الحساب'));
            final document = snapshot.data!;
            return ListView(
              padding: const EdgeInsets.all(16),
              children: [
                const Icon(Icons.badge_rounded, size: 72, color: Color(0xFF002F6C)),
                const SizedBox(height: 16),
                Text('${document['fullName'] ?? document['name'] ?? ''}', textAlign: TextAlign.center, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 22)),
                const SizedBox(height: 20),
                ...document.entries.where((entry) => entry.value != null).map(
                      (entry) => Card(child: ListTile(title: Text(_label(entry.key)), trailing: Text('${entry.value}'))),
                    ),
              ],
            );
          },
        ),
      );

  String _label(String key) => key.replaceAllMapped(RegExp(r'[A-Z]'), (match) => ' ${match.group(0)}').trim();
}
