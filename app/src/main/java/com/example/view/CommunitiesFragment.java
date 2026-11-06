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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.GymBuddyApp;
import com.example.R;
import com.example.adapter.CommunityAdapter;
import com.example.adapter.InvitationAdapter;
import com.example.databinding.FragmentCommunitiesBinding;
import com.example.model.Community;
import com.example.model.Invitation;
import com.example.repository.GymBuddyRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class CommunitiesFragment extends Fragment {

    private FragmentCommunitiesBinding binding;
    private GymBuddyRepository repository;
    private CommunityAdapter communityAdapter;
    private InvitationAdapter invitationAdapter;
    private int currentTab = 0; // 0 = Explorar, 1 = Meus Grupos, 2 = Convites
    private List<Community> allCommunitiesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCommunitiesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = GymBuddyApp.getInstance().getRepository();

        communityAdapter = new CommunityAdapter(new CommunityAdapter.OnCommunityActionListener() {
            @Override
            public void onToggleJoin(Community community) {
                repository.toggleCommunityMembership(community.getId(), new GymBuddyRepository.RepositoryCallback<Community>() {
                    @Override
                    public void onSuccess(Community result) {
                        loadCommunities();
                        String msg = result.isMember() ? "Você entrou na comunidade!" : "Você saiu da comunidade.";
                        if (isAdded()) {
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String message) {}
                });
            }

            @Override
            public void onCommunityClick(Community community) {
                Intent intent = new Intent(requireContext(), CommunityDetailActivity.class);
                intent.putExtra(CommunityDetailActivity.EXTRA_COMMUNITY, community);
                startActivity(intent);
            }

            @Override
            public void onDeleteCommunity(Community community) {
                confirmDeleteCommunity(community);
            }
        });

        invitationAdapter = new InvitationAdapter(new InvitationAdapter.OnInvitationActionListener() {
            @Override
            public void onAccept(Invitation invitation) {
                repository.updateInvitationStatus(invitation.getId(), Invitation.STATUS_ACCEPTED, new GymBuddyRepository.RepositoryCallback<Invitation>() {
                    @Override
                    public void onSuccess(Invitation result) {
                        loadInvitations();
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Convite aceito! Você entrou na comunidade.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onError(String message) {}
                });
            }

            @Override
            public void onDecline(Invitation invitation) {
                repository.updateInvitationStatus(invitation.getId(), Invitation.STATUS_DECLINED, new GymBuddyRepository.RepositoryCallback<Invitation>() {
                    @Override
                    public void onSuccess(Invitation result) {
                        loadInvitations();
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Convite recusado.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onError(String message) {}
                });
            }

            @Override
            public void onCancel(Invitation invitation) {
                repository.updateInvitationStatus(invitation.getId(), Invitation.STATUS_CANCELLED, new GymBuddyRepository.RepositoryCallback<Invitation>() {
                    @Override
                    public void onSuccess(Invitation result) {
                        loadInvitations();
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Convite cancelado.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onError(String message) {}
                });
            }
        });

        binding.rvCommunitiesOrInvites.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCommunitiesOrInvites.setAdapter(communityAdapter);

        binding.tabLayoutCommunities.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                if (currentTab == 0 || currentTab == 1) {
                    binding.rvCommunitiesOrInvites.setAdapter(communityAdapter);
                    binding.btnNewCommunity.setVisibility(View.VISIBLE);
                    binding.fabCreateCommunity.show();
                    loadCommunities();
                } else {
                    binding.rvCommunitiesOrInvites.setAdapter(invitationAdapter);
                    binding.btnNewCommunity.setVisibility(View.GONE);
                    binding.fabCreateCommunity.hide();
                    loadInvitations();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.btnNewCommunity.setOnClickListener(v -> showCreateCommunityDialog());
        binding.fabCreateCommunity.setOnClickListener(v -> showCreateCommunityDialog());
        binding.btnInviteGeneral.setOnClickListener(v -> showGeneralInviteDialog());
        binding.btnDeleteOldCommunities.setOnClickListener(v -> confirmDeleteOldCommunities());

        loadCommunities();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentTab == 0 || currentTab == 1) {
            loadCommunities();
        } else {
            loadInvitations();
        }
    }

    private void loadCommunities() {
        binding.pbCommunitiesLoading.setVisibility(View.GONE);
        repository.getCommunities(new GymBuddyRepository.RepositoryCallback<List<Community>>() {
            @Override
            public void onSuccess(List<Community> communities) {
                allCommunitiesList = communities != null ? communities : new ArrayList<>();
                if (!isAdded()) return;

                if (currentTab == 0) {
                    communityAdapter.setCommunities(allCommunitiesList);
                    binding.tvEmptyCommunities.setVisibility(allCommunitiesList.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.tvEmptyCommunities.setText("Nenhuma comunidade criada ainda.\nClique em '+ Nova' para criar!");
                } else if (currentTab == 1) {
                    List<Community> myCommunities = new ArrayList<>();
                    for (Community c : allCommunitiesList) {
                        if (c.isMember()) {
                            myCommunities.add(c);
                        }
                    }
                    communityAdapter.setCommunities(myCommunities);
                    binding.tvEmptyCommunities.setVisibility(myCommunities.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.tvEmptyCommunities.setText("Você ainda não faz parte de nenhuma comunidade.\nExplore os grupos e clique em 'Entrar'!");
                }
            }

            @Override
            public void onError(String message) {
                if (isAdded()) {
                    binding.pbCommunitiesLoading.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadInvitations() {
        binding.pbCommunitiesLoading.setVisibility(View.GONE);
        repository.getInvitations(new GymBuddyRepository.RepositoryCallback<List<Invitation>>() {
            @Override
            public void onSuccess(List<Invitation> invitations) {
                if (!isAdded()) return;
                if (currentTab == 2) {
                    invitationAdapter.setInvitations(invitations);
                    binding.tvEmptyCommunities.setVisibility(invitations.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.tvEmptyCommunities.setText("Você não possui convites pendentes no momento.\nEnvie um convite para um amigo treinar com você!");
                }
            }

            @Override
            public void onError(String message) {
                if (isAdded()) {
                    binding.pbCommunitiesLoading.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showCreateCommunityDialog() {
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
                etName.setError("Informe o nome da comunidade");
                return;
            }

            repository.createCommunity(name, desc, new GymBuddyRepository.RepositoryCallback<Community>() {
                @Override
                public void onSuccess(Community result) {
                    dialog.dismiss();
                    loadCommunities();
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Comunidade criada com sucesso!", Toast.LENGTH_SHORT).show();
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

    private void showGeneralInviteDialog() {
        if (allCommunitiesList.isEmpty()) {
            Toast.makeText(requireContext(), "Nenhuma comunidade disponível para convidar.", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_send_invitation, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        EditText etEmail = dialogView.findViewById(R.id.et_invite_email);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_invite_cancel);
        MaterialButton btnSend = dialogView.findViewById(R.id.btn_invite_send);

        // Prepara a primeira comunidade como padrão
        final Community targetComm = allCommunitiesList.get(0);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSend.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            if (TextUtils.isEmpty(email) || !email.contains("@")) {
                etEmail.setError("Informe um e-mail válido");
                return;
            }

            repository.sendInvitation(targetComm.getId(), targetComm.getName(), email, new GymBuddyRepository.RepositoryCallback<Invitation>() {
                @Override
                public void onSuccess(Invitation result) {
                    dialog.dismiss();
                    if (currentTab == 2) {
                        loadInvitations();
                    }
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Convite enviado para " + email + "!", Toast.LENGTH_SHORT).show();
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

    private void confirmDeleteCommunity(Community community) {
        if (!isAdded() || community == null) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Excluir Comunidade / Grupo")
                .setMessage("Deseja realmente excluir a comunidade '" + community.getName() + "'?")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Excluir", (dialog, which) -> {
                    repository.deleteCommunity(community.getId(), new GymBuddyRepository.RepositoryCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "Comunidade excluída com sucesso!", Toast.LENGTH_SHORT).show();
                                loadCommunities();
                            }
                        }

                        @Override
                        public void onError(String message) {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmDeleteOldCommunities() {
        if (!isAdded()) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Excluir Comunidades Antigas")
                .setMessage("Deseja remover as comunidades antigas de exemplo para manter apenas os seus novos grupos e alunos?")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Sim, Excluir Antigas", (dialog, which) -> {
                    repository.deleteOldCommunities(new GymBuddyRepository.RepositoryCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "Comunidades antigas removidas com sucesso!", Toast.LENGTH_SHORT).show();
                                loadCommunities();
                            }
                        }

                        @Override
                        public void onError(String message) {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
