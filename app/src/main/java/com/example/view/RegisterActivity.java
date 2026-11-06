package com.example.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.GymBuddyApp;
import com.example.R;
import com.example.databinding.ActivityRegisterBinding;
import com.example.model.User;
import com.example.repository.GymBuddyRepository;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private GymBuddyRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = GymBuddyApp.getInstance().getRepository();

        binding.btnRegister.setOnClickListener(v -> attemptRegister());

        binding.btnGoToLogin.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        hideError();
        String name = binding.etName.getText() != null ? binding.etName.getText().toString().trim() : "";
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";
        String confirm = binding.etConfirmPassword.getText() != null ? binding.etConfirmPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            showError(getString(R.string.error_name_required));
            binding.etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(getString(R.string.error_email_invalid));
            binding.etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            showError(getString(R.string.error_password_min_length));
            binding.etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirm)) {
            showError(getString(R.string.error_password_mismatch));
            binding.etConfirmPassword.requestFocus();
            return;
        }

        setLoading(true);

        repository.register(name, email, password, new GymBuddyRepository.RepositoryCallback<User>() {
            @Override
            public void onSuccess(User user) {
                setLoading(false);
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                showError(message != null ? message : getString(R.string.error_generic));
            }
        });
    }

    private void showError(String message) {
        binding.cardError.setVisibility(View.VISIBLE);
        binding.tvErrorMessage.setText(message);
    }

    private void hideError() {
        binding.cardError.setVisibility(View.GONE);
    }

    private void setLoading(boolean isLoading) {
        binding.pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnRegister.setEnabled(!isLoading);
        binding.btnGoToLogin.setEnabled(!isLoading);
    }
}
