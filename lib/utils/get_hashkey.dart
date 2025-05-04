import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class GetHashkey extends StatelessWidget {
  const GetHashkey({super.key});

  Future<String> getZaloHashKey() async {
    const platform = MethodChannel('com.example.zalogin/auth');
    try {
      final hashKey = await platform.invokeMethod<String>('getHashKey');
      print('Zalo Hash Key: $hashKey');
      return hashKey ?? 'Không nhận được';
    } catch (e) {
      return 'Lỗi: $e';
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('Zalo Hash Key')),
        body: Center(
          child: FutureBuilder<String>(
            future: getZaloHashKey(),
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return const CircularProgressIndicator();
              } else {
                return Text('Zalo Hash Key:\n${snapshot.data}');
              }
            },
          ),
        ),
      ),
    );
  }
}
