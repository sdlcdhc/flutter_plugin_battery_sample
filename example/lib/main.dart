import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:battery_plugin/battery_plugin.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  num _batteryLevel = -1;
  BatteryState _batteryState = BatteryState.unknown;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    num batteryLevel;
    BatteryState batteryState;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      batteryLevel = await BatteryPlugin.getBatteryLevel;
      batteryState = await BatteryPlugin.getBatteryState;
    } on PlatformException {
      batteryLevel = -1;
      batteryState = BatteryState.unknown;
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _batteryLevel = batteryLevel;
      _batteryState = batteryState;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              Text('BatteryLevel: $_batteryLevel\n'),
              Text('BatteryState: $_batteryState\n'),
              FlatButton(
                child: Text('刷新'),
                onPressed: initPlatformState,
              )
            ],
          ),
        ),
      ),
    );
  }
}
