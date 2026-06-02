package com.example.livestockguardian.util;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.livestockguardian.AiToolsActivity;
import com.example.livestockguardian.LivestockListActivity;
import com.example.livestockguardian.MainActivity;
import com.example.livestockguardian.ProfileActivity;
import com.example.livestockguardian.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public final class NavigationHelper {

    private NavigationHelper() {
    }

    public static void setupBottomNavigation(AppCompatActivity activity, int currentItemId) {
        BottomNavigationView bottomNavigation = activity.findViewById(R.id.bottomNavigation);
        if (bottomNavigation == null) {
            return;
        }

        bottomNavigation.setSelectedItemId(currentItemId);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == currentItemId) {
                return true;
            }

            Class<?> target = null;
            if (itemId == R.id.nav_home) {
                target = MainActivity.class;
            } else if (itemId == R.id.nav_ai) {
                target = AiToolsActivity.class;
            } else if (itemId == R.id.nav_livestock) {
                target = LivestockListActivity.class;
            } else if (itemId == R.id.nav_profile) {
                target = ProfileActivity.class;
            }

            if (target != null) {
                Intent intent = new Intent(activity, target);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
                activity.finish();
            }
            return true;
        });
    }
}
