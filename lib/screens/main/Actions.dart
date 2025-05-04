import 'package:base/config/view_actions.dart';
import 'package:base/screens/contact/contact_view.dart';
import 'package:base/screens/login/login_view.dart';
import 'package:base/utils/get_hashkey.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class MainActions extends ViewActions {
  List<Widget> views = <Widget>[
    const LoginScreen(),
    const ContactScreen(),
    const GetHashkey(),
  ];
}
