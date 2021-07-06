package com.example.mefitness.view;

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

public class TreinosActivity extends AppCompatActivity {

    private Context context;
    private FloatingActionButton fabAddTreino;
    private RecyclerView treinosRecycleView;
    private TreinosAdapter adapter;
    private ShimmerFrameLayout shimmerFrameLayout;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treinos);

        init();
        setTollbar();
        setShimmerEffect();
        tutorial();
        startAdapter();
        fabAddTreino.setOnClickListener((v) -> startTreinoAddActivity());
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void init() {
        context = this;
        fabAddTreino = findViewById(R.id.treinos_fabAddTreino);
        treinosRecycleView = findViewById(R.id.treinos_recycleView);
        shimmerFrameLayout = findViewById(R.id.treinos_shimmerViewContainer);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
    }

    private void setShimmerEffect() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            });
        });
    }

    private void startTreinoAddActivity() {
        Intent intent = new Intent(context, TreinoAddActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);

    }

    private void tutorial() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(2000);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "1");
        sequence.setConfig(config);
        sequence.addSequenceItem(fabAddTreino,
                "Utilize esse botão para adicionar seus treinos", "Clique aqui para prosseguir");
        sequence.start();
    }

    private void setTollbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setNavigationIcon(R.drawable.ic_baseline_directions_run_24);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
    }

    private void startAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new TreinosAdapter(getTreinoList(), context);
        treinosRecycleView.setLayoutManager(linearLayoutManager);
        treinosRecycleView.setHasFixedSize(true);
        treinosRecycleView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteTreino(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(treinosRecycleView);
    }

    private FirestoreRecyclerOptions<Treino> getTreinoList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection(userID);
        return new FirestoreRecyclerOptions.Builder<Treino>()
                .setQuery(query, Treino.class)
                .build();
    }
}
