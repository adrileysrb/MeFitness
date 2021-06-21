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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TreinosAdapter extends FirestoreRecyclerAdapter<Treino, TreinosAdapter.TreinoHolder> {

    private Context context;
    FirestoreRecyclerOptions<Treino> response;
    ArrayList<Treino> a;

    public TreinosAdapter( FirestoreRecyclerOptions options, Context context) {
        super(options);
        this.context = context;
        this.response = options;
        a = new ArrayList<>();
    }

    @Override
    protected void onBindViewHolder(TreinosAdapter.TreinoHolder holder, int position, Treino model) {
        Map<String, Object> x;
        if(model.getNome()==0){

        } else {

            if (model.getExercicioMap() == null) {
                Map<String, Object> y = new HashMap<>();
                Map<String, Object> z = new HashMap<>();
                z.put("nome", "No Data");
                z.put("image", "No Data");
                z.put("observacoes", "No Data");
                y.put("No data", z);
                x = y;
            } else {
                x = model.getExercicioMap();
            }
            a.add(new Treino(model.getNome(),
                    model.getDescricao(),
                    model.getTimestramp(),
                    x
            ));

            holder.treinoNome.setText(model.getNome() + "");
            holder.treinoDescricao.setText(model.getDescricao());
            holder.treinoDate.setText(model.getTimestramp().toString());

            holder.treinoDeleteFAB.setOnClickListener(v -> {
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setMessage("Deseja apagar esse treino?")
                        .create();
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim",
                        (dialog, which) -> {
                            delete(position);
                            dialog.dismiss();
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            });

            holder.itemView.setOnClickListener( v -> {
                    DocumentSnapshot snapshot = response.getSnapshots().getSnapshot(position);
                    Toast.makeText(context, "" + model.getNome(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, ExerciciosActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("value", a.get(position));
                    bundle.putSerializable("docID", snapshot.getId());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            });

            holder.treinoEditFAB.setOnClickListener(v -> {
                    Intent intent = new Intent(context, TreinoEditActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("value", a.get(position));
                     DocumentSnapshot snapshot = response.getSnapshots().getSnapshot(position);
                    Toast.makeText(context, "" + model.getNome(), Toast.LENGTH_SHORT).show();
                    bundle.putSerializable("position", position);
                bundle.putSerializable("dbID", snapshot.getId()+"");
                    intent.putExtras(bundle);
                    context.startActivity(intent);
            });
        }

    }

    public void delete(int pos){
        Toast.makeText(context, ""+pos, Toast.LENGTH_SHORT).show();
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


