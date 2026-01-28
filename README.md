# BatteryChargeNotifier

一个简单的 Android 充电电量提醒应用：在开始充电时启动前台服务监控电量，达到设定阈值后发送通知提醒。

## 功能

- 充电开始（插上电源）时启动前台服务并开始监控电量
- 电量达到阈值（0–100%）时发送通知，并自动停止监控服务
- 拔掉电源时停止监控服务
- 可在设置页配置：
  - 启动时是否发送通知
  - 通知内容
  - 提醒阈值百分比

## 工作原理（简述）

- 静态广播接收器监听充电/断电事件：
  - `ACTION_POWER_CONNECTED` → 启动 `BatteryMonitorService`
  - `ACTION_POWER_DISCONNECTED` → 停止 `BatteryMonitorService`
- `BatteryMonitorService` 在 `onCreate()` 中切到前台服务（`startForeground`），并动态注册 `ACTION_BATTERY_CHANGED`：
  - 当电量 ≥ 阈值：发送提醒通知，然后 `stopSelf()`

相关代码：

- `BatteryLevelReceiver`：`app/src/main/java/com/ted/batterychargenotifier/BatteryLevelReceiver.kt`
- `BatteryMonitorService`：`app/src/main/java/com/ted/batterychargenotifier/BatteryMonitorService.kt`
- 设置页面：`FirstFragment` / `SecondFragment`

## 环境要求

- Android Studio（推荐最新稳定版）
- Android SDK：`compileSdk 34`（Android 14），`minSdk 28`（Android 9）

## 构建与运行

### Android Studio

1. 用 Android Studio 打开项目根目录 `ChargeNotifier`
2. 等待 Gradle Sync 完成
3. 选择设备并运行 `app` 模块

### 命令行（Windows / macOS / Linux）

Debug 构建：

```bash
./gradlew assembleDebug
```

Release 构建：

```bash
./gradlew assembleRelease
```

## 权限说明

- `POST_NOTIFICATIONS`：Android 13+ 需要用户授权才能发通知
- `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_DATA_SYNC`：用于运行前台服务进行电量监控

---

# BatteryChargeNotifier (English)

A lightweight Android charge reminder app. It starts a foreground service when charging begins, monitors battery level, and sends a notification once the configured threshold is reached.

## Features

- Starts a foreground monitoring service on power connected
- Sends a notification when battery level reaches the threshold (0–100%)
- Stops the monitoring service when power is disconnected or after notifying
- Configurable in the settings screen:
  - Whether to send a startup notification
  - Custom notification message
  - Battery threshold percentage

## How it works (high level)

- A manifest-registered receiver listens for:
  - `ACTION_POWER_CONNECTED` → start `BatteryMonitorService`
  - `ACTION_POWER_DISCONNECTED` → stop `BatteryMonitorService`
- `BatteryMonitorService` calls `startForeground(...)` and dynamically listens to `ACTION_BATTERY_CHANGED`:
  - When battery ≥ threshold: post an alert notification and `stopSelf()`

## Requirements

- Android Studio (latest stable recommended)
- Android SDK: `compileSdk 34` (Android 14), `minSdk 28` (Android 9)

## Build & Run

```bash
./gradlew assembleDebug
./gradlew assembleRelease
```

## Permissions

- `POST_NOTIFICATIONS` (Android 13+ runtime permission)
- `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_DATA_SYNC` (foreground service monitoring)

