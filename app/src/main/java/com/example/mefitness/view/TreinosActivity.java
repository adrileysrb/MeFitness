package com.example.mefitness.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mefitness.R;
import com.example.mefitness.model.User;
import com.example.mefitness.viewmodel.TreinosAdapter;
import com.example.mefitness.model.Treino;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static java.lang.Thread.sleep;

public class TreinosActivity extends AppCompatActivity{

    private FirebaseFirestore db;
    private TreinosAdapter adapter;
    private Context context;

    /*private String userName;
    private Bundle bundle;
    private Intent intent;*/

    private FloatingActionButton fab;
    private RecyclerView treinoList;
    private LinearLayoutManager linearLayoutManager;


    ExecutorService executor;
    Handler handler;
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treinos);
        setTollbar();
        init();
        setShimmerEffect();

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(2000); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "1");

        sequence.setConfig(config);

        sequence.addSequenceItem(fab,
                "Utilize esse botão para adicionar seus treinos", "Entendido! Clique aqui para prosseguir");

        /*sequence.addSequenceItem(mButtonTwo,
                "This is button two", "GOT IT");

        sequence.addSequenceItem(mButtonThree,
                "This is button three", "GOT IT");*/

        sequence.start();


        FirebaseAuth a = FirebaseAuth.getInstance();
        startAdapter(getTreinoList(a.getCurrentUser().getUid()));

        fab.setOnClickListener((v) -> {
                Intent intent = new Intent(context, TreinoAddActivity.class);
                startActivity(intent);
            overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
        });
    }



    private void setTollbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Me Fitness");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_directions_run_24);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
    }

    private void startAdapter(FirestoreRecyclerOptions<Treino> res){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        treinoList.setLayoutManager(linearLayoutManager);
        adapter = new TreinosAdapter(res, context);
        treinoList = findViewById(R.id.recycleView);
        treinoList.setHasFixedSize(true);
        treinoList.setLayoutManager(new LinearLayoutManager(this));
        treinoList.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        treinoList.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setMessage("Deseja apagar esse treino?")
                        .create();
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim",
                        (dialog, which) -> {
                            adapter.delete(viewHolder.getAdapterPosition());
                            dialog.dismiss();
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não",
                        (dialog, which) -> {
                            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            dialog.dismiss();
                        });
                alertDialog.show();
            }
        }).attachToRecyclerView(treinoList);

    }
    private void init(){
        context = this;
        db = FirebaseFirestore.getInstance();
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        fab = findViewById(R.id.fab);
        treinoList = findViewById(R.id.recycleView);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);

        /*intent = this.getIntent();
        bundle = intent.getExtras();
        userName = (String) bundle.getSerializable("userName");
        setUserName();*/

    }
   /* private void setUserName(){

        FirebaseFirestore.getInstance().collection(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        )
        .add(new User(
                FirebaseAuth.getInstance().getCurrentUser().getUid()+"",
                userName+""
        ))
        ;
    }*/
    private FirestoreRecyclerOptions<Treino> getTreinoList(String idea){
        //db.collection(id).document(id).set(new User(id));
        Query query = db.collection(idea);
        return new FirestoreRecyclerOptions.Builder<Treino>()
                .setQuery(query, Treino.class)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.op1:
                Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context, LoginActivity.class));
                overridePendingTransition(R.anim.zoom_out, R.anim.static_animation);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setShimmerEffect(){
        executor.execute(() -> {
            try {
                sleep(3000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            handler.post(() -> {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            });
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

}

/*
* FirebaseAuth auth;
* auth = FirebaseAuth.getInstance();
*
* auth.signOut();
* startActivity(new Intent(context, LoginActivity.class));
* finish();
* */