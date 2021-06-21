package com.example.mefitness.view;

import android.content.Intent;
import android.os.Bundle;

import com.example.mefitness.R;
import com.example.mefitness.model.Treino;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class TreinoEditActivity extends AppCompatActivity {


    String a;
    String c;
    String dbID;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_treino);

        EditText editText1 = findViewById(R.id.editText01);
        EditText editText2 = findViewById(R.id.editText02);

        Button button = findViewById(R.id.button);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        Treino treino = (Treino) bundle.getSerializable("value");

        a = treino.getNome()+"";
        c = treino.getDescricao()+"";
        editText1.setText(a);
        editText2.setText(c);

        db = FirebaseFirestore.getInstance();
        dbID = bundle.getSerializable("dbID")+"";

        button.setOnClickListener(v -> {
            a = editText1.getText().toString();
            c = editText2.getText().toString();
            if(a.isEmpty()){
                editText1.setError("Por favor escreva um nome para o treino");
                editText1.requestFocus();
            }
            else if(c.isEmpty()){
                editText2.setError("Por favor escreva alguma descrição");
                editText2.requestFocus();
            } else{
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            String uid = firebaseAuth.getCurrentUser().getUid();

            Toast.makeText(getApplication(), "Updated", Toast.LENGTH_LONG).show();
            db.collection(uid)
                    .document(dbID)
                    .update("nome", Integer.parseInt(a));

            db.collection(uid)
                    .document(dbID)
                    .update("descricao", c);
            db.collection(uid)
                    .document(dbID)
                    .update("timestramp", new Date());

            Toast.makeText(TreinoEditActivity.this, "Data Saved!", Toast.LENGTH_SHORT).show();
            finish();}
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
    }
}