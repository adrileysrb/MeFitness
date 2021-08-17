package com.example.mefitness.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.mefitness.R;
import com.example.mefitness.model.Treino;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class TreinoEditActivity extends AppCompatActivity {

    private String docID;

    private EditText treinoName;
    private EditText treinoDescription;
    private Button button;
    private Treino treino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_treino);

        init();

        SharedPreferences sharedPreferences = getSharedPreferences("AppSettingPref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean isNightMode = sharedPreferences.getBoolean("NightMode", false);

        if(isNightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


        button.setOnClickListener(v -> editTreino());
    }

    public void init() {
        treinoName = findViewById(R.id.treinoEdit_name);
        treinoDescription = findViewById(R.id.treinoEdit_description);
        button = findViewById(R.id.treinoEdit_btnSave);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        treino = (Treino) bundle.getSerializable("value");
        docID = bundle.getSerializable("dbID") + "";
        treinoName.setText(treino.getNome() + "");
        treinoDescription.setText(treino.getDescricao() + "");
    }

    public void editTreino() {
        String name = treinoName.getText().toString();
        String description = treinoDescription.getText().toString();
        if (name.isEmpty()) {
            treinoName.setError(getString(R.string.entry_treino_name));
            treinoName.requestFocus();
        } else if (description.isEmpty()) {
            treinoDescription.setError(getString(R.string.entry_treino_description));
            treinoDescription.requestFocus();
        } else {
            editTreinoInFirestore(name, description);
            finish();
        }
    }

    private void editTreinoInFirestore(String name, String description) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore.collection(firebaseAuth.getCurrentUser().getUid())
                .document(docID)
                .update("nome", name, "descricao", description, "timestamp", new Date())
                .addOnSuccessListener(unused -> Toast.makeText(TreinoEditActivity.this, getString(R.string.treino_edit_text), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(TreinoEditActivity.this, getString(R.string.treino_edit_failure_text), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
    }
}