package com.example.mefitness.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mefitness.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        EditText emailPasswordReset = findViewById(R.id.email_password_reset);
        Button sendEmail = findViewById(R.id.button_passworrd_reset);
        sendEmail.setOnClickListener(v -> {
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailPasswordReset.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())   {
                            Toast.makeText(PasswordResetActivity.this, "E-mail enviado com sucesso", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                       else Toast.makeText(PasswordResetActivity.this, "Erro ao enviar e-mail", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}