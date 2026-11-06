package com.example.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.GymBuddyApp;
import com.example.R;
import com.example.adapter.RankingAdapter;
import com.example.databinding.FragmentRankingBinding;
import com.example.model.RankingUser;
import com.example.repository.GymBuddyRepository;

import java.util.List;

public class RankingFragment extends Fragment {

    private FragmentRankingBinding binding;
    private GymBuddyRepository repository;
    private RankingAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRankingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = GymBuddyApp.getInstance().getRepository();

        adapter = new RankingAdapter();
        binding.rvRanking.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRanking.setAdapter(adapter);

        binding.btnResetRanking.setOnClickListener(v -> showConfirmResetRankingDialog());

        loadRanking();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRanking();
    }

    private void showConfirmResetRankingDialog() {
        if (!isAdded()) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Limpar e Zerar Ranking")
                .setMessage("Deseja realmente zerar as atividades e o ranking da temporada?\n\nIsso zerará a pontuação de XP de todos os competidores, reiniciando o placar para deixar a disputa 100% competitiva do zero!")
                .setIcon(R.drawable.ic_refresh)
                .setPositiveButton("Sim, Zerar Ranking", (dialog, which) -> {
                    binding.pbRankingLoading.setVisibility(View.VISIBLE);
                    repository.resetRanking(new GymBuddyRepository.RepositoryCallback<List<RankingUser>>() {
                        @Override
                        public void onSuccess(List<RankingUser> resetList) {
                            if (!isAdded()) return;
                            binding.pbRankingLoading.setVisibility(View.GONE);
                            binding.tvRankingBanner.setText("🏆 Ranking Zerado! Temporada iniciada do zero.");
                            updatePodiumAndList(resetList);
                            Toast.makeText(requireContext(), "Ranking zerado com sucesso! A nova temporada competitiva começou!", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(String message) {
                            if (!isAdded()) return;
                            binding.pbRankingLoading.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Erro ao zerar: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void updatePodiumAndList(List<RankingUser> list) {
        if (list != null && !list.isEmpty()) {
            // Atualiza Pódio Top 3
            if (list.size() > 0) {
                RankingUser first = list.get(0);
                binding.tvPodiumFirstName.setText(first.getName());
                binding.tvPodiumFirstXp.setText(first.getXp() + " XP");
            }
            if (list.size() > 1) {
                RankingUser second = list.get(1);
                binding.tvPodiumSecondName.setText(second.getName());
                binding.tvPodiumSecondXp.setText(second.getXp() + " XP");
            }
            if (list.size() > 2) {
                RankingUser third = list.get(2);
                binding.tvPodiumThirdName.setText(third.getName());
                binding.tvPodiumThirdXp.setText(third.getXp() + " XP");
            }

            adapter.setUsers(list);
        }
    }

    private void loadRanking() {
        binding.pbRankingLoading.setVisibility(View.VISIBLE);
        repository.getRanking(new GymBuddyRepository.RepositoryCallback<List<RankingUser>>() {
            @Override
            public void onSuccess(List<RankingUser> list) {
                binding.pbRankingLoading.setVisibility(View.GONE);
                updatePodiumAndList(list);
            }

            @Override
            public void onError(String message) {
                binding.pbRankingLoading.setVisibility(View.GONE);
            }
        });
    }
}
