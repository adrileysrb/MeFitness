package com.example.mefitness.view;

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
import com.example.mefitness.model.Exercicio;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ExercicioEditActivity extends AppCompatActivity {

    private EditText exercicioEditNome;
    private ImageView exercicioEditImage;
    private EditText exercicioEditObservacoes;
    private Button exercicioEditButton;
    private Exercicio exercicio;
    private String docID, imageURI;
    private int positionExercicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercicio);

        init();
        exercicioEditImage.setOnClickListener(v -> getImageInPhone());
        exercicioEditButton.setOnClickListener(v -> updateExercicio());
    }

    private void init() {
        exercicioEditNome = findViewById(R.id.exercicioEditNome);
        exercicioEditImage = findViewById(R.id.exercicioEditImage);
        exercicioEditObservacoes = findViewById(R.id.exercicioEditObservacoes);
        exercicioEditButton = findViewById(R.id.exercicioEditButton);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        exercicio = (Exercicio) bundle.getSerializable("exercicio");
        docID = (String) bundle.getSerializable("docID");
        positionExercicio = Integer.parseInt(bundle.getSerializable("positionExercicio") + "");
        exercicioEditNome.setText(exercicio.getNumber() + "");
        exercicioEditObservacoes.setText(exercicio.getObservacoes() + "");
        imageURI = exercicio.getImage() + "";
        loadImage();
    }

    private void loadImage() {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .override(200, 180)
                .error(R.mipmap.ic_launcher_round);
        Glide.with(ExercicioEditActivity.this).load(imageURI).apply(options).into(exercicioEditImage);
    }

    private void getImageInPhone() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    private void updateExercicio() {
        String nome = exercicioEditNome.getText().toString();
        String observacoes = exercicioEditObservacoes.getText().toString();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();
        if (nome.isEmpty()) {
            exercicioEditNome.setError("Por favor escreva um nome para o exercicio");
            exercicioEditNome.requestFocus();
        } else if (observacoes.isEmpty()) {
            exercicioEditObservacoes.setError("Por favor escreva alguma observação");
            exercicioEditObservacoes.requestFocus();
        } else {
            updateExercicioInFirebase(userID, docID, positionExercicio, nome, imageURI + "", observacoes);
        }
    }

    public void updateExercicioInFirebase(String userID, String docID, int positionExercicio, String nome, String image, String observacoes) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection(userID).document(docID);
        documentReference.update("exercicios." + positionExercicio + ".nome", nome,
                "exercicios." + positionExercicio + ".image", image,
                "exercicios." + positionExercicio + ".observacoes", observacoes)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(ExercicioEditActivity.this, "Exercicio atualizado", Toast.LENGTH_SHORT).show();
                    startActivityOnCompleteOperation();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ExercicioEditActivity.this, "Erro ao atualizar o exercicio", Toast.LENGTH_SHORT).show();
                    startActivityOnCompleteOperation();
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uploadPicture(ExercicioEditActivity.this, FirebaseStorage.getInstance().getReference(), data.getData(), exercicioEditImage);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ExercicioEditActivity.this, ExerciciosActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("docID", docID);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
    }

    private void uploadPicture(Context context, StorageReference storageReference, Uri imageLocalURI, ImageView imageView) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Carregando imagem...");
        pd.setProgressStyle(R.style.Theme_AppCompat_DayNight_DarkActionBar);
        pd.show();

        String randomImageID = UUID.randomUUID().toString();
        storageReference = storageReference.child("images/" + randomImageID);
        storageReference.putFile(imageLocalURI)
                .addOnSuccessListener(taskSnapshot -> {
                    pd.dismiss();
                    setURIImage(FirebaseStorage.getInstance().getReference(), randomImageID);
                    Snackbar.make(findViewById(android.R.id.content), "Imagem carregada", Snackbar.LENGTH_SHORT).show();
                    try {
                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .error(R.mipmap.ic_launcher_round);
                        Glide.with(context).load(imageLocalURI).apply(options).into(imageView);
                    } catch (Exception e) {
                        Toast.makeText(context, "Sem pre-visualização disponivel nesta guia", Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(context, "Falha no carregamento", Toast.LENGTH_LONG).show();
                })
                .addOnProgressListener(snapshot -> {
                    double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    pd.setMessage("Percentage: " + (int) progressPercent + "%");
                });
    }

    private void setURIImage(StorageReference storageReference, String imageID) {
        storageReference.child("images/" + imageID).getDownloadUrl()
                .addOnSuccessListener(uri -> imageURI = uri + "")
                .addOnFailureListener(exception -> imageURI = "1");
    }

    private void startActivityOnCompleteOperation() {
        Intent intent = new Intent(ExercicioEditActivity.this, ExerciciosActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("docID", docID);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
        finish();
    }

}