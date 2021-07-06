package com.example.mefitness.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button btnLogin;
    private TextView tVSignUp, tVForgotPassword;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        btnLogin.setOnClickListener((v) -> login());
        tVSignUp.setOnClickListener(v -> startSignUpActivity());
        tVForgotPassword.setOnClickListener(v -> forgotPassword());
    }


    private void init() {
        context = this;
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.login_btnLogin);
        tVSignUp = findViewById(R.id.login_tVSignUp);
        tVForgotPassword = findViewById(R.id.login_tVForgotPassword);
    }

    private void login() {
        String userEmail, userPassword;
        userEmail = email.getText().toString();
        userPassword = password.getText().toString();
        if (userEmail.isEmpty()) {
            email.setError(getString(R.string.entry_email));
            email.requestFocus();
        } else if (userPassword.isEmpty()) {
            password.setError(getString(R.string.entry_passsword));
            password.requestFocus();
        } else {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        startTreinosActivity();
                    } else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void forgotPassword() {
        Toast.makeText(context, "Recurso não implementado", Toast.LENGTH_SHORT).show();
    }

    private void startSignUpActivity() {
        startActivity(new Intent(context, SignUpActivity.class));
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        finish();
    }

    private void startTreinosActivity() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Intent a = new Intent(context, TreinosActivity.class);
        a.putExtra("userID", auth.getCurrentUser().getUid().toString());
        startActivity(a);
        overridePendingTransition(R.anim.zoom_in, R.anim.static_animation);
        finish();
    }

}
