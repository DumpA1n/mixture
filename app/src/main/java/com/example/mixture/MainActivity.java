package com.example.mixture;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "DUMPA1N";

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

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        @SuppressLint("RestrictedApi") BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            for (int i = 0; i < menuView.getChildCount(); i++) {
                final View itemView = menuView.getChildAt(i);
                boolean isSelected = bottomNavigationView.getMenu().getItem(i).getItemId() == item.getItemId();
                float targetY = isSelected ? -20f : 0f;
                itemView.animate().translationY(targetY).setDuration(200).start();
            }

            if (itemId == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
                return true;
            }
            else if (itemId == R.id.nav_function) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FunctionFragment())
                        .commit();
                return true;
            }
            else if (itemId == R.id.nav_profile) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MineFragment())
                        .commit();
                return true;
            }
            return false;
        });

        // copyAssets(this, "models", getFilesDir().getAbsolutePath() + "/models");
    }
}