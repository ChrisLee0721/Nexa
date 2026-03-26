package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.data.MonitorRepository;
import com.example.myapplication.data.SensorSnapshot;
import com.example.myapplication.network.AzureApiClient;
import com.example.myapplication.network.WeatherApiClient;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionActivity extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH_CONNECT = 1001;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final MonitorRepository repository = new MonitorRepository(new AzureApiClient(), new WeatherApiClient());

    private RadioGroup modeGroup;
    private EditText targetInput;
    private EditText messageInput;
    private TextView statusView;
    private TextView temperatureValue;
    private TextView humidityValue;
    private ProgressBar temperatureProgress;
    private ProgressBar humidityProgress;
    private boolean localConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.connectionRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.connectionToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        bindViews();
        initUiActions();
        GlassEffectUtil.applyIfSupported(findViewById(R.id.statusPanel), AppPreferences.isBlurEnabled(this));
        GlassEffectUtil.applyIfSupported(findViewById(R.id.metricsPanel), AppPreferences.isBlurEnabled(this));
    }

    private void bindViews() {
        modeGroup = findViewById(R.id.modeGroup);
        targetInput = findViewById(R.id.targetInput);
        messageInput = findViewById(R.id.messageInput);
        statusView = findViewById(R.id.statusView);
        temperatureValue = findViewById(R.id.temperatureValue);
        humidityValue = findViewById(R.id.humidityValue);
        temperatureProgress = findViewById(R.id.temperatureProgress);
        humidityProgress = findViewById(R.id.humidityProgress);
    }

    private void initUiActions() {
        Button connectButton = findViewById(R.id.connectButton);
        Button sendTerminalButton = findViewById(R.id.sendTerminalButton);
        Button receiveDataButton = findViewById(R.id.receiveDataButton);

        modeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.modeBluetooth) {
                targetInput.setHint(R.string.hint_target_bt);
            } else if (checkedId == R.id.modeWifi) {
                targetInput.setHint(R.string.hint_target_wifi);
            } else {
                targetInput.setHint(R.string.hint_target_azure);
            }
        });
        targetInput.setHint(R.string.hint_target_bt);

        connectButton.setOnClickListener(v -> connectChannel());
        sendTerminalButton.setOnClickListener(v -> sendToTerminal());
        receiveDataButton.setOnClickListener(v -> receiveData());
    }

    private void connectChannel() {
        if (isAzureMode()) {
            String endpoint = AppPreferences.getAzureEndpoint(this);
            if (TextUtils.isEmpty(endpoint) || endpoint.contains("<your-app>")) {
                toast(getString(R.string.link_settings_required));
                return;
            }
            appendStatus("Azure 通道已就绪: " + endpoint);
            return;
        }

        String target = targetInput.getText().toString().trim();
        if (TextUtils.isEmpty(target)) {
            toast("请先填写终端目标");
            return;
        }

        if (isBluetoothMode() && !hasBluetoothPermission()) {
            requestBluetoothPermission();
            return;
        }

        appendStatus("Connecting terminal...");
        executorService.execute(() -> {
            try {
                if (isBluetoothMode()) {
                    repository.connectBluetooth(target);
                    localConnected = true;
                    appendStatusFromWorker("Bluetooth connected: " + target);
                } else {
                    String[] hostPort = target.split(":");
                    if (hostPort.length != 2) {
                        throw new IOException("Wi-Fi target must be host:port");
                    }
                    int port = Integer.parseInt(hostPort[1]);
                    repository.connectWifi(hostPort[0], port);
                    localConnected = true;
                    appendStatusFromWorker("Wi-Fi connected: " + target);
                }
            } catch (Exception e) {
                localConnected = false;
                appendStatusFromWorker("Connect failed: " + e.getMessage());
            }
        });
    }

    private void sendToTerminal() {
        if (isAzureMode()) {
            toast("Azure 模式用于接收，请使用【接收数据】");
            return;
        }

        String payload = messageInput.getText().toString().trim();
        if (TextUtils.isEmpty(payload)) {
            toast("请先输入发送内容");
            return;
        }

        executorService.execute(() -> {
            try {
                repository.sendToTerminal(payload);
                appendStatusFromWorker("Payload sent to terminal.");
            } catch (IOException e) {
                appendStatusFromWorker("Send failed: " + e.getMessage());
            }
        });
    }

    private void receiveData() {
        appendStatus("正在接收数据...");
        executorService.execute(() -> {
            try {
                SensorSnapshot snapshot;
                if (isAzureMode()) {
                    String endpoint = AppPreferences.getAzureEndpoint(this);
                    String apiKey = AppPreferences.getAzureApiKey(this);
                    if (TextUtils.isEmpty(endpoint) || endpoint.contains("<your-app>")) {
                        appendStatusFromWorker(getString(R.string.link_settings_required));
                        return;
                    }
                    snapshot = repository.getFromAzure(endpoint, apiKey);
                } else {
                    if (!localConnected) {
                        appendStatusFromWorker("请先连接蓝牙或 Wi-Fi 终端");
                        return;
                    }
                    snapshot = repository.getMockLocalSnapshot(isBluetoothMode() ? "bluetooth" : "wifi");
                }
                updateMetricsFromWorker(snapshot);
                appendStatusFromWorker("接收成功，来源: " + snapshot.source);
            } catch (Exception e) {
                appendStatusFromWorker("接收失败: " + e.getMessage());
            }
        });
    }

    private boolean isBluetoothMode() {
        return modeGroup.getCheckedRadioButtonId() == R.id.modeBluetooth;
    }

    private boolean isAzureMode() {
        return modeGroup.getCheckedRadioButtonId() == R.id.modeAzure;
    }

    private boolean hasBluetoothPermission() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S) {
            return true;
        }
        return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestBluetoothPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                REQUEST_BLUETOOTH_CONNECT
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_CONNECT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                appendStatus("Bluetooth permission granted.");
            } else {
                appendStatus("Bluetooth permission denied.");
            }
        }
    }

    private void updateMetricsFromWorker(SensorSnapshot snapshot) {
        runOnUiThread(() -> {
            double temperature = Math.max(0d, Math.min(60d, snapshot.temperature));
            double humidity = Math.max(0d, Math.min(100d, snapshot.humidity));
            temperatureValue.setText(String.format(Locale.US, "温度：%.1f °C", temperature));
            humidityValue.setText(String.format(Locale.US, "湿度：%.1f %%", humidity));
            temperatureProgress.setProgress((int) Math.round(temperature));
            humidityProgress.setProgress((int) Math.round(humidity));
        });
    }

    private void appendStatus(String line) {
        statusView.setText(statusView.getText().toString() + "\n" + line);
    }

    private void appendStatusFromWorker(String line) {
        runOnUiThread(() -> appendStatus(line));
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        repository.closeConnector();
        executorService.shutdownNow();
    }
}

