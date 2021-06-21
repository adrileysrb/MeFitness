package com.example.mefitness.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mefitness.R;
import com.example.mefitness.viewmodel.ExerciciosAdapter;
import com.example.mefitness.model.Treino;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class ExerciciosActivity extends AppCompatActivity implements ExerciciosAdapter.ItemClickListener{

    Intent intent;
    ExerciciosAdapter adapter;
    Context context;
    Bundle bundle;
    Treino treino;
    String docID;
    Treino t;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercicios);

        intent = this.getIntent();
        bundle = intent.getExtras();
        context = this;
        treino = (Treino) bundle.getSerializable("treino");
        docID = bundle.getSerializable("docID")+"";
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseGetRefreshedTreino(firebaseAuth.getCurrentUser().getUid());



        FloatingActionButton fabS = findViewById(R.id.fabS);
        fabS.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExercicioAddActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("docID", docID);
            bundle.putSerializable("position", positionArray);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
        });
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(2000); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "1");

        sequence.setConfig(config);

        sequence.addSequenceItem(fabS,
                "Utilize esse botão para adicionar seus exercicios", "Entendido! Clique aqui para prosseguir");

        /*sequence.addSequenceItem(mButtonTwo,
                "This is button two", "GOT IT");

        sequence.addSequenceItem(mButtonThree,
                "This is button three", "GOT IT");*/

        sequence.start();

    }
    int positionArray;
    @Override
    public void onItemClick(View view, int position) {
        positionArray = position;
        Toast.makeText(this, "You clicked " + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    private void startRecyclerView(Treino x){

        RecyclerView recyclerView = findViewById(R.id.recycleViewS);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciciosAdapter(this, x, docID);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setMessage("Deseja apagar esse exercicio?")
                        .create();
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.delete(viewHolder.getAdapterPosition());
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
        FirebaseAuth firebaseAuth2 = FirebaseAuth.getInstance();
        firebaseGetRefreshedTreino(firebaseAuth2.getCurrentUser().getUid());
    }

    private void firebaseGetRefreshedTreino(String id){

        db.collection(id)
                .document(docID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                t = documentSnapshot.toObject(Treino.class);
                startRecyclerView(t);
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

}