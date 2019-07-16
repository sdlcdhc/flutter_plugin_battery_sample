#import "BatteryPlugin.h"

@implementation BatteryPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"battery_plugin"
            binaryMessenger:[registrar messenger]];
    BatteryPlugin* instance = [[BatteryPlugin alloc] init];
    [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
      result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else if ([@"getBatteryLevel" isEqualToString:call.method]) {
      [[UIDevice currentDevice] setBatteryMonitoringEnabled:YES];
      float batteryLevel = [[UIDevice currentDevice] batteryLevel];
      [[UIDevice currentDevice] setBatteryMonitoringEnabled:NO];
    result([NSNumber numberWithFloat:batteryLevel]);
  } else if ([@"getBatteryState" isEqualToString:call.method]) {
      [[UIDevice currentDevice] setBatteryMonitoringEnabled:YES];
      UIDeviceBatteryState batteryState = [[UIDevice currentDevice] batteryState];
      [[UIDevice currentDevice] setBatteryMonitoringEnabled:NO];
      result([NSNumber numberWithInt:batteryState]);
  } else {
    result(FlutterMethodNotImplemented);
  }
}

@end
