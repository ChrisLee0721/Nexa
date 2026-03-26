package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settingsRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        Spinner samplingSpinner = findViewById(R.id.samplingSpinner);
        Spinner prioritySpinner = findViewById(R.id.prioritySpinner);
        SwitchCompat blurSwitch = findViewById(R.id.blurSwitch);
        Button saveButton = findViewById(R.id.saveSettingsButton);

        samplingSpinner.setSelection(AppPreferences.getSamplingIndex(this));
        prioritySpinner.setSelection(AppPreferences.getPriorityIndex(this));
        blurSwitch.setChecked(AppPreferences.isBlurEnabled(this));

        saveButton.setOnClickListener(v -> {
            AppPreferences.setSamplingIndex(this, samplingSpinner.getSelectedItemPosition());
            AppPreferences.setPriorityIndex(this, prioritySpinner.getSelectedItemPosition());
            AppPreferences.setBlurEnabled(this, blurSwitch.isChecked());
            Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
        });

        GlassEffectUtil.applyIfSupported(findViewById(R.id.settingsPanel), AppPreferences.isBlurEnabled(this));
    }
}

