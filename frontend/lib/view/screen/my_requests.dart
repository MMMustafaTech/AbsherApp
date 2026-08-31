import 'package:flutter/material.dart';
import 'package:frontend/data/datasource/chari_api.dart';

class MyRequests extends StatefulWidget {
  const MyRequests({super.key});

  @override
  State<MyRequests> createState() => _MyRequests();
}

class _MyRequests extends State<MyRequests> {
  final ChariApi _api = ChariApi();
  late Future<List<Map<String, dynamic>>> _requests = _load();

  Future<List<Map<String, dynamic>>> _load() async {
    final lists = await Future.wait([
      _api.passportRequests(),
      _api.nationalIdentityRequests(),
      _api.birthCertificateRequests(),
    ]);
    final merged = <Map<String, dynamic>>[];
    for (final list in lists) {
      merged.addAll(list);
    }
    merged.sort((a, b) => '${b['submittedAt'] ?? ''}'.compareTo('${a['submittedAt'] ?? ''}'));
    return merged;
  }

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
          title: const Text('طلباتي'),
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
        body: FutureBuilder<List<Map<String, dynamic>>>(
          future: _requests,
          builder: (context, snapshot) {
            if (snapshot.connectionState != ConnectionState.done) return const Center(child: CircularProgressIndicator());
            if (snapshot.hasError) return Center(child: TextButton(onPressed: () => setState(() => _requests = _load()), child: const Text('تعذر تحميل الطلبات، حاول مجددًا')));
            final requests = snapshot.data ?? [];
            if (requests.isEmpty) return const Center(child: Text('لا توجد طلبات حتى الآن'));
            return RefreshIndicator(
              onRefresh: () async => setState(() => _requests = _load()),
              child: ListView.builder(
                itemCount: requests.length,
                itemBuilder: (context, index) {
                  final item = requests[index];
                  return Card(
                    margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                    child: ListTile(
                      leading: const Icon(Icons.assignment_outlined),
                      title: Text('${item['kind'] ?? 'طلب خدمة'}'),
                      subtitle: Text('${item['submittedAt'] ?? ''}'),
                      trailing: Chip(label: Text('${item['status'] ?? ''}')),
                    ),
                  );
                },
              ),
            );
          },
        ),
      );
}
