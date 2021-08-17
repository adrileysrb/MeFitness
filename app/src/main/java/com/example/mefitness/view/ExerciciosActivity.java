package com.example.mefitness.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.mefitness.R;
import com.example.mefitness.viewmodel.ExerciciosAdapter;
import com.example.mefitness.model.Treino;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ExerciciosActivity extends AppCompatActivity {

    private ExerciciosAdapter adapter;
    private String docID;
    private FloatingActionButton fabS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercicios);

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

        bindDataInRecyclerView();
        fabS.setOnClickListener(v -> startExercicioAddActivity());
    }

    private void startExercicioAddActivity() {
        Intent intent = new Intent(ExerciciosActivity.this, ExercicioAddActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("docID", docID);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
        finish();
    }

    private void init() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        docID = bundle.getSerializable("docID") + "";
        fabS = findViewById(R.id.fabS);
    }

    private void startRecyclerView(Treino treino) {
        RecyclerView recyclerView = findViewById(R.id.recycleViewS);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new ExerciciosAdapter(this, treino, docID);
        recyclerView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog alertDialog = new AlertDialog.Builder(ExerciciosActivity.this)
                        .setMessage("Deseja apagar esse exercicio?")
                        .create();
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim",
                        (dialog, which) -> {
                            adapter.deleteExercicioInFirebase(viewHolder.getAdapterPosition());
                            dialog.dismiss();
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não",
                        (dialog, which) -> {
                            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            dialog.dismiss();
                        });
                alertDialog.show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void bindDataInRecyclerView() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        db.collection(firebaseAuth.getCurrentUser().getUid())
                .document(docID)
                .get().addOnSuccessListener(documentSnapshot -> {
            startRecyclerView(documentSnapshot.toObject(Treino.class));
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        finish();
    }
}