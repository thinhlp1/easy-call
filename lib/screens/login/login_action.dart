import 'package:base/config/view_actions.dart';
import 'package:flutter/services.dart';

class LoginAction extends ViewActions {
  static const platform = MethodChannel('com.example.zalogin/auth');

  Future<void> login() async {
    try {
      final oauthCode = await platform.invokeMethod('loginZalo');
      // OAuthCode nhận được, bạn có thể dùng nó để lấy access token
      print("Received OAuth Code: $oauthCode");
    } on PlatformException catch (e) {
      print("Failed to login with Zalo: ${e.message}");
    }
  }
}
