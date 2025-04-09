package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private TextView signupRedirectText, forgotPassword;
    private Button loginButton;
    private SignInButton googleBtn;
    private FirebaseAuth auth;
    private GoogleSignInClient gClient;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signUpRedirectText);
        forgotPassword = findViewById(R.id.forgot_password);
        googleBtn = findViewById(R.id.googleSignInBtn);

        auth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> handleEmailPasswordLogin());
        signupRedirectText.setOnClickListener(v -> navigateToSignup());
        forgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
        setupGoogleSignIn();
        googleBtn.setOnClickListener(v -> initiateGoogleSignIn());
    }

    private void handleEmailPasswordLogin() {
        String email = loginEmail.getText().toString().trim();
        String pass = loginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            loginEmail.setError("Email is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.setError("Enter a valid email");
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            loginPassword.setError("Password is required");
            return;
        }

        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void navigateToSignup() {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
        EditText emailBox = dialogView.findViewById(R.id.emailBox);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnReset).setOnClickListener(v -> {
            String userEmail = emailBox.getText().toString().trim();
            if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                Toast.makeText(this, "Enter your registered email", Toast.LENGTH_SHORT).show();
                return;
            }
            auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gClient = GoogleSignIn.getClient(this, gOptions);
    }

    private void initiateGoogleSignIn() {
        Intent signInIntent = gClient.getSignInIntent();
        startActivityForResult(signInIntent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    auth.signInWithCredential(credential)
                            .addOnCompleteListener(this, task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(this, "Google Sign-In Success", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, HomeActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(this, "Firebase Auth Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}