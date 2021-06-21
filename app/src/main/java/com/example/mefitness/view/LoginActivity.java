package com.example.mefitness.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    EditText email, pass;
    Button btnLogin;
    TextView btnSignUp, textViewEsqueceuSenha;
    //TextView signUpTV;
    FirebaseAuth auth;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        email = findViewById(R.id.email2_login);
        pass = findViewById(R.id.pass2_login);
        btnLogin = findViewById(R.id.btnLogin_login);
        btnSignUp = findViewById(R.id.textViewSignUp);
        textViewEsqueceuSenha = findViewById(R.id.textViewEsqueceuSenha);

        auth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener( (v) -> {

            String emailID, password;

            emailID = email.getText().toString();
            password = pass.getText().toString();

            if(emailID.isEmpty()){
                email.setError("Please enter your Email");
                email.requestFocus();
            }
            else if(password.isEmpty()){
                pass.setError("Please enter your Password");
                pass.requestFocus();
            }
            else{
                auth.signInWithEmailAndPassword(emailID, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent a = new Intent(context, TreinosActivity.class);
                            a.putExtra("userID", auth.getCurrentUser().getUid()+"");
                            startActivity(a);
                            overridePendingTransition(R.anim.zoom_in, R.anim.static_animation);
                            //Toast.makeText(context, ""+auth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SignUpActivity.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                //finish();
            }
        });
        textViewEsqueceuSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Recurso não implementado", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
