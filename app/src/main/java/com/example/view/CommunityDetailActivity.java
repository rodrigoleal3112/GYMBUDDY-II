package com.example.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.GymBuddyApp;
import com.example.R;
import com.example.adapter.CheckInHistoryAdapter;
import com.example.adapter.CommunityActivityAdapter;
import com.example.adapter.CommunityMemberAdapter;
import com.example.databinding.ActivityCommunityDetailBinding;
import com.example.model.CheckIn;
import com.example.model.Community;
import com.example.model.CommunityActivity;
import com.example.model.CommunityMember;
import com.example.model.Invitation;
import com.example.model.User;
import com.example.repository.GymBuddyRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class CommunityDetailActivity extends AppCompatActivity {

    public static final String EXTRA_COMMUNITY = "extra_community";

    private ActivityCommunityDetailBinding binding;
    private GymBuddyRepository repository;
    private Community community;

    private CommunityActivityAdapter activityAdapter;
    private CommunityMemberAdapter memberAdapter;
    private CheckInHistoryAdapter checkInAdapter;
    private int currentTab = 0; // 0 = Treinos/Atividades, 1 = Membros/Alunos, 2 = Check-ins
    private boolean isUserAdmin = false;
    private List<CommunityActivity> currentActivities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommunityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = GymBuddyApp.getInstance().getRepository();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            community = getIntent().getSerializableExtra(EXTRA_COMMUNITY, Community.class);
        } else {
            community = (Community) getIntent().getSerializableExtra(EXTRA_COMMUNITY);
        }
        if (community == null) {
            Toast.makeText(this, "Comunidade não encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        checkAdminStatus();
        setupToolbar();
        setupCommunityHeader();
        setupAdapters();
        setupTabs();

        binding.btnCreateCommActivity.setOnClickListener(v -> showCreateActivityDialog());
        binding.btnRegisterStudent.setOnClickListener(v -> showRegisterStudentDialog());
        binding.btnDetailInvite.setOnClickListener(v -> showRegisterStudentDialog());
        binding.btnDetailJoinToggle.setOnClickListener(v -> toggleMembership());
        binding.btnDetailDeleteCommunity.setOnClickListener(v -> confirmDeleteCommunity());

        loadCurrentTabData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCurrentTabData();
    }

    private void checkAdminStatus() {
        User user = repository.getCurrentUser();
        if (user != null && community != null) {
            if (user.getName() != null && user.getName().equalsIgnoreCase(community.getCreatorName())) {
                isUserAdmin = true;
            } else if ("Você".equalsIgnoreCase(community.getCreatorName())) {
                isUserAdmin = true;
            } else if (community.isMember()) {
                // Se foi o usuário que criou ou é membro administrador
                isUserAdmin = true;
            }
        }
    }

    private void setupToolbar() {
        binding.toolbarCommunity.setTitle(community.getName());
        binding.toolbarCommunity.setNavigationOnClickListener(v -> finish());
    }

    private void setupCommunityHeader() {
        binding.tvDetailName.setText(community.getName());
        binding.tvDetailDesc.setText(community.getDescription());
        binding.tvDetailCreator.setText("Criador / Admin: " + community.getCreatorName());
        binding.tvDetailMembersCount.setText("👥 " + community.getMemberCount() + " membros");
        binding.tvDetailStatus.setText(community.getStatus() != null ? community.getStatus() : "ATIVO");

        // Todo grupo possui o botão de excluir grupo (já que pode ser criado apenas para o treino)
        binding.btnDetailDeleteCommunity.setVisibility(View.VISIBLE);
        if (isUserAdmin) {
            binding.tvDetailAdminBadge.setVisibility(View.VISIBLE);
        } else {
            binding.tvDetailAdminBadge.setVisibility(View.GONE);
        }

        updateMembershipButton();
    }

    private void updateMembershipButton() {
        if (community.isMember()) {
            binding.btnDetailJoinToggle.setText("Membro ✓");
            binding.btnDetailJoinToggle.setBackgroundColor(ContextCompat.getColor(this, R.color.status_success_container));
            binding.btnDetailJoinToggle.setTextColor(ContextCompat.getColor(this, R.color.status_success));
        } else {
            binding.btnDetailJoinToggle.setText("Entrar no Grupo");
            binding.btnDetailJoinToggle.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_orange_container));
            binding.btnDetailJoinToggle.setTextColor(ContextCompat.getColor(this, R.color.primary_orange_dark));
        }
    }

    private void toggleMembership() {
        repository.toggleCommunityMembership(community.getId(), new GymBuddyRepository.RepositoryCallback<Community>() {
            @Override
            public void onSuccess(Community updated) {
                community = updated;
                setupCommunityHeader();
                String msg = updated.isMember() ? "Você entrou na comunidade!" : "Você saiu da comunidade.";
                Toast.makeText(CommunityDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                if (currentTab == 1) {
                    loadMembers();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(CommunityDetailActivity.this, "Erro: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeleteCommunity() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Comunidade / Grupo")
                .setMessage("Deseja realmente excluir a comunidade '" + community.getName() + "'? Todos os alunos cadastrados e treinos serão removidos.")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Sim, Excluir", (dialog, which) -> {
                    repository.deleteCommunity(community.getId(), new GymBuddyRepository.RepositoryCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(CommunityDetailActivity.this, "Comunidade excluída com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(CommunityDetailActivity.this, "Erro ao excluir: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void setupAdapters() {
        activityAdapter = new CommunityActivityAdapter(new CommunityActivityAdapter.OnCommunityActivityListener() {
            @Override
            public void onToggleParticipate(CommunityActivity activity) {
                repository.toggleCommunityActivityParticipation(community.getId(), activity.getId(), new GymBuddyRepository.RepositoryCallback<CommunityActivity>() {
                    @Override
                    public void onSuccess(CommunityActivity result) {
                        loadActivities();
                        String msg = result.isParticipating() ? "Você agora participa deste desafio!" : "Inscrição removida.";
                        Toast.makeText(CommunityDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String message) {}
                });
            }

            @Override
            public void onCheckInClick(CommunityActivity activity) {
                Intent intent = new Intent(CommunityDetailActivity.this, CheckInActivity.class);
                intent.putExtra(CheckInActivity.EXTRA_PRESELECTED_ACTIVITY_ID, activity.getId());
                startActivity(intent);
            }
        });

        memberAdapter = new CommunityMemberAdapter(isUserAdmin, this::showCheckInStudentDialog);
        checkInAdapter = new CheckInHistoryAdapter();

        binding.rvDetailContent.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDetailContent.setAdapter(activityAdapter);
    }

    private void setupTabs() {
        binding.tabLayoutDetail.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                if (currentTab == 0) {
                    binding.rvDetailContent.setAdapter(activityAdapter);
                    binding.layoutActivityActions.setVisibility(View.VISIBLE);
                    binding.layoutMembersActions.setVisibility(View.GONE);
                    binding.tvTabSectionTitle.setText("Treinos Prescritos do Grupo");
                    loadActivities();
                } else if (currentTab == 1) {
                    binding.rvDetailContent.setAdapter(memberAdapter);
                    binding.layoutActivityActions.setVisibility(View.GONE);
                    binding.layoutMembersActions.setVisibility(View.VISIBLE);
                    loadMembers();
                } else {
                    binding.rvDetailContent.setAdapter(checkInAdapter);
                    binding.layoutActivityActions.setVisibility(View.GONE);
                    binding.layoutMembersActions.setVisibility(View.GONE);
                    loadCheckIns();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadCurrentTabData() {
        if (currentTab == 0) {
            loadActivities();
        } else if (currentTab == 1) {
            loadMembers();
        } else {
            loadCheckIns();
        }
    }

    private void loadActivities() {
        binding.pbDetailLoading.setVisibility(View.VISIBLE);
        repository.getCommunityActivities(community.getId(), new GymBuddyRepository.RepositoryCallback<List<CommunityActivity>>() {
            @Override
            public void onSuccess(List<CommunityActivity> list) {
                binding.pbDetailLoading.setVisibility(View.GONE);
                currentActivities = list != null ? list : new ArrayList<>();
                if (currentTab == 0) {
                    activityAdapter.setActivities(currentActivities);
                    binding.tvDetailEmpty.setVisibility(currentActivities.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.tvDetailEmpty.setText("Nenhum treino prescrito ainda.\nClique em '+ Criar Treino' para cadastrar!");
                }
            }

            @Override
            public void onError(String message) {
                binding.pbDetailLoading.setVisibility(View.GONE);
            }
        });
    }

    private void loadMembers() {
        binding.pbDetailLoading.setVisibility(View.VISIBLE);
        repository.getCommunityMembers(community.getId(), new GymBuddyRepository.RepositoryCallback<List<CommunityMember>>() {
            @Override
            public void onSuccess(List<CommunityMember> list) {
                binding.pbDetailLoading.setVisibility(View.GONE);
                if (currentTab == 1) {
                    memberAdapter.setIsAdmin(isUserAdmin);
                    memberAdapter.setMembers(list);
                    binding.tvDetailEmpty.setVisibility((list == null || list.isEmpty()) ? View.VISIBLE : View.GONE);
                    binding.tvDetailEmpty.setText("Nenhum membro no grupo ainda.\nClique em '+ Acrescentar Membro' para adicionar!");
                }
            }

            @Override
            public void onError(String message) {
                binding.pbDetailLoading.setVisibility(View.GONE);
            }
        });
    }

    private void loadCheckIns() {
        binding.pbDetailLoading.setVisibility(View.VISIBLE);
        repository.getCommunityCheckIns(community.getId(), new GymBuddyRepository.RepositoryCallback<List<CheckIn>>() {
            @Override
            public void onSuccess(List<CheckIn> list) {
                binding.pbDetailLoading.setVisibility(View.GONE);
                if (currentTab == 2) {
                    checkInAdapter.setCheckIns(list);
                    binding.tvDetailEmpty.setVisibility((list == null || list.isEmpty()) ? View.VISIBLE : View.GONE);
                    binding.tvDetailEmpty.setText("Nenhum check-in registrado neste grupo ainda.\nFaça o check dos seus alunos para aparecer aqui!");
                }
            }

            @Override
            public void onError(String message) {
                binding.pbDetailLoading.setVisibility(View.GONE);
            }
        });
    }

    private void showCreateActivityDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_community_activity, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        EditText etTitle = dialogView.findViewById(R.id.et_comm_act_title);
        EditText etDesc = dialogView.findViewById(R.id.et_comm_act_desc);
        AutoCompleteTextView actvCategory = dialogView.findViewById(R.id.actv_comm_act_category);
        EditText etXp = dialogView.findViewById(R.id.et_comm_act_xp);
        EditText etSchedule = dialogView.findViewById(R.id.et_comm_act_schedule);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_comm_act_cancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.btn_comm_act_save);

        String[] categories = new String[]{"CARDIO", "BRAÇO", "PERNA", "COSTA", "FUNCIONAL", "OUTROS"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(catAdapter);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
            String desc = etDesc.getText() != null ? etDesc.getText().toString().trim() : "";
            String cat = actvCategory.getText() != null ? actvCategory.getText().toString().trim() : "CARDIO";
            String xpStr = etXp.getText() != null ? etXp.getText().toString().trim() : "50";
            String schedule = etSchedule.getText() != null ? etSchedule.getText().toString().trim() : "Diário";

            if (TextUtils.isEmpty(title)) {
                etTitle.setError("Informe o título do treino");
                return;
            }

            int xp = 50;
            try {
                xp = Integer.parseInt(xpStr);
            } catch (Exception ignored) {}

            repository.createCommunityActivity(community.getId(), title, desc, cat, xp, schedule, new GymBuddyRepository.RepositoryCallback<CommunityActivity>() {
                @Override
                public void onSuccess(CommunityActivity result) {
                    dialog.dismiss();
                    loadActivities();
                    Toast.makeText(CommunityDetailActivity.this, "Treino criado com sucesso!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(CommunityDetailActivity.this, "Erro: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void showRegisterStudentDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_register_student, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        EditText etName = dialogView.findViewById(R.id.et_student_name);
        AutoCompleteTextView actvRole = dialogView.findViewById(R.id.actv_student_role);
        AutoCompleteTextView actvLevel = dialogView.findViewById(R.id.actv_student_level);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_student_cancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.btn_student_save);

        String[] roles = new String[]{"Aluno", "Colega", "Monitor"};
        actvRole.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles));

        String[] levels = new String[]{"Iniciante (Nível 1)", "Praticante (Nível 2)", "Intermediário (Nível 3)", "Avançado (Nível 4)"};
        actvLevel.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, levels));

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String role = actvRole.getText() != null ? actvRole.getText().toString().trim() : "Aluno";
            String levelStr = actvLevel.getText() != null ? actvLevel.getText().toString() : "1";

            if (TextUtils.isEmpty(name)) {
                etName.setError("Informe o nome do integrante");
                return;
            }

            int level = 1;
            if (levelStr.contains("2")) level = 2;
            else if (levelStr.contains("3")) level = 3;
            else if (levelStr.contains("4")) level = 4;

            repository.registerStudent(community.getId(), name, "", role, level, new GymBuddyRepository.RepositoryCallback<CommunityMember>() {
                @Override
                public void onSuccess(CommunityMember member) {
                    dialog.dismiss();
                    community.setMemberCount(community.getMemberCount() + 1);
                    setupCommunityHeader();
                    loadMembers();
                    Toast.makeText(CommunityDetailActivity.this, "Membro " + name + " acrescentado com sucesso!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(CommunityDetailActivity.this, "Erro: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void showCheckInStudentDialog(CommunityMember student) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_checkin_student, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        TextView tvName = dialogView.findViewById(R.id.tv_target_student_name);
        TextView tvInfo = dialogView.findViewById(R.id.tv_target_student_info);
        AutoCompleteTextView actvWorkout = dialogView.findViewById(R.id.actv_workout_select);
        EditText etXp = dialogView.findViewById(R.id.et_xp_reward);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel_student_checkin);
        MaterialButton btnConfirm = dialogView.findViewById(R.id.btn_confirm_student_checkin);

        tvName.setText("Aluno: " + student.getUserName());
        tvInfo.setText("Papel: " + student.getUserRole() + " • Nível " + student.getLevel() + " • Sequência: " + student.getStreakDays() + " dias");

        List<String> workoutNames = new ArrayList<>();
        if (!currentActivities.isEmpty()) {
            for (CommunityActivity a : currentActivities) {
                workoutNames.add(a.getTitle() + " (+" + a.getXpReward() + " XP)");
            }
        } else {
            workoutNames.add("Treino Prescrito do Dia");
            workoutNames.add("Treino de Musculação & Força");
            workoutNames.add("Treino de Cardio & Resistência");
            workoutNames.add("Alongamento & Mobilidade");
        }

        ArrayAdapter<String> workoutAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, workoutNames);
        actvWorkout.setAdapter(workoutAdapter);
        actvWorkout.setText(workoutNames.get(0), false);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String selectedWorkout = actvWorkout.getText() != null ? actvWorkout.getText().toString().trim() : "Treino do Dia";
            String xpStr = etXp.getText() != null ? etXp.getText().toString().trim() : "50";

            int xp = 50;
            try {
                xp = Integer.parseInt(xpStr);
            } catch (Exception ignored) {}

            repository.checkInStudent(community.getId(), community.getName(), student, selectedWorkout, xp, new GymBuddyRepository.RepositoryCallback<CheckIn>() {
                @Override
                public void onSuccess(CheckIn checkIn) {
                    dialog.dismiss();
                    loadMembers();
                    Toast.makeText(CommunityDetailActivity.this, "Check-in do aluno " + student.getUserName() + " registrado com sucesso! (+" + checkIn.getXpEarned() + " XP)", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(CommunityDetailActivity.this, "Erro ao registrar check-in: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void showInviteFriendDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_send_invitation, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        EditText etEmail = dialogView.findViewById(R.id.et_invite_email);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_invite_cancel);
        MaterialButton btnSend = dialogView.findViewById(R.id.btn_invite_send);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSend.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            if (TextUtils.isEmpty(email) || !email.contains("@")) {
                etEmail.setError("Informe um e-mail válido");
                return;
            }

            repository.sendInvitation(community.getId(), community.getName(), email, new GymBuddyRepository.RepositoryCallback<Invitation>() {
                @Override
                public void onSuccess(Invitation result) {
                    dialog.dismiss();
                    Toast.makeText(CommunityDetailActivity.this, "Convite enviado para " + email + "!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(CommunityDetailActivity.this, "Erro: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
