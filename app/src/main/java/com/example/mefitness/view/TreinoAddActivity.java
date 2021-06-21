package com.example.mefitness.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mefitness.viewmodel.FirestoreHelper;
import com.example.mefitness.R;
import com.example.mefitness.model.Treino;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class TreinoAddActivity extends AppCompatActivity {
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_treino);
        context = this;
        FirestoreHelper firestoreHelper = new FirestoreHelper(FirebaseFirestore.getInstance());

        EditText editText1 = findViewById(R.id.editTextA01);
        EditText editText2 = findViewById(R.id.editTextA02);

        FirebaseAuth a = FirebaseAuth.getInstance();

        Button button = findViewById(R.id.buttonA);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String aa = editText1.getText().toString();
                //String b = exercicioEditImage.getText().toString();
                String c = editText2.getText().toString();
                if(aa.isEmpty()){
                    editText1.setError("Por favor escreva um nome para o treino");
                    editText1.requestFocus();
                }
                else if(c.isEmpty()){
                    editText2.setError("Por favor escreva alguma descrição");
                    editText2.requestFocus();
                } else{
                Treino treino = new Treino(Integer.parseInt(editText1.getText().toString()), editText2.getText().toString(), new Date());
                firestoreHelper.createData(a.getCurrentUser().getUid(), treino);
                Toast.makeText(TreinoAddActivity.this, "Data Saved!", Toast.LENGTH_SHORT).show();
                finish();}
            }
        });




    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
    }
}