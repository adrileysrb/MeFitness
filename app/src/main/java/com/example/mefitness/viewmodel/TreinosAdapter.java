package com.example.mefitness.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mefitness.R;
import com.example.mefitness.model.Treino;
import com.example.mefitness.view.ExerciciosActivity;
import com.example.mefitness.view.TreinoEditActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TreinosAdapter extends FirestoreRecyclerAdapter<Treino, TreinosAdapter.TreinoHolder> {

    private Context context;
    FirestoreRecyclerOptions<Treino> treinos;

    public TreinosAdapter(FirestoreRecyclerOptions treinos, Context context) {
        super(treinos);
        this.context = context;
        this.treinos = treinos;
    }

    @Override
    protected void onBindViewHolder(TreinosAdapter.TreinoHolder holder, int position, Treino treino) {
        holder.treinoNome.setText(treino.getNome() + "");
        holder.treinoDescricao.setText(treino.getDescricao());
        holder.treinoDate.setText(treino.getTimestamp().toString());
        holder.treinoDeleteFAB.setOnClickListener(v -> deleteTreino(position));
        holder.itemView.setOnClickListener(v -> startExerciciosActivity(treino, position));
        holder.treinoEditFAB.setOnClickListener(v -> startExerciciosEditActivity(treino, position));
    }

    private void startExerciciosEditActivity(Treino model, int position) {
        Intent intent = new Intent(context, TreinoEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("value", model);
        DocumentSnapshot snapshot = treinos.getSnapshots().getSnapshot(position);
        bundle.putSerializable("position", position);
        bundle.putSerializable("dbID", snapshot.getId() + "");
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private void startExerciciosActivity(Treino model, int position) {
        DocumentSnapshot documentSnapshot = treinos.getSnapshots().getSnapshot(position);
        Intent intent = new Intent(context, ExerciciosActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("value", model);
        bundle.putSerializable("docID", documentSnapshot.getId());
        intent.putExtras(bundle);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void deleteTreino(int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage("Deseja apagar esse treino?")
                .create();
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim",
                (dialog, which) -> {
                    deleteTreinoInFirebase(position);
                    dialog.dismiss();
                    notifyDataSetChanged();
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não",
                (dialog, which) -> {
                    dialog.dismiss();
                    notifyDataSetChanged();
                });
        alertDialog.show();
    }

    public void deleteTreinoInFirebase(int pos) {
        Toast.makeText(context, "Treino apagado", Toast.LENGTH_SHORT).show();
        getSnapshots().getSnapshot(pos).getReference().delete();
    }

    @Override
    public TreinoHolder onCreateViewHolder(ViewGroup group, int i) {
        View view = LayoutInflater.from(group.getContext())
                .inflate(R.layout.item_treino, group, false);
        return new TreinoHolder(view);
    }

    public class TreinoHolder extends RecyclerView.ViewHolder {

        TextView treinoNome;
        TextView treinoDescricao;
        TextView treinoDate;
        FloatingActionButton treinoEditFAB;
        FloatingActionButton treinoDeleteFAB;

        public TreinoHolder(View itemView) {
            super(itemView);
            treinoNome = itemView.findViewById(R.id.treinoNome);
            treinoDescricao = itemView.findViewById(R.id.treinoDescricao);
            treinoDate = itemView.findViewById(R.id.treinoDate);
            treinoEditFAB = itemView.findViewById(R.id.treinoEditFAB);
            treinoDeleteFAB = itemView.findViewById(R.id.treinoDeleteFAB);
        }
    }
}


