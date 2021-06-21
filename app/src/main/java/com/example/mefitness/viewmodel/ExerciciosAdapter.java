package com.example.mefitness.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.mefitness.view.ExercicioEditActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private FirebaseFirestore db;

    private ItemClickListener itemClickListener;

    private Treino treino;
    private String docID;

    FirebaseStorage storage;
    StorageReference storageReference;

    public ExerciciosAdapter(Context context, Treino treino, String docID) {
        this.mContext = context;
        this.treino = treino;
        this.docID = docID;
        init();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_exercicio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Map<String, Object> xy = (Map<String, Object>) mData.get(position);

        holder.exercicioNome.setText(xy.get("nome")+"");
        holder.exercicioObservacoes.setText(xy.get("observacoes")+"");
        //setImageInView(position, holder.exercicioImage);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .override(180, 180)
                .error(R.mipmap.ic_launcher_round);
        Glide.with(mContext).load(xy.get("image")).apply(options).into(holder.exercicioImage);
        holder.exercicioEditFAB.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ExercicioEditActivity.class);
            Bundle bundle = new Bundle();
            Map<String, Object> xy1 = (Map<String, Object>) mData.get(position);
            Exercicio e = new Exercicio(
                    Integer.parseInt(xy1.get("nome")+""),
                    xy1.get("image")+"",
                    xy1.get("observacoes")+""
            );
            bundle.putSerializable("exercicio", e);
            bundle.putSerializable("positionExercicio", position); //positionArray
            bundle.putSerializable("treino", treino);
            bundle.putSerializable("docID", docID);
            bundle.putSerializable("image", xy.get("image")+"");
            intent.putExtras(bundle);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);;
        });
        //downloadPicture("08bddc6b-aa87-4795-b31d-3deb394b445e");
        holder.exercicioDeleteFAB.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                    .setMessage("Deseja apagar esse exercicio?")
                    .create();
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim",
                    (dialog, which) -> {
                        delete(position);
                        dialog.dismiss();
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não",
                    (dialog, which) -> {
                        notifyItemChanged(position);
                        dialog.dismiss();
                    });
            alertDialog.show();
        });
        if(mData.size() == 0){
            mLinearLayout = holder.itemView.findViewById(R.id.mainTreino);
            mLinearLayout.removeView(holder.exercicioEditFAB);
        }
    }
    /*public void setImageInView(int size, ImageView imageView){

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        db.collection(uid)
                .document(docID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String xx = documentSnapshot.getString(("exercicioMap.exercicio-0"+size+".image"));



                Glide.with(mContext).load(xx).into(imageView);
                Toast.makeText(mContext, ""+xx, Toast.LENGTH_LONG).show();
            }
        });
    }*/

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) itemClickListener.onItemClick(view, getAdapterPosition());
        }

    }

    public void delete(int posi){
        ArrayList<Object> c = (ArrayList<Object>) mData;
        Map<String, Object >a = new HashMap<>();
        c.remove(posi);
        int size = c.size();

        for(int i = 0; i<size; i++){
            a.put("exercicio-0"+i, c.get(i));
        }

        this.mData =  new ArrayList<>(a.values());

        db.collection(mFirebaseAuth.getCurrentUser().getUid())
                .document(docID)
                .update("exercicioMap", a);
        notifyDataSetChanged();
    }

    public void init(){
        this.mInflater = LayoutInflater.from(mContext);
        this.mData =  new ArrayList<>(treino.getExercicioMap().values());
        this.mFirebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    /*private void downloadPicture(String imageIdFromDatabase){

        //GLIDE
        //FirebaseAuth.getInstance().signOut();
        //downloadPicture();
        //Readme github


        //StorageReference mImageStorage = FirebaseStorage.getInstance().getReference();

        StorageReference storageRef = storage.getReferenceFromUrl("gs://me-fitness-eb529.appspot.com/");
        StorageReference pathReference = storageRef.child("images/"+imageIdFromDatabase);


        pathReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    Glide.with(mContext)
                            .load(pathReference)
                            .into(exercicioImage);
                    StorageReference ref = storageReference.child("images/")
                            .child(imageIdFromDatabase);
                    /*Uri downUri = task.getResult();
                    String imageUrlA = downUri.toString();
                    Glide.with(ExercicioEditActivity.this).load(imageUrlA).into(exercicioEditImage);
                    Toast.makeText(mContext, "aaaaaaaaaa" , Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(mContext, ""+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance();

    }*/

}