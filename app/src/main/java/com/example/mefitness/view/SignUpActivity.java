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

public class SignUpActivity extends AppCompatActivity {

    EditText name, pass2 , email, pass;
    Button btnSignUp;
    TextView tViewSignUp;
    //TextView signUpTV;
    FirebaseAuth auth;
    Context context;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        context = this;
        name = findViewById(R.id.name_signup);
        pass = findViewById(R.id.pass_signup);
        email = findViewById(R.id.email_signup);
        pass2 = findViewById(R.id.pass_signup2);
        btnSignUp = findViewById(R.id.btnSignUp_signup);
        tViewSignUp = findViewById(R.id.tViewSignUp);

        auth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener( (v) -> {

                String emailID, userEmail, userPass1, userPass2;

                userName = name.getText().toString();
                userEmail= email.getText().toString();
                userPass1 = pass.getText()+"";
                userPass2 = pass2.getText()+"";

                if(userName.isEmpty()){
                    name.setError("Please enter your Name");
                    name.requestFocus();
                }
                else if(userEmail.isEmpty()){
                    email.setError("Please enter your Email");
                    email.requestFocus();
                }
                else if(userPass1.isEmpty()){
                    pass.setError("Please enter your Password");
                    pass.requestFocus();
                }
                else if(userPass2.isEmpty()){
                    pass2.setError("Please enter your Password");
                    pass2.requestFocus();
                }
                else{
                    auth.createUserWithEmailAndPassword(userEmail, userPass1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(context, TreinosActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("userName", userName);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                Toast.makeText(context, "Conta criada com sucesso", Toast.LENGTH_SHORT).show();
                                Toast.makeText(context, "Bem Vindo "+userName, Toast.LENGTH_LONG).show();

                                finish();
                            }
                            else{
                                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
        });
        tViewSignUp.setOnClickListener(v -> {
            startActivity(new Intent(context, LoginActivity.class));
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            finish();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
       /* if(auth.getCurrentUser() != null){
            startActivity(new Intent(context, TreinosActivity.class));
            finish();
        }*/
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}

