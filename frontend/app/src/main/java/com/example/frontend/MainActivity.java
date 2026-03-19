package com.example.frontend;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        fab = findViewById(R.id.fab_camera);

        if (savedInstanceState == null) {
            loadFragment(new DetectFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;

            int id = item.getItemId();
            if (id == R.id.nav_home) {
                selected = new HomeFragment();
            } else if (id == R.id.nav_library) {
                selected = new LibraryFragment();
            } else if (id == R.id.nav_chat) {
                selected = new ChatRAGFragment();
            } else if (id == R.id.nav_history) {
                selected = new HistoryFragment();
            } else {
                return false;
            }

            loadFragment(selected);
            return true;
        });

        fab.setOnClickListener(v -> {
            loadFragment(new DetectFragment());
            bottomNav.getMenu().setGroupCheckable(0, true, true);
            bottomNav.setSelectedItemId(R.id.nav_empty);
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }
}