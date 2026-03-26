package com.example.myapplication;

import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.view.View;

public final class GlassEffectUtil {
    private GlassEffectUtil() {
    }

    public static void applyIfSupported(View view, boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(enabled ? RenderEffect.createBlurEffect(18f, 18f, Shader.TileMode.CLAMP) : null);
        }
    }
}

