package com.example.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.GymBuddyApp;
import com.example.R;
import com.example.adapter.ActivityAdapter;
import com.example.databinding.FragmentActivitiesBinding;
import com.example.model.ActivityModel;
import com.example.model.CheckIn;
import com.example.model.User;
import com.example.repository.GymBuddyRepository;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActivitiesFragment extends Fragment {

    private FragmentActivitiesBinding binding;
    private GymBuddyRepository repository;
    private ActivityAdapter adapter;
    private List<ActivityModel> allActivities = new ArrayList<>();
    private String selectedCategory = "TODOS";

    private Set<String> currentCompletedIds = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentActivitiesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = GymBuddyApp.getInstance().getRepository();

        // Configura adaptador com seleção e exclusão de cada treino individual
        adapter = new ActivityAdapter(new ActivityAdapter.OnActivityClickListener() {
            @Override
            public void onActivitySelected(ActivityModel activity) {
                showConfirmActivityDialog(activity);
            }

            @Override
            public void onActivityDelete(ActivityModel activity) {
                showConfirmDeleteActivityDialog(activity);
            }
        });

        binding.rvActivities.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvActivities.setAdapter(adapter);

        binding.btnNewActivity.setOnClickListener(v -> showCreateActivityDialog());
        binding.btnClearCompleted.setOnClickListener(v -> showConfirmClearCompletedDialog());

        setupChips();
        loadActivities();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadActivities();
    }

    /**
     * Exibe o diálogo de confirmação da atividade selecionada (ex: supino).
     * Possui o botão/ícone de OK para confirmar. Quando confirmada, encerra e gera XP para o praticante.
     */
    private void showConfirmActivityDialog(ActivityModel activity) {
        if (!isAdded()) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_activity, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        TextView tvCategory = dialogView.findViewById(R.id.tv_confirm_act_category);
        TextView tvXp = dialogView.findViewById(R.id.tv_confirm_act_xp);
        TextView tvName = dialogView.findViewById(R.id.tv_confirm_act_name);
        TextView tvSeriesReps = dialogView.findViewById(R.id.tv_confirm_act_series_reps);
        TextView tvDesc = dialogView.findViewById(R.id.tv_confirm_act_desc);
        MaterialButton btnOk = dialogView.findViewById(R.id.btn_confirm_act_ok);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_confirm_act_cancel);

        tvCategory.setText(activity.getCategory());
        tvXp.setText("+" + activity.getXpReward() + " XP");
        tvName.setText(activity.getName());
        tvSeriesReps.setText("💪 " + activity.getSeriesAndRepsFormatted());
        tvDesc.setText(activity.getDescription());
        btnOk.setText("Dar OK na Atividade (+" + activity.getXpReward() + " XP)");

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnOk.setOnClickListener(v -> {
            btnOk.setEnabled(false);
            btnOk.setText("Confirmando...");

            // Registra a atividade concluída, encerra e gera XP
            repository.registerCheckIn(
                    activity.getId(),
                    activity.getName(),
                    activity.getXpReward(),
                    -23.5505,
                    -46.6333,
                    "Academia / Treino Concluído",
                    "ACADEMIA",
                    5.0f,
                    new GymBuddyRepository.RepositoryCallback<CheckIn>() {
                        @Override
                        public void onSuccess(CheckIn checkIn) {
                            if (!isAdded()) return;
                            // Encerra o diálogo da atividade
                            dialog.dismiss();

                            // Recarrega lista atualizada com status
                            loadActivities();

                            // Exibe celebração do XP gerado para o praticante
                            showSuccessActivityDialog(activity, checkIn);
                        }

                        @Override
                        public void onError(String message) {
                            if (!isAdded()) return;
                            dialog.dismiss();
                            Toast.makeText(requireContext(), "Erro ao confirmar atividade: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });

        MaterialButton btnDelete = dialogView.findViewById(R.id.btn_confirm_act_delete);
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                dialog.dismiss();
                showConfirmDeleteActivityDialog(activity);
            });
        }

        dialog.show();
    }

    /**
     * Diálogo para excluir um treino específico individualmente.
     */
    private void showConfirmDeleteActivityDialog(ActivityModel activity) {
        if (!isAdded() || activity == null) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Excluir Treino")
                .setMessage("Deseja realmente excluir o exercício '" + activity.getName() + "' da sua lista de treinos?")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Sim, Excluir", (dialog, which) -> {
                    repository.deleteActivity(activity.getId(), new GymBuddyRepository.RepositoryCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            if (!isAdded()) return;
                            Toast.makeText(requireContext(), "Treino excluído com sucesso!", Toast.LENGTH_SHORT).show();
                            loadActivities();
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

    /**
     * Diálogo para limpar todas as atividades já realizadas/feitas.
     */
    private void showConfirmClearCompletedDialog() {
        if (!isAdded()) return;
        if (currentCompletedIds.isEmpty()) {
            Toast.makeText(requireContext(), "Você ainda não possui treinos concluídos para limpar.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Limpar Treinos Concluídos")
                .setMessage("Deseja limpar da lista todos os treinos que já foram realizados e não serão utilizados?")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Sim, Limpar Feitos", (dialog, which) -> {
                    repository.deleteCompletedActivities(new ArrayList<>(currentCompletedIds), new GymBuddyRepository.RepositoryCallback<Integer>() {
                        @Override
                        public void onSuccess(Integer count) {
                            if (!isAdded()) return;
                            Toast.makeText(requireContext(), count + " treinos concluídos foram removidos da lista!", Toast.LENGTH_SHORT).show();
                            loadActivities();
                        }

                        @Override
                        public void onError(String message) {
                            if (!isAdded()) return;
                            Toast.makeText(requireContext(), "Erro ao limpar: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Diálogo de sucesso exibido assim que a atividade é confirmada com OK.
     */
    private void showSuccessActivityDialog(ActivityModel activity, CheckIn checkIn) {
        if (!isAdded()) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_activity_completed_success, null);
        AlertDialog successDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        TextView tvActName = dialogView.findViewById(R.id.tv_success_act_name);
        TextView tvActXp = dialogView.findViewById(R.id.tv_success_act_xp);
        TextView tvUserXp = dialogView.findViewById(R.id.tv_success_user_xp);
        MaterialButton btnClose = dialogView.findViewById(R.id.btn_success_act_close);

        tvActName.setText(activity.getName());
        tvActXp.setText("+" + checkIn.getXpEarned() + " XP Gerados!");

        User user = repository.getCurrentUser();
        if (user != null) {
            tvUserXp.setText("Saldo atualizado: " + user.getXp() + " XP • Nível " + user.getLevel());
        } else {
            tvUserXp.setText("Parabéns pelo treino concluído!");
        }

        btnClose.setOnClickListener(v -> successDialog.dismiss());

        successDialog.show();
    }

    private void showCreateActivityDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_activity, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        EditText etName = dialogView.findViewById(R.id.et_act_name);
        AutoCompleteTextView actvCategory = dialogView.findViewById(R.id.actv_act_category);
        EditText etSets = dialogView.findViewById(R.id.et_act_sets);
        EditText etReps = dialogView.findViewById(R.id.et_act_reps);
        EditText etWeight = dialogView.findViewById(R.id.et_act_weight);
        EditText etXp = dialogView.findViewById(R.id.et_act_xp);
        EditText etDesc = dialogView.findViewById(R.id.et_act_desc);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_act_cancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.btn_act_save);

        String[] categories = new String[]{"BRAÇO", "PERNA", "COSTA", "CARDIO", "OUTROS"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(catAdapter);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String category = actvCategory.getText() != null ? actvCategory.getText().toString().trim() : "BRAÇO";
            String setsStr = etSets.getText() != null ? etSets.getText().toString().trim() : "3";
            String repsStr = etReps.getText() != null ? etReps.getText().toString().trim() : "10";
            String weight = etWeight.getText() != null ? etWeight.getText().toString().trim() : "";
            String xpStr = etXp.getText() != null ? etXp.getText().toString().trim() : "50";
            String desc = etDesc.getText() != null ? etDesc.getText().toString().trim() : "";

            if (TextUtils.isEmpty(name)) {
                etName.setError("Informe o nome do exercício");
                return;
            }

            int sets = 3;
            int reps = 10;
            int xp = 50;

            try {
                sets = Integer.parseInt(setsStr);
            } catch (Exception ignored) {}
            try {
                reps = Integer.parseInt(repsStr);
            } catch (Exception ignored) {}
            try {
                xp = Integer.parseInt(xpStr);
            } catch (Exception ignored) {}

            final int finalSets = sets;
            final int finalReps = reps;
            final int finalXp = xp;

            repository.createActivity(name, desc, category, finalSets, finalReps, weight, finalXp, new GymBuddyRepository.RepositoryCallback<ActivityModel>() {
                @Override
                public void onSuccess(ActivityModel result) {
                    dialog.dismiss();
                    loadActivities();
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Exercício criado: " + result.getName() + " (" + result.getSeriesAndRepsFormatted() + ")!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String message) {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }

    private void setupChips() {
        binding.chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                selectedCategory = "TODOS";
            } else {
                int id = checkedIds.get(0);
                if (id == R.id.chip_cardio) selectedCategory = "CARDIO";
                else if (id == R.id.chip_braco) selectedCategory = "BRAÇO";
                else if (id == R.id.chip_perna) selectedCategory = "PERNA";
                else if (id == R.id.chip_costa) selectedCategory = "COSTA";
                else if (id == R.id.chip_outros) selectedCategory = "OUTROS";
                else selectedCategory = "TODOS";
            }
            filterActivities();
        });
    }

    private void loadActivities() {
        binding.pbActivitiesLoading.setVisibility(View.VISIBLE);
        repository.getActivities(new GymBuddyRepository.RepositoryCallback<List<ActivityModel>>() {
            @Override
            public void onSuccess(List<ActivityModel> activities) {
                binding.pbActivitiesLoading.setVisibility(View.GONE);
                allActivities = activities;

                // Carrega histórico para marcar quais já foram feitas
                repository.getCheckInHistory(new GymBuddyRepository.RepositoryCallback<List<CheckIn>>() {
                    @Override
                    public void onSuccess(List<CheckIn> checkIns) {
                        Set<String> completed = new HashSet<>();
                        if (checkIns != null) {
                            for (CheckIn c : checkIns) {
                                if (c.getActivityId() != null) completed.add(c.getActivityId());
                                if (c.getActivityName() != null) completed.add(c.getActivityName());
                            }
                        }
                        currentCompletedIds = completed;
                        adapter.setCompletedActivityIds(completed);
                        filterActivities();
                    }

                    @Override
                    public void onError(String message) {
                        filterActivities();
                    }
                });
            }

            @Override
            public void onError(String message) {
                binding.pbActivitiesLoading.setVisibility(View.GONE);
            }
        });
    }

    private void filterActivities() {
        if ("TODOS".equalsIgnoreCase(selectedCategory)) {
            adapter.setActivities(allActivities);
        } else {
            List<ActivityModel> filtered = new ArrayList<>();
            for (ActivityModel act : allActivities) {
                if (selectedCategory.equalsIgnoreCase(act.getCategory())) {
                    filtered.add(act);
                }
            }
            adapter.setActivities(filtered);
        }
    }
}
