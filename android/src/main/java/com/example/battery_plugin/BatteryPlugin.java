package com.example.battery_plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.EventChannel.StreamHandler;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * BatteryPlugin
 */
public class BatteryPlugin implements MethodCallHandler, StreamHandler {
    private BroadcastReceiver chargingStateChangeReceiver;
    private final Registrar registrar;

    BatteryPlugin(Registrar registrar) {
        this.registrar = registrar;
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel methodChannel = new MethodChannel(registrar.messenger(), "battery_plugin");
        final EventChannel eventChannel = new EventChannel(registrar.messenger(), "battery_plugin");
        final BatteryPlugin instance = new BatteryPlugin(registrar);
        eventChannel.setStreamHandler(instance);
        methodChannel.setMethodCallHandler(instance);

    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("getBatteryLevel")) {
            int batteryLevel = getBatteryLevel();

            if (batteryLevel != -1) {
                result.success(batteryLevel);
            } else {
                result.error("UNAVAILABLE", "Battery level not available.", null);
            }
        } else if (call.method.equals("getBatteryState")) {
            int batteryState = getBatteryState();

            if (batteryState != -1) {
                result.success(batteryState);
            } else {
                result.error("UNAVAILABLE", "Battery state not available.", null);
            }
        } else {
            result.notImplemented();
        }
    }

    private int getBatteryLevel() {
        int batteryLevel;
        Context context = registrar.context();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager batteryManager =
                    (BatteryManager) context.getSystemService(context.BATTERY_SERVICE);
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {
            Intent intent =
                    new ContextWrapper(context)
                            .registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            batteryLevel =
                    (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100)
                            / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }

        return batteryLevel;
    }

    private int getBatteryState() {
        int batteryState;
        Context context = registrar.context();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            BatteryManager batteryManager =
                    (BatteryManager) context.getSystemService(context.BATTERY_SERVICE);
            batteryState = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
        } else {
            Intent intent =
                    new ContextWrapper(context)
                            .registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            batteryState = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        }

        return batteryState;
    }

    @Override
    public void onListen(Object arguments, EventSink events) {
        chargingStateChangeReceiver = createChargingStateChangeReceiver(events);
        registrar
                .context()
                .registerReceiver(
                        chargingStateChangeReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onCancel(Object arguments) {
        registrar.context().unregisterReceiver(chargingStateChangeReceiver);
        chargingStateChangeReceiver = null;
    }

    private BroadcastReceiver createChargingStateChangeReceiver(final EventSink events) {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        events.success("charging");
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        events.success("full");
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        events.success("discharging");
                        break;
                    default:
                        events.error("UNAVAILABLE", "Charging status unavailable", null);
                        break;
                }
            }
        };
    }
}
