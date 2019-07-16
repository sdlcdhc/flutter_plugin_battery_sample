import 'dart:async';

import 'package:flutter/services.dart';

enum BatteryState {
  unknown,  /// 未知
  unplugged,  /// 设备未通电;电池正在放电
  charging, /// 该设备插入电源，电池充电不足 100%。
  full /// 设备插入电源，电池充电达到 100%。
}

class BatteryPlugin {
  static const MethodChannel _channel =
      const MethodChannel('battery_plugin');

  static Future<num> get getBatteryLevel async {
    final num level = await _channel.invokeMethod('getBatteryLevel');
    return level;
  }

  static Future<BatteryState> get getBatteryState async {
    final int state = await _channel.invokeMethod('getBatteryState');
    return BatteryState.values[state];
  }
}
