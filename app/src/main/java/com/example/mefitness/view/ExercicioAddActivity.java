package com.example.mefitness.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mefitness.R;
import com.example.mefitness.viewmodel.FirestoreHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.UUID;

public class ExercicioAddActivity extends AppCompatActivity {

    Intent intent;
    Bundle bundle;

    EditText exercicioAddNome, exercicioAddObservacoes;
    ImageView exercicioAddImage;
    Button exercicioAddButton;

    FirestoreHelper firestoreHelper;

    String imageID="1", docID, size;
    //Uri uriImage;
    int sizeMap;

    //URI da imagem
    String imageURI="1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercicio);
        init();

        exercicioAddImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        exercicioAddButton.setOnClickListener(v -> {
            String nome, observacoes;
            nome = exercicioAddNome.getText().toString();
            observacoes = exercicioAddObservacoes.getText().toString();

            if(nome.isEmpty()){
                exercicioAddNome.setError("Por favor escreva um nome para o exercicio");
                exercicioAddNome.requestFocus();
            }
            else if(observacoes.isEmpty()){
                exercicioAddObservacoes.setError("Por favor escreva alguma observação");
                exercicioAddObservacoes.requestFocus();
            } else{

                firestoreHelper.createDataExercicioAdd(FirebaseFirestore.getInstance(), sizeMap,
                        FirebaseAuth.getInstance().getCurrentUser().getUid(), docID, nome, imageURI+"", observacoes);
                Toast.makeText(ExercicioAddActivity.this, "Exercicio adicionado", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() !=null){
            uploadPicture( ExercicioAddActivity.this, FirebaseStorage.getInstance().getReference(), data.getData(), exercicioAddImage);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
    }

    private String uploadPicture(Context context, StorageReference storageReference, Uri imageLocalURI, ImageView imageView){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Carregando imagem...");
        pd.show();

        String randomImageID = UUID.randomUUID().toString();
        storageReference = storageReference.child("images/"+ randomImageID);
        storageReference.putFile(imageLocalURI)
                .addOnSuccessListener(taskSnapshot -> {
                    pd.dismiss();
                    setURIImage(FirebaseStorage.getInstance().getReference(), randomImageID);
                    Snackbar.make(findViewById(android.R.id.content), "Imagem carregada", Snackbar.LENGTH_LONG).show();
                    //downloadPicture(imageId);
                    try {
                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .error(R.mipmap.ic_launcher_round);
                        Glide.with(context).load(imageLocalURI).apply(options).into(imageView);
                    }
                    catch (Exception e){
                        Toast.makeText(context, "Sem pre-visualização disponivel nesta guia", Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(context, "Falha no carregamento", Toast.LENGTH_LONG).show();
                })
                .addOnProgressListener(snapshot -> {
                    double progressPercent = (100.00* snapshot.getBytesTransferred()/ snapshot.getTotalByteCount());
                    pd.setMessage("Percentage: " + (int) progressPercent + "%");
                });
        return randomImageID;
    }

    /*public void setImageInView(Context context, FirebaseFirestore firebaseFirestore, String userID, String docID, int positionExercicio, ImageView imageView) {
        firebaseFirestore.collection(userID)
                .document(docID)
                .get().addOnSuccessListener(documentSnapshot -> {
            imageFirestoreURI = documentSnapshot.getString(("exercicioMap.exercicio-0" + positionExercicio + ".image"));
            if (Integer.parseInt(imageURI) == 1) {
                imageView.setImageResource(R.drawable.ic_launcher_foreground);
            } else {
                Glide.with(context).load(imageFirestoreURI).into(imageView);
            }
        });
    }*/

    private void setURIImage(StorageReference storageReference, String imageID){
        storageReference.child("images/"+imageID).getDownloadUrl().addOnSuccessListener(uri -> {
            imageURI = uri+"";
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                imageURI="1";
            }
        });
    }

    private void init(){
        exercicioAddNome = findViewById(R.id.exercicioAddNome);
        exercicioAddObservacoes = findViewById(R.id.exercicioAddObservacoes);
        exercicioAddImage = findViewById(R.id.exercicioAddImage);
        exercicioAddButton = findViewById(R.id.exercicioAddButton);

        firestoreHelper = new FirestoreHelper(FirebaseFirestore.getInstance());

        intent = this.getIntent();
        bundle = intent.getExtras();
        docID = (String) bundle.getSerializable("docID");

        setSizeMap(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance().getCurrentUser().getUid(), docID);
    }

    private String setSizeMap(FirebaseFirestore firebaseFirestore, String userID, String docID){
        firebaseFirestore.collection(userID).document(docID).get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> a = (Map<String, Object>) documentSnapshot.get("exercicioMap");
            sizeMap = a.size()+1;
        });
        return String.valueOf(sizeMap);
    }

}