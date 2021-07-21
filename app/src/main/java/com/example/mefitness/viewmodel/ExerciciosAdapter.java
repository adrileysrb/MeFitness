package com.example.mefitness.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mefitness.R;
import com.example.mefitness.model.Exercicio;
import com.example.mefitness.model.Treino;
import com.example.mefitness.view.DetailExercicio;
import com.example.mefitness.view.ExercicioEditActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExerciciosAdapter extends RecyclerView.Adapter<ExerciciosAdapter.ViewHolder> {

    private List<Object> mData;
    private LayoutInflater mInflater;
    private Context mContext;
    private LinearLayout mLinearLayout;
    private FirebaseAuth mFirebaseAuth;

    private Treino treino;
    private String docID;

    public ExerciciosAdapter(Context context, Treino treino, String docID) {
        this.mContext = context;
        this.treino = treino;
        this.docID = docID;
        init();
    }

    public void init() {
        this.mInflater = LayoutInflater.from(mContext);
        this.mData = new ArrayList<>(treino.getExercicios().values());
        this.mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_exercicio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map<String, Object> exercicio = (Map<String, Object>) mData.get(position);
        holder.exercicioNome.setText(exercicio.get("nome") + "");
        holder.exercicioObservacoes.setText(exercicio.get("observacoes") + "");
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .override(180, 180)
                .error(R.mipmap.ic_launcher_round);
        Glide.with(mContext).load(exercicio.get("image")).apply(options).into(holder.exercicioImage);
        holder.exercicioEditFAB.setOnClickListener(v -> startExercicioEditActivity(exercicio, position));
        holder.exercicioDeleteFAB.setOnClickListener(v -> deleteExercicio(position));
        if (mData.size() == 0) {
            mLinearLayout = holder.itemView.findViewById(R.id.mainTreino);
            mLinearLayout.removeView(holder.exercicioEditFAB);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailExercicio.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("image", exercicio.get("image")+"");
                bundle.putSerializable("nome", exercicio.get("nome")+"");
                bundle.putSerializable("observacoes", exercicio.get("observacoes")+"");
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

    }

    private void deleteExercicio(int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setMessage("Deseja apagar esse exercicio?")
                .create();
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim",
                (dialog, which) -> {
                    deleteExercicioInFirebase(position);
                    dialog.dismiss();
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não",
                (dialog, which) -> {
                    notifyItemChanged(position);
                    dialog.dismiss();
                });
        alertDialog.show();
    }

    private void startExercicioEditActivity(Map<String, Object> exercicio, int position) {
        Map<String, Object> xy1 = (Map<String, Object>) mData.get(position);
        Exercicio e = new Exercicio(
                Integer.parseInt(xy1.get("nome") + ""),
                xy1.get("image") + "",
                xy1.get("observacoes") + ""
        );
        Intent intent = new Intent(mContext, ExercicioEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("exercicio", e);
        bundle.putSerializable("positionExercicio", position);
        bundle.putSerializable("docID", docID);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
        ((Activity) mContext).finish();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView exercicioNome, exercicioObservacoes;
        FloatingActionButton exercicioEditFAB;
        FloatingActionButton exercicioDeleteFAB;
        ImageView exercicioImage;

        ViewHolder(View itemView) {
            super(itemView);
            exercicioNome = itemView.findViewById(R.id.exercicioNome);
            exercicioObservacoes = itemView.findViewById(R.id.exercicioObservacoes);
            exercicioImage = itemView.findViewById(R.id.exercicioImage);
            exercicioEditFAB = itemView.findViewById(R.id.exercicioEditFAB);
            exercicioDeleteFAB = itemView.findViewById(R.id.exercicioDeleteFAB);
        }
    }

    public void deleteExercicioInFirebase(int posi) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<Object> c = (ArrayList<Object>) mData;
        Map<String, Object> a = new HashMap<>();
        c.remove(posi);
        int size = c.size();
        for (int i = 0; i < size; i++) {
            a.put("" + i, c.get(i));
        }
        this.mData = new ArrayList<>(a.values());
        db.collection(mFirebaseAuth.getCurrentUser().getUid())
                .document(docID)
                .update("exercicios", a);
        notifyDataSetChanged();
    }
}