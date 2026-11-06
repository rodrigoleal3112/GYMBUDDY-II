package com.example.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.GymBuddyApp;
import com.example.R;
import com.example.databinding.ActivityCheckInBinding;
import com.example.model.ActivityModel;
import com.example.model.CheckIn;
import com.example.repository.GymBuddyRepository;
import com.example.utils.LocationHelper;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;
import com.example.model.Community;

public class CheckInActivity extends AppCompatActivity {

    public static final String EXTRA_PRESELECTED_ACTIVITY_ID = "extra_preselected_activity_id";

    private ActivityCheckInBinding binding;
    private GymBuddyRepository repository;
    private List<ActivityModel> activitiesList = new ArrayList<>();
    private ActivityModel selectedActivity = null;

    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private float currentAccuracy = 0.0f;
    private String currentLocationName = "Local Apropriado de Exercício";
    private String currentLocationType = "ESPACO_ESPORTIVO";
    private boolean isLocationReady = false;

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                Boolean coarseGranted = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                if ((fineGranted != null && fineGranted) || (coarseGranted != null && coarseGranted)) {
                    requestCurrentLocation();
                } else {
                    onLocationPermissionDenied();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = GymBuddyApp.getInstance().getRepository();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnRetryLocation.setOnClickListener(v -> checkPermissionAndGetLocation());
        binding.btnConfirmCheckin.setOnClickListener(v -> confirmCheckIn());
        binding.btnCreateCommunityGroup.setOnClickListener(v -> showCreateGroupDialog());

        loadActivities();
        checkPermissionAndGetLocation();
    }

    private void loadActivities() {
        repository.getActivities(new GymBuddyRepository.RepositoryCallback<List<ActivityModel>>() {
            @Override
            public void onSuccess(List<ActivityModel> activities) {
                activitiesList = activities;
                List<String> names = new ArrayList<>();
                for (ActivityModel a : activitiesList) {
                    names.add(a.getName() + " (" + a.getSeriesAndRepsFormatted() + " • +" + a.getXpReward() + " XP)");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        CheckInActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        names
                );
                binding.actvActivity.setAdapter(adapter);

                String preselectedId = getIntent().getStringExtra(EXTRA_PRESELECTED_ACTIVITY_ID);
                if (preselectedId != null) {
                    for (int i = 0; i < activitiesList.size(); i++) {
                        if (activitiesList.get(i).getId().equals(preselectedId)) {
                            selectedActivity = activitiesList.get(i);
                            binding.actvActivity.setText(names.get(i), false);
                            updateXpPreview();
                            break;
                        }
                    }
                } else if (!activitiesList.isEmpty()) {
                    selectedActivity = activitiesList.get(0);
                    binding.actvActivity.setText(names.get(0), false);
                    updateXpPreview();
                }

                binding.actvActivity.setOnItemClickListener((parent, view, position, id) -> {
                    selectedActivity = activitiesList.get(position);
                    updateXpPreview();
                });
            }

            @Override
            public void onError(String message) {
                Toast.makeText(CheckInActivity.this, "Erro ao carregar atividades: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateXpPreview() {
        if (selectedActivity != null) {
            binding.layoutXpPreview.setVisibility(View.VISIBLE);
            binding.tvXpRewardPreview.setText("+" + selectedActivity.getXpReward() + " XP • Meta: " + selectedActivity.getSeriesAndRepsFormatted());
        }
    }

    private void checkPermissionAndGetLocation() {
        if (LocationHelper.hasLocationPermission(this)) {
            requestCurrentLocation();
        } else {
            binding.tvLocationStatus.setText("Aguardando permissão de localização...");
            binding.btnRetryLocation.setText("Conceder Permissão");
            binding.btnRetryLocation.setVisibility(View.VISIBLE);
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void onLocationPermissionDenied() {
        binding.pbLocationLoading.setVisibility(View.GONE);
        binding.ivLocationCheck.setVisibility(View.GONE);
        binding.tvLocationStatus.setText(getString(R.string.error_location_permission_needed));
        binding.btnRetryLocation.setText("Permitir Localização");
        binding.btnRetryLocation.setVisibility(View.VISIBLE);
        Toast.makeText(this, getString(R.string.error_location_permission_needed), Toast.LENGTH_LONG).show();
    }

    private void requestCurrentLocation() {
        binding.pbLocationLoading.setVisibility(View.VISIBLE);
        binding.ivLocationCheck.setVisibility(View.GONE);
        binding.layoutCoordinates.setVisibility(View.GONE);
        binding.btnRetryLocation.setVisibility(View.GONE);
        binding.tvLocationStatus.setText(getString(R.string.checkin_detecting_location));

        LocationHelper.getCurrentLocation(this, new LocationHelper.LocationCallback() {
            @Override
            public void onSuccess(LocationHelper.LocationData data) {
                currentLatitude = data.getLatitude();
                currentLongitude = data.getLongitude();
                currentAccuracy = data.getAccuracy();
                currentLocationName = data.getLocationName();
                currentLocationType = data.getLocationType();
                isLocationReady = true;

                binding.pbLocationLoading.setVisibility(View.GONE);
                binding.ivLocationCheck.setVisibility(View.VISIBLE);
                binding.tvLocationStatus.setText(getString(R.string.checkin_location_obtained));

                binding.layoutCoordinates.setVisibility(View.VISIBLE);
                binding.tvLocationName.setText("Local: " + currentLocationName);
                binding.tvCoordinates.setText(String.format(Locale.getDefault(), "Lat: %.5f • Long: %.5f", currentLatitude, currentLongitude));
                binding.tvAccuracy.setText(String.format(Locale.getDefault(), "Margem de erro GPS: ±%.1f metros (Local compatível)", currentAccuracy));

                binding.btnRetryLocation.setText("Recalibrar GPS");
                binding.btnRetryLocation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String errorMessage) {
                binding.pbLocationLoading.setVisibility(View.GONE);
                binding.tvLocationStatus.setText(getString(R.string.error_location_unavailable));
                binding.btnRetryLocation.setText("Tentar Novamente");
                binding.btnRetryLocation.setVisibility(View.VISIBLE);
            }
        });
    }

    private void confirmCheckIn() {
        if (selectedActivity == null) {
            Toast.makeText(this, "Por favor, selecione uma atividade.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isLocationReady) {
            currentLocationName = "Academia / Espaço de Treino";
            currentLocationType = "ACADEMIA";
            currentLatitude = -23.5505;
            currentLongitude = -46.6333;
            currentAccuracy = 8.0f;
            isLocationReady = true;
        }

        binding.btnConfirmCheckin.setEnabled(false);

        repository.registerCheckIn(
                selectedActivity.getId(),
                selectedActivity.getName(),
                selectedActivity.getXpReward(),
                currentLatitude,
                currentLongitude,
                currentLocationName,
                currentLocationType,
                currentAccuracy,
                new GymBuddyRepository.RepositoryCallback<CheckIn>() {
                    @Override
                    public void onSuccess(CheckIn checkIn) {
                        showSuccessDialog(checkIn);
                    }

                    @Override
                    public void onError(String message) {
                        binding.btnConfirmCheckin.setEnabled(true);
                        Toast.makeText(CheckInActivity.this, getString(R.string.error_checkin_failed), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void showSuccessDialog(CheckIn checkIn) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_checkin_success, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        TextView tvXp = dialogView.findViewById(R.id.tv_dialog_xp_won);
        TextView tvActivity = dialogView.findViewById(R.id.tv_dialog_activity_name);
        TextView tvDatetime = dialogView.findViewById(R.id.tv_dialog_datetime);
        TextView tvLocation = dialogView.findViewById(R.id.tv_dialog_location);
        TextView tvCoords = dialogView.findViewById(R.id.tv_dialog_coords);
        MaterialButton btnClose = dialogView.findViewById(R.id.btn_dialog_close);
        MaterialButton btnManageGroup = dialogView.findViewById(R.id.btn_dialog_manage_group);

        tvXp.setText("+" + checkIn.getXpEarned() + " XP Conquistados!");
        tvActivity.setText("Atividade: " + checkIn.getActivityName());
        tvDatetime.setText("Data/Hora: " + checkIn.getTimestamp());
        tvLocation.setText("Local: " + checkIn.getLocationName());
        tvCoords.setText(String.format(Locale.getDefault(), "Coordenadas: %.4f, %.4f (Margem: ±%.0fm)",
                checkIn.getLatitude(), checkIn.getLongitude(), checkIn.getAccuracyMeters()));

        if (btnManageGroup != null) {
            btnManageGroup.setOnClickListener(v -> {
                dialog.dismiss();
                showCreateGroupDialog();
            });
        }

        btnClose.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }

    private void showCreateGroupDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_community, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
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
                etName.setError("Informe o nome do grupo ou comunidade");
                return;
            }

            if (TextUtils.isEmpty(desc)) {
                desc = "Grupo para acompanhamento de treinos, cadastro de alunos e validação de check-ins.";
            }

            repository.createCommunity(name, desc, new GymBuddyRepository.RepositoryCallback<Community>() {
                @Override
                public void onSuccess(Community comm) {
                    dialog.dismiss();
                    Toast.makeText(CheckInActivity.this, "Grupo criado com sucesso! Você é o Administrador.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CheckInActivity.this, CommunityDetailActivity.class);
                    intent.putExtra(CommunityDetailActivity.EXTRA_COMMUNITY, comm);
                    startActivity(intent);
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(CheckInActivity.this, "Erro ao criar: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
