package com.example.mefitness.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mefitness.R;
import com.example.mefitness.model.Treino;
import com.example.mefitness.viewmodel.TreinosAdapter;
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

public class FragmentTreino extends Fragment {
    private Context context;
    private FloatingActionButton fabAddTreino;
    private RecyclerView treinosRecycleView;
    private TreinosAdapter adapter;
    private ShimmerFrameLayout shimmerFrameLayout;
    private String userID;

    View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_treinos, container, false);
        init();
        setShimmerEffect();
        tutorial();
        startAdapter();
        fabAddTreino.setOnClickListener((v) -> startTreinoAddActivity());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }




    private void init() {
        context = getActivity();
        fabAddTreino = view.findViewById(R.id.treinos_fabAddTreino);
        treinosRecycleView = view.findViewById(R.id.treinos_recycleView);
        shimmerFrameLayout = view.findViewById(R.id.treinos_shimmerViewContainer);
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
    }

    private void tutorial() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(2000);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "1");
        sequence.setConfig(config);
        sequence.addSequenceItem(fabAddTreino,
                "Utilize esse botão para adicionar seus treinos", "Clique aqui para prosseguir");
        sequence.start();
    }


    private void startAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new TreinosAdapter(getTreinoList(), context);
        treinosRecycleView.setLayoutManager(linearLayoutManager);
        treinosRecycleView.setHasFixedSize(true);
        treinosRecycleView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        /*new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteTreino(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(treinosRecycleView);*/
    }

    private FirestoreRecyclerOptions<Treino> getTreinoList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection(userID);
        return new FirestoreRecyclerOptions.Builder<Treino>()
                .setQuery(query, Treino.class)
                .build();
    }
}