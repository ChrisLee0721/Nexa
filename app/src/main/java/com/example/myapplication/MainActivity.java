package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private View contentRoot;
    private View drawerBackdrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindViews();
    }

    private void bindViews() {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        drawerLayout = findViewById(R.id.main);
        contentRoot = findViewById(R.id.contentRoot);
        drawerBackdrop = findViewById(R.id.drawerBackdrop);
        drawerLayout.setScrimColor(Color.TRANSPARENT);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                topAppBar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                updateDrawerVisuals(slideOffset);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                updateDrawerVisuals(0f);
            }
        });

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(item -> {
            handleDrawerNavigation(item.getItemId());
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        GlassEffectUtil.applyIfSupported(findViewById(R.id.homePanel), AppPreferences.isBlurEnabled(this));
    }

    private void handleDrawerNavigation(int itemId) {
        if (itemId == R.id.nav_connection) {
            startActivity(new Intent(this, ConnectionActivity.class));
            return;
        }
        if (itemId == R.id.nav_link_settings) {
            startActivity(new Intent(this, LinkSettingsActivity.class));
            return;
        }
        if (itemId == R.id.nav_weather) {
            startActivity(new Intent(this, WeatherActivity.class));
            return;
        }
        if (itemId == R.id.nav_settings_page) {
            startActivity(new Intent(this, SettingsActivity.class));
            return;
        }
        if (itemId == R.id.nav_preview_page) {
            startActivity(new Intent(this, VisualPreviewActivity.class));
        }
    }

    private void updateDrawerVisuals(float slideOffset) {
        if (slideOffset <= 0f) {
            drawerBackdrop.setAlpha(0f);
            drawerBackdrop.setVisibility(View.GONE);
            GlassEffectUtil.applyIfSupported(contentRoot, false);
            return;
        }

        drawerBackdrop.setVisibility(View.VISIBLE);
        drawerBackdrop.setAlpha(Math.min(0.6f, slideOffset * 0.75f));
        GlassEffectUtil.applyIfSupported(contentRoot, true);
    }



}