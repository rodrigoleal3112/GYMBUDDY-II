package com.example.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.GymBuddyApp;
import com.example.R;
import com.example.adapter.CheckInHistoryAdapter;
import com.example.databinding.FragmentHomeBinding;
import com.example.model.CheckIn;
import com.example.model.Community;
import com.example.model.User;
import com.example.repository.GymBuddyRepository;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private GymBuddyRepository repository;
    private CheckInHistoryAdapter historyAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = GymBuddyApp.getInstance().getRepository();

        historyAdapter = new CheckInHistoryAdapter();
        binding.rvRecentCheckins.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRecentCheckins.setAdapter(historyAdapter);

        binding.tvViewAllHistory.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectTab(R.id.nav_profile);
            }
        });

        View.OnClickListener manageGroupsClick = v -> showManageGroupsDialog();
        binding.cardHomeQuickGroups.setOnClickListener(manageGroupsClick);
        binding.btnHomeManageGroups.setOnClickListener(manageGroupsClick);

        loadUserData();
        loadRecentCheckIns();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
        loadRecentCheckIns();
    }

    private void loadUserData() {
        User user = repository.getCurrentUser();
        if (user != null) {
            binding.tvGreeting.setText("Olá, " + user.getName() + "!");
            binding.tvCurrentLevel.setText("Nível " + user.getLevel());
            binding.tvXpSummary.setText(user.getXp() + " XP");

            int currentLvl = user.getLevel();
            int baseLevelXp = (currentLvl - 1) * 100;
            int xpInCurrentLvl = Math.max(0, user.getXp() - baseLevelXp);
            int progressPercent = Math.min(100, Math.max(0, xpInCurrentLvl));
            binding.pbXpProgress.setProgress(progressPercent);

            int xpRemaining = Math.max(0, (currentLvl * 100) - user.getXp());
            binding.tvXpToNextLevel.setText(xpRemaining + " XP para o Nível " + (currentLvl + 1));

            binding.tvStreakCount.setText(user.getStreakDays() + " Dias");
            binding.tvBadgesCount.setText(user.getBadgesCount() + " Badges");

            if (user.getStreakDays() > 0) {
                binding.tvMascotQuote.setText(getString(R.string.mascot_quote_workout_done));
            } else {
                binding.tvMascotQuote.setText(getString(R.string.mascot_quote_keep_going));
            }
        }
    }

    private void loadRecentCheckIns() {
        repository.getCheckInHistory(new GymBuddyRepository.RepositoryCallback<List<CheckIn>>() {
            @Override
            public void onSuccess(List<CheckIn> list) {
                if (list != null && !list.isEmpty()) {
                    binding.tvEmptyCheckins.setVisibility(View.GONE);
                    binding.rvRecentCheckins.setVisibility(View.VISIBLE);
                    historyAdapter.setCheckIns(list.size() > 3 ? list.subList(0, 3) : list);
                } else {
                    binding.tvEmptyCheckins.setVisibility(View.VISIBLE);
                    binding.rvRecentCheckins.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String message) {
                binding.tvEmptyCheckins.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showManageGroupsDialog() {
        if (!isAdded()) return;
        List<Community> communities = GymBuddyApp.getInstance().getPreferences().getCommunities();
        List<String> options = new ArrayList<>();
        options.add("+ Criar Novo Grupo de Treino");
        for (Community c : communities) {
            options.add("👥 " + c.getName() + " (" + c.getMemberCount() + " membros)");
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Grupos & Turmas do Treino")
                .setItems(options.toArray(new String[0]), (dialog, which) -> {
                    if (which == 0) {
                        showCreateGroupDialog();
                    } else {
                        Community selected = communities.get(which - 1);
                        String[] actions = new String[]{"Abrir / Ver Grupo", "🗑 Excluir Grupo"};
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
        if (!isAdded()) return;
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
                desc = "Grupo para a hora do treino.";
            }

            repository.createCommunity(name, desc, new GymBuddyRepository.RepositoryCallback<Community>() {
                @Override
                public void onSuccess(Community comm) {
                    dialog.dismiss();
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Grupo '" + comm.getName() + "' criado com sucesso!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireContext(), CommunityDetailActivity.class);
                    intent.putExtra(CommunityDetailActivity.EXTRA_COMMUNITY, comm);
                    startActivity(intent);
                }

                @Override
                public void onError(String message) {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Erro ao criar grupo: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
