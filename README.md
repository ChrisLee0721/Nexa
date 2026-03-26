# MyApplication - Smart Desk Terminal Bridge

Android Java app that can connect to a smart desktop monitoring terminal through Bluetooth, Wi-Fi, or Azure API as a receive channel, with built-in temperature/humidity visualization and free weather data.

## Features
- Bluetooth SPP connection (target by MAC address)
- Wi-Fi TCP connection (target by `host:port`)
- Azure HTTP receive channel (`GET`, optional `x-functions-key`)
- Send manual payload to local terminal (Bluetooth/Wi-Fi)
- Temperature/Humidity visualization with progress bars
- Free weather API integration via Open-Meteo (no API key)
- Top-right menu navigation (Connection / Settings / Visual Preview)
- Dropdown-based settings page (`Spinner`) for sampling and connection priority
- Frosted-glass style panels with API 31+ blur effect fallback

## Main Files
- `app/src/main/java/com/example/myapplication/MainActivity.java`
- `app/src/main/java/com/example/myapplication/SettingsActivity.java`
- `app/src/main/java/com/example/myapplication/VisualPreviewActivity.java`
- `app/src/main/java/com/example/myapplication/AppPreferences.java`
- `app/src/main/java/com/example/myapplication/GlassEffectUtil.java`
- `app/src/main/java/com/example/myapplication/data/MonitorRepository.java`
- `app/src/main/java/com/example/myapplication/data/SensorSnapshot.java`
- `app/src/main/java/com/example/myapplication/connectivity/BluetoothTerminalConnector.java`
- `app/src/main/java/com/example/myapplication/connectivity/WifiTerminalConnector.java`
- `app/src/main/java/com/example/myapplication/network/AzureApiClient.java`
- `app/src/main/java/com/example/myapplication/network/WeatherApiClient.java`

## Quick Run
1. Open in Android Studio.
2. Run app on Android 12+ device and allow Bluetooth permission when prompted.
3. Choose mode:
   - Bluetooth: enter terminal MAC (e.g. `00:11:22:33:44:55`)
   - Wi-Fi: enter endpoint (e.g. `192.168.1.50:9000`)
   - Azure: fill endpoint like `https://<your-app>.azurewebsites.net/api/latest`
4. Tap `连接终端`.
5. Tap `接收数据` to pull telemetry (Azure mode reads cloud data, Bluetooth/Wi-Fi mode shows local snapshot view).
6. Use `获取天气` with latitude/longitude to load Open-Meteo weather.
7. Use the toolbar menu to open:
   - `设置`: dropdown tuning and blur toggle
   - `视觉预览`: glassmorphism UI preview page

## PowerShell Build/Test
```powershell
./gradlew.bat :app:assembleDebug
./gradlew.bat :app:testDebugUnitTest
```

