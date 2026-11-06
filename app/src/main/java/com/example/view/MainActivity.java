package com.example.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.GymBuddyApp;
import com.example.R;
import com.example.databinding.ActivityMainBinding;
import com.example.utils.AppPreferences;
import com.example.utils.ThemeHelper;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = GymBuddyApp.getInstance().getPreferences();

        // Configura o FAB de Check-in em destaque
        binding.fabCheckin.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CheckInActivity.class));
        });

        // Alternador rápido de tema no Toolbar
        binding.ivTopTheme.setOnClickListener(v -> {
            int current = preferences.getThemeMode();
            int next = (current == ThemeHelper.THEME_LIGHT) ? ThemeHelper.THEME_DARK : ThemeHelper.THEME_LIGHT;
            preferences.saveThemeMode(next);
            ThemeHelper.applyTheme(next);
        });

        // Configuração do menu inferior (4 itens: Início, Atividades, Ranking, Perfil)
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                binding.fabCheckin.show();
            } else if (itemId == R.id.nav_activities) {
                selectedFragment = new ActivitiesFragment();
                binding.fabCheckin.show();
            } else if (itemId == R.id.nav_ranking) {
                selectedFragment = new RankingFragment();
                binding.fabCheckin.hide();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
                binding.fabCheckin.hide();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Inicializa na tela Home se não houver estado salvo
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    public void selectTab(int menuId) {
        binding.bottomNavigation.setSelectedItemId(menuId);
    }
}
