import 'package:base/config/view_widget.dart';
import 'package:base/screens/login/login_action.dart';
import 'package:base/utils/hex_color.dart';
import 'package:base/utils/theme_color.dart';
import 'package:flutter/material.dart';

class LoginScreen extends StatefulWidget {
  @override
  // ignore: overridden_fields
  final Key? key;

  const LoginScreen({
    this.key,
  }) : super(key: key);

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends ViewWidget<LoginScreen, LoginAction> {
  @override
  LoginAction createViewActions() => LoginAction();

  @override
  Widget render(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Login'),
        backgroundColor: HexColor.fromHex(ThemeColors.PRIMARY),
      ),
      body: Center(
        child: ElevatedButton(
          onPressed: () {
            viewActions.login();
          },
          style: ElevatedButton.styleFrom(
            padding: EdgeInsets.symmetric(horizontal: 24, vertical: 12),
          ),
          child: const Text('Login with Zalo'),
        ),
      ),
    );
  }
}
