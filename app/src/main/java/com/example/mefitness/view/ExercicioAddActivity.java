package com.example.mefitness.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mefitness.R;
import com.example.mefitness.model.Categorias;
import com.example.mefitness.viewmodel.CategoriasAdapter;
import com.example.mefitness.viewmodel.DificuldadesAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ExercicioAddActivity extends AppCompatActivity {

    private EditText exercicioAddNome, exercicioAddObservacoes;
    private ImageView exercicioAddImage;
    private Button exercicioAddButton;
    private String docID, imageURI = "1";
    private int sizeMap;
    private String nome, observacoes, categoria, dificuldade;


    private List<Categorias> objectsCategoria;
    private List<Categorias> objectsDificuldade;
    private AutoCompleteTextView exercicioAddCategoria;
    private AutoCompleteTextView exercicioAddDificuldade;
    private TextInputLayout exercicioAddCategoria1;
    private TextInputLayout exercicioAddDificuldade1;
    private CategoriasAdapter arrayAdapterCategoria;
    private DificuldadesAdapter arrayAdapterDificuldadade;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercicio);
        init();


        SharedPreferences sharedPreferences = getSharedPreferences("AppSettingPref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean isNightMode = sharedPreferences.getBoolean("NightMode", false);

        if(isNightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        exercicioAddImage.setOnClickListener(v -> getImageInPhone());
        exercicioAddButton.setOnClickListener(v -> addExercicio());


        exercicioAddCategoria.setOnItemClickListener((parent, view, position, id) -> exercicioAddCategoria1.setStartIconDrawable(objectsCategoria.get(position).getImage()));
        exercicioAddDificuldade.setOnItemClickListener((parent, view, position, id) -> exercicioAddDificuldade1.setStartIconDrawable(objectsDificuldade.get(position).getImage()));

    }


    private void init() {
        exercicioAddNome = findViewById(R.id.exercicioEditNome);
        exercicioAddObservacoes = findViewById(R.id.exercicioEditDescricao);
        exercicioAddImage = findViewById(R.id.exercicioEditImage);
        exercicioAddButton = findViewById(R.id.exercicioEditButton);
        exercicioAddCategoria = findViewById(R.id.exercicioEditCategoria);
        exercicioAddDificuldade = findViewById(R.id.exercicioEditDificuldades);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        docID = (String) bundle.getSerializable("docID");
        setSizeMap(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance().getCurrentUser().getUid(), docID);

        String[] a = getResources().getStringArray(R.array.categorias);
        String[] b = getResources().getStringArray(R.array.difi);

        objectsCategoria = new ArrayList<Categorias>();
        objectsCategoria.add(new Categorias("Aerobico", R.drawable.human));
        objectsCategoria.add(new Categorias("Meditação", R.drawable.ic_baseline_lock_24));
        objectsCategoria.add(new Categorias("Hipertrofia", R.drawable.ic_baseline_edit_24));
        objectsCategoria.add(new Categorias("Musculação", R.drawable.ic_baseline_title_24));


        objectsDificuldade = new ArrayList<Categorias>();
        objectsDificuldade.add(new Categorias("Facil", R.drawable.human_handsdown));
        objectsDificuldade.add(new Categorias("Media", R.drawable.human));
        objectsDificuldade.add(new Categorias("Dificil", R.drawable.human_handsup));


        arrayAdapterCategoria = new CategoriasAdapter(this, R.layout.dropdown_item, a);

        arrayAdapterDificuldadade = new DificuldadesAdapter(this, R.layout.drop_down_item_2, b);

        exercicioAddCategoria1 = findViewById(R.id.exercicioEditCategoria1);
        exercicioAddDificuldade1 = findViewById(R.id.exercicioEditDificuldades1);

        exercicioAddCategoria.setAdapter(arrayAdapterCategoria);
        exercicioAddDificuldade.setAdapter(arrayAdapterDificuldadade);


    }



    private void addExercicio() {
        nome = exercicioAddNome.getText().toString();
        observacoes = exercicioAddObservacoes.getText().toString();
        categoria = exercicioAddCategoria.getText().toString();
        dificuldade = exercicioAddDificuldade.getText().toString();

        if (nome.isEmpty()) {
            exercicioAddNome.setError("Por favor escreva um nome para o exercicio");
            exercicioAddNome.requestFocus();
        } else if (observacoes.isEmpty()) {
            exercicioAddObservacoes.setError("Por favor escreva alguma observação");
            exercicioAddObservacoes.requestFocus();
        } else {

            addExercicioInFireStore(sizeMap, docID, nome, imageURI + "", observacoes, categoria, dificuldade);
        }
    }

    public void addExercicioInFireStore(int size, String docID, String nome, String image, String observacoes, String categoria, String dificuldade) {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid() + "";
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference doc = db.collection(userID).document(docID);
        doc.update("exercicios." + size + ".nome", nome,
                "exercicios." + size + ".image", image,
                "exercicios." + size + ".observacoes", observacoes, "exercicios." + size + ".categoria", categoria, "exercicios." + size + ".dificuldade", dificuldade)
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
    private void getImageInPhone() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
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