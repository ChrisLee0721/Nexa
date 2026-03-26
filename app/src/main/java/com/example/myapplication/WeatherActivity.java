package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.data.MonitorRepository;
import com.example.myapplication.network.AzureApiClient;
import com.example.myapplication.network.WeatherApiClient;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherActivity extends AppCompatActivity {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final MonitorRepository repository = new MonitorRepository(new AzureApiClient(), new WeatherApiClient());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.weatherRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.weatherToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        EditText latInput = findViewById(R.id.latInput);
        EditText lonInput = findViewById(R.id.lonInput);
        TextView weatherValue = findViewById(R.id.weatherValue);
        Button refreshButton = findViewById(R.id.refreshWeatherButton);

        latInput.setText("22.3193");
        lonInput.setText("114.1694");

        refreshButton.setOnClickListener(v -> {
            String latText = latInput.getText().toString().trim();
            String lonText = lonInput.getText().toString().trim();
            if (TextUtils.isEmpty(latText) || TextUtils.isEmpty(lonText)) {
                Toast.makeText(this, "请填写经纬度", Toast.LENGTH_SHORT).show();
                return;
            }
            executorService.execute(() -> {
                try {
                    double latitude = Double.parseDouble(latText);
                    double longitude = Double.parseDouble(lonText);
                    String summary = repository.getWeatherSummary(latitude, longitude);
                    runOnUiThread(() -> weatherValue.setText(summary));
                } catch (Exception e) {
                    runOnUiThread(() -> weatherValue.setText("天气获取失败: " + e.getMessage()));
                }
            });
        });

        GlassEffectUtil.applyIfSupported(findViewById(R.id.weatherPanel), AppPreferences.isBlurEnabled(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdownNow();
    }
}

