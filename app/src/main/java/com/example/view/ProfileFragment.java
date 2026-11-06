package com.example.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.GymBuddyApp;
import com.example.R;
import com.example.adapter.BadgeAdapter;
import com.example.adapter.CheckInHistoryAdapter;
import com.example.api.ApiClient;
import com.example.databinding.FragmentProfileBinding;
import com.example.model.Badge;
import com.example.model.CheckIn;
import com.example.model.Community;
import com.example.model.User;
import com.example.repository.GymBuddyRepository;
import com.example.utils.AppPreferences;
import com.example.utils.ThemeHelper;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private GymBuddyRepository repository;
    private AppPreferences preferences;
    private BadgeAdapter badgeAdapter;
    private CheckInHistoryAdapter historyAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = GymBuddyApp.getInstance().getRepository();
        preferences = GymBuddyApp.getInstance().getPreferences();

        badgeAdapter = new BadgeAdapter();
        binding.rvBadges.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBadges.setAdapter(badgeAdapter);

        historyAdapter = new CheckInHistoryAdapter();
        binding.rvProfileHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvProfileHistory.setAdapter(historyAdapter);

        binding.btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        binding.btnToggleTheme.setOnClickListener(v -> toggleTheme());
        binding.btnEditApiUrl.setOnClickListener(v -> showApiSettingsDialog());
        binding.btnLogout.setOnClickListener(v -> performLogout());
        binding.cardProfileManageGroups.setOnClickListener(v -> showManageGroupsDialog());

        loadUserData();
        loadBadges();
        loadCheckInHistory();
        updateThemeLabel();
        binding.tvCurrentApiUrl.setText(preferences.getApiBaseUrl());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
        loadBadges();
        loadCheckInHistory();
    }

    private void loadUserData() {
        User user = repository.getCurrentUser();
        if (user != null) {
            binding.tvProfileName.setText(user.getName());
            binding.tvProfileEmail.setText(user.getEmail());
            binding.tvStatLevel.setText("Nível " + user.getLevel());
            binding.tvStatXp.setText(user.getXp() + " XP total");
            binding.tvStatStreak.setText(user.getStreakDays() + " Dias");
            binding.tvStatCheckins.setText(user.getTotalCheckins() + " check-ins realizados");
        }
    }

    private void loadBadges() {
        repository.getBadges(new GymBuddyRepository.RepositoryCallback<List<Badge>>() {
            @Override
            public void onSuccess(List<Badge> list) {
                badgeAdapter.setBadges(list);
            }
            @Override
            public void onError(String message) {}
        });
    }

    private void loadCheckInHistory() {
        repository.getCheckInHistory(new GymBuddyRepository.RepositoryCallback<List<CheckIn>>() {
            @Override
            public void onSuccess(List<CheckIn> list) {
                historyAdapter.setCheckIns(list);
            }
            @Override
            public void onError(String message) {}
        });
    }

    private void toggleTheme() {
        int currentMode = preferences.getThemeMode();
        int newMode;
        if (currentMode == ThemeHelper.THEME_LIGHT) {
            newMode = ThemeHelper.THEME_DARK;
        } else {
            newMode = ThemeHelper.THEME_LIGHT;
        }

        preferences.saveThemeMode(newMode);
        ThemeHelper.applyTheme(newMode);
        updateThemeLabel();
    }

    private void updateThemeLabel() {
        int mode = preferences.getThemeMode();
        if (mode == ThemeHelper.THEME_DARK) {
            binding.tvCurrentThemeLabel.setText("Modo Escuro Ativo (Toque para Modo Claro)");
        } else {
            binding.tvCurrentThemeLabel.setText("Modo Claro Ativo (Toque para Modo Escuro)");
        }
    }

    private void showEditProfileDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        EditText etName = dialogView.findViewById(R.id.et_edit_name);
        EditText etEmail = dialogView.findViewById(R.id.et_edit_email);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_edit_cancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.btn_edit_save);

        User current = repository.getCurrentUser();
        if (current != null) {
            etName.setText(current.getName());
            etEmail.setText(current.getEmail());
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

            if (TextUtils.isEmpty(name)) {
                etName.setError("Informe seu nome");
                return;
            }

            repository.updateUserProfile(name, email, new GymBuddyRepository.RepositoryCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    dialog.dismiss();
                    loadUserData();
                    Toast.makeText(requireContext(), "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(requireContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void showApiSettingsDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_api_settings, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        EditText etUrl = dialogView.findViewById(R.id.et_api_url);
        MaterialButton btnDefault = dialogView.findViewById(R.id.btn_api_default);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_api_cancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.btn_api_save);

        etUrl.setText(preferences.getApiBaseUrl());

        btnDefault.setOnClickListener(v -> etUrl.setText(ApiClient.DEFAULT_BASE_URL));
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String url = etUrl.getText() != null ? etUrl.getText().toString().trim() : "";
            if (!url.endsWith("/")) {
                url += "/";
            }
            preferences.saveApiBaseUrl(url);
            binding.tvCurrentApiUrl.setText(url);
            dialog.dismiss();
            Toast.makeText(requireContext(), "URL da API atualizada com sucesso!", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void showManageGroupsDialog() {
        List<Community> communities = preferences.getCommunities();
        List<String> options = new ArrayList<>();
        options.add("+ Criar Novo Grupo");
        for (Community c : communities) {
            options.add("👥 " + c.getName() + " (" + c.getMemberCount() + " membros)");
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Grupos & Alunos (Administrador)")
                .setItems(options.toArray(new String[0]), (dialog, which) -> {
                    if (which == 0) {
                        showCreateGroupDialog();
                    } else {
                        Community selected = communities.get(which - 1);
                        String[] actions = new String[]{"Abrir Grupo", "🗑 Excluir Grupo"};
                        new AlertDialog.Builder(requireContext())
                                .setTitle(selected.getName())
                                .setItems(actions, (subDialog, actionWhich) -> {
                                    if (actionWhich == 0) {
                                        Intent intent = new Intent(requireContext(), CommunityDetailActivity.class);
                                        intent.putExtra(CommunityDetailActivity.EXTRA_COMMUNITY, selected);
                                        startActivity(intent);
                                    } else {
                                        confirmDeleteGroup(selected);
                                    }
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                    }
                })
                .setNegativeButton("Fechar", null)
                .show();
    }

    private void confirmDeleteGroup(Community community) {
        if (!isAdded() || community == null) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Excluir Grupo de Treino")
                .setMessage("Deseja realmente excluir o grupo '" + community.getName() + "'?")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Sim, Excluir", (dialog, which) -> {
                    repository.deleteCommunity(community.getId(), new GymBuddyRepository.RepositoryCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            if (!isAdded()) return;
                            Toast.makeText(requireContext(), "Grupo excluído com sucesso!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String message) {
                            if (!isAdded()) return;
                            Toast.makeText(requireContext(), "Erro ao excluir: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showCreateGroupDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_community, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        EditText etName = dialogView.findViewById(R.id.et_comm_name);
        EditText etDesc = dialogView.findViewById(R.id.et_comm_desc);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_comm_cancel);
        MaterialButton btnCreate = dialogView.findViewById(R.id.btn_comm_create);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnCreate.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String desc = etDesc.getText() != null ? etDesc.getText().toString().trim() : "";

            if (TextUtils.isEmpty(name)) {
                etName.setError("Informe o nome do grupo");
                return;
            }

            if (TextUtils.isEmpty(desc)) {
                desc = "Grupo para acompanhamento de treinos, cadastro de alunos e validação de check-ins.";
            }

            repository.createCommunity(name, desc, new GymBuddyRepository.RepositoryCallback<Community>() {
                @Override
                public void onSuccess(Community comm) {
                    dialog.dismiss();
                    Toast.makeText(requireContext(), "Grupo criado com sucesso! Você é o Administrador.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireContext(), CommunityDetailActivity.class);
                    intent.putExtra(CommunityDetailActivity.EXTRA_COMMUNITY, comm);
                    startActivity(intent);
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(requireContext(), "Erro ao criar grupo: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void performLogout() {
        repository.logout();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
