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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.UUID;

public class ExercicioAddActivity extends AppCompatActivity {

    private EditText exercicioAddNome, exercicioAddObservacoes;
    private ImageView exercicioAddImage;
    private Button exercicioAddButton;
    private String docID, imageURI = "1";
    private int sizeMap;
    private String nome, observacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercicio);
        init();

        exercicioAddImage.setOnClickListener(v -> getImageInPhone());
        exercicioAddButton.setOnClickListener(v -> addExercicio());
    }

    private void init() {
        exercicioAddNome = findViewById(R.id.exercicioAddNome);
        exercicioAddObservacoes = findViewById(R.id.exercicioAddObservacoes);
        exercicioAddImage = findViewById(R.id.exercicioAddImage);
        exercicioAddButton = findViewById(R.id.exercicioAddButton);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        docID = (String) bundle.getSerializable("docID");
        setSizeMap(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance().getCurrentUser().getUid(), docID);
    }

    private void getImageInPhone() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    private void addExercicio() {
        nome = exercicioAddNome.getText().toString();
        observacoes = exercicioAddObservacoes.getText().toString();
        if (nome.isEmpty()) {
            exercicioAddNome.setError("Por favor escreva um nome para o exercicio");
            exercicioAddNome.requestFocus();
        } else if (observacoes.isEmpty()) {
            exercicioAddObservacoes.setError("Por favor escreva alguma observação");
            exercicioAddObservacoes.requestFocus();
        } else {

            addExercicioInFireStore(sizeMap, docID, nome, imageURI + "", observacoes);
        }
    }

    public void addExercicioInFireStore(int size, String docID, String nome, String image, String observacoes) {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid() + "";
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference doc = db.collection(userID).document(docID);
        doc.update("exercicios." + size + ".nome", nome,
                "exercicios." + size + ".image", image,
                "exercicios." + size + ".observacoes", observacoes)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(ExercicioAddActivity.this, "Exercicio adicionado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ExercicioAddActivity.this, ExerciciosActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("docID", docID);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ExercicioAddActivity.this, "Erro ao adicionar o exercicio", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ExercicioAddActivity.this, ExerciciosActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("docID", docID);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
                    finish();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uploadPicture(ExercicioAddActivity.this, FirebaseStorage.getInstance().getReference(), data.getData(), exercicioAddImage);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ExercicioAddActivity.this, ExerciciosActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("docID", docID);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
        finish();
    }

    private String uploadPicture(Context context, StorageReference storageReference, Uri imageLocalURI, ImageView imageView) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Carregando imagem...");
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
        return randomImageID;
    }

    private void setURIImage(StorageReference storageReference, String imageID) {
        storageReference.child("images/" + imageID).getDownloadUrl().addOnSuccessListener(uri -> {
            imageURI = uri + "";
        }).addOnFailureListener(exception -> imageURI = "1");
    }

    private String setSizeMap(FirebaseFirestore firebaseFirestore, String userID, String docID) {
        firebaseFirestore.collection(userID).document(docID).get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> a = (Map<String, Object>) documentSnapshot.get("exercicios");
            sizeMap = a.size();
        });
        return String.valueOf(sizeMap);
    }

}