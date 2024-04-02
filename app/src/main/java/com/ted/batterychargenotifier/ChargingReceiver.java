package com.ted.batterychargenotifier;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class ChargingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        // 处理充电状态和充电类型的变化
        if (isCharging) {
            if (usbCharge) {
                // USB充电
                // 发送充电通知
            } else if (acCharge) {
                // AC充电
                // 发送充电通知
            }
        } else {
            // 不在充电状态
            // 发送非充电通知
        }
    }
}