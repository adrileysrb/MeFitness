package com.example.mefitness.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mefitness.R;
import com.example.mefitness.model.Treino;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class TreinoAddActivity extends AppCompatActivity {

    Context context;
    EditText treinoName;
    EditText treinoDescription;
    Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_treino);

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

        btnAdd.setOnClickListener(v -> createTreino());
    }

    private void init() {
        context = this;
        treinoName = findViewById(R.id.treinoEdit_name);
        treinoDescription = findViewById(R.id.treinoEdit_description);
        btnAdd = findViewById(R.id.treinoAdd_btnAdd);
    }

    private void createTreino() {
        String name = treinoName.getText().toString();
        String description = treinoDescription.getText().toString();
        if (name.isEmpty()) {
            treinoName.setError(getString(R.string.entry_treino_name));
            treinoName.requestFocus();
        } else if (description.isEmpty()) {
            treinoDescription.setError(getString(R.string.entry_treino_description));
            treinoDescription.requestFocus();
        } else {
            Treino treino = new Treino(treinoName.getText().toString(),
                    treinoDescription.getText().toString(), new Date());
            addTreinoInFirestore(treino);
            finish();
        }
    }

    private void addTreinoInFirestore(Treino treino) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection(firebaseAuth.getCurrentUser().getUid()).document();
        documentReference.set(treino)
                .addOnSuccessListener(unused -> Toast.makeText(context, getString(R.string.treino_add_text), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, getString(R.string.treino_add_failure_text), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
        finish();
    }
}