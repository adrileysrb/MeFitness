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
import com.example.mefitness.model.Exercicio;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ExercicioEditActivity extends AppCompatActivity {

    Intent intent;
    Bundle bundle;

    EditText exercicioEditNome;
    ImageView exercicioEditImage;
    EditText exercicioEditObservacoes;
    Button exercicioEditButton;

    FirestoreHelper firestoreHelper;

    //Intent Extras
    Exercicio exercicio;
    String docID, image;
    int positionExercicio;

    //URI da imagem
    String imageURI="1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercicio);

        init();

        exercicioEditImage.setOnClickListener(v-> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
        });

        exercicioEditNome.setText(exercicio.getNumber()+"");
        exercicioEditObservacoes.setText(exercicio.getObservacoes()+"");

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .override(200, 180)
                .error(R.mipmap.ic_launcher_round);
        Glide.with(ExercicioEditActivity.this).load(image).apply(options).into(exercicioEditImage);

        exercicioEditButton.setOnClickListener(v -> {
            String nome = exercicioEditNome.getText().toString();
            String observacoes = exercicioEditObservacoes.getText().toString();

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            String userID = firebaseAuth.getCurrentUser().getUid();
            if(nome.isEmpty()){
                exercicioEditNome.setError("Por favor escreva um nome para o exercicio");
                exercicioEditNome.requestFocus();
            }
            else if(observacoes.isEmpty()){
                exercicioEditObservacoes.setError("Por favor escreva alguma observação");
                exercicioEditObservacoes.requestFocus();
            } else
                {
                    firestoreHelper.updateExercicioEdit(userID, docID, positionExercicio, nome, imageURI+"", observacoes);
                Toast.makeText(ExercicioEditActivity.this, "Dados atualizados", Toast.LENGTH_SHORT).show();
                finish();}
            });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() !=null){
            uploadPicture( ExercicioEditActivity.this, FirebaseStorage.getInstance().getReference(), data.getData(), exercicioEditImage);
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

        exercicioEditNome = findViewById(R.id.exercicioEditNome);
        exercicioEditImage = findViewById(R.id.exercicioEditImage);
        exercicioEditObservacoes = findViewById(R.id.exercicioEditObservacoes);
        exercicioEditButton = findViewById(R.id.exercicioEditButton);

        intent = this.getIntent();
        bundle = intent.getExtras();

        exercicio = (Exercicio) bundle.getSerializable("exercicio");
        docID = (String) bundle.getSerializable("docID");
        image = (String) bundle.getSerializable("image");
        positionExercicio = Integer.parseInt(bundle.getSerializable("positionExercicio")+"") ;

        firestoreHelper = new FirestoreHelper(FirebaseFirestore.getInstance());
    }
}