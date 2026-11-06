package com.example.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.GymBuddyApp;
import com.example.R;
import com.example.databinding.ActivityLoginBinding;
import com.example.model.User;
import com.example.repository.GymBuddyRepository;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private GymBuddyRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = GymBuddyApp.getInstance().getRepository();

        binding.btnLogin.setOnClickListener(v -> attemptLogin());

        binding.btnGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void attemptLogin() {
        hideError();
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

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

        setLoading(true);

        repository.login(email, password, new GymBuddyRepository.RepositoryCallback<User>() {
            @Override
            public void onSuccess(User user) {
                setLoading(false);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                showError(message != null ? message : getString(R.string.error_invalid_credentials));
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
        binding.btnLogin.setEnabled(!isLoading);
        binding.btnGoToRegister.setEnabled(!isLoading);
    }
}
