package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class LinkSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_link_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.linkSettingsRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.linkSettingsToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        EditText endpointInput = findViewById(R.id.azureEndpointInput);
        EditText apiKeyInput = findViewById(R.id.azureApiKeyInput);
        Button saveButton = findViewById(R.id.saveLinkButton);

        endpointInput.setText(AppPreferences.getAzureEndpoint(this));
        apiKeyInput.setText(AppPreferences.getAzureApiKey(this));

        saveButton.setOnClickListener(v -> {
            String endpoint = endpointInput.getText().toString().trim();
            String apiKey = apiKeyInput.getText().toString().trim();
            if (TextUtils.isEmpty(endpoint)) {
                Toast.makeText(this, R.string.link_settings_required, Toast.LENGTH_SHORT).show();
                return;
            }
            AppPreferences.setAzureEndpoint(this, endpoint);
            AppPreferences.setAzureApiKey(this, apiKey);
            Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
        });

        GlassEffectUtil.applyIfSupported(findViewById(R.id.linkSettingsPanel), AppPreferences.isBlurEnabled(this));
    }
}

