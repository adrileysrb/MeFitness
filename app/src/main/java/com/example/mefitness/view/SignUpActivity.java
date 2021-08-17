package com.example.mefitness.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mefitness.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText name, confirmPassword, email, password;
    private Button btnSignUp;
    private TextView tViewHaveAccount;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
        btnSignUp.setOnClickListener((v) -> createUser());
        tViewHaveAccount.setOnClickListener(v -> startLoginActivity());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startLoginActivity();
    }

    private void init() {
        context = this;
        name = findViewById(R.id.signup_name);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.signup_confirmPassword);
        btnSignUp = findViewById(R.id.signup_btnSignUp);
        tViewHaveAccount = findViewById(R.id.signup_tViewHaveAccount);
    }

    private void createUser() {
        String userName, userEmail, userPassword, userConfirmPassword;
        userName = name.getText().toString();
        userEmail = email.getText().toString();
        userPassword = password.getText().toString();
        userConfirmPassword = confirmPassword.getText().toString();
        String x = ""+ confirmPassword.getText().toString();
        String y = ""+ password.getText().toString();

        if (userName.isEmpty()) {
            name.setError(getString(R.string.entry_name));
            name.requestFocus();
        } else if (userEmail.isEmpty()) {
            email.setError(getString(R.string.entry_email));
            email.requestFocus();
        } else if (userPassword.isEmpty()) {
            password.setError(getString(R.string.entry_passsword));
            password.requestFocus();
        } else if (userConfirmPassword.isEmpty()) {
            confirmPassword.setError(getString(R.string.entry_confirm_password));
            confirmPassword.requestFocus();
        }
        else if (x==y) {
            confirmPassword.setError("Senhas não conferem");
            confirmPassword.requestFocus();
        }
        else {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        startTreinosActivity(userName, userEmail);
                    } else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void startTreinosActivity(String userName, String userEmail) {
        Intent intent = new Intent(context, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("name", userName);
        bundle.putSerializable("email", userEmail);
        bundle.putInt("from", 2);
        intent.putExtras(bundle);
        startActivity(intent);
        Toast.makeText(context, "Conta criada com sucesso", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "Bem Vindo " + userName, Toast.LENGTH_LONG).show();

        finish();
    }

    private void startLoginActivity() {
        startActivity(new Intent(context, LoginActivity.class));
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        finish();
    }

}

