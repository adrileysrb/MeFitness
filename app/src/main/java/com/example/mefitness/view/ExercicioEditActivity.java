package com.example.mefitness.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mefitness.R;
import com.example.mefitness.model.Categorias;
import com.example.mefitness.model.Exercicio;
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
import java.util.UUID;

public class ExercicioEditActivity extends AppCompatActivity {

    private EditText exercicioEditNome;
    private ImageView exercicioEditImage;
    private EditText exercicioEditObservacoes;
    private Button exercicioEditButton;
    private Exercicio exercicio;
    private String docID, imageURI;
    private int positionExercicio;

    private List<Categorias> objectsCategoria;
    private List<Categorias> objectsDificuldade;
    private AutoCompleteTextView exercicioAddCategoria;
    private AutoCompleteTextView exercicioAddDificuldade;
    private TextInputLayout exercicioAddCategoria1;
    private TextInputLayout exercicioAddDificuldade1;
    private CategoriasAdapter arrayAdapterCategoria;
    private DificuldadesAdapter arrayAdapterDificuldadade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercicio);

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

        exercicioEditImage.setOnClickListener(v -> getImageInPhone());
        exercicioEditButton.setOnClickListener(v -> updateExercicio());
    }

    private void init() {
        exercicioEditNome = findViewById(R.id.exercicioEditNome);
        exercicioEditImage = findViewById(R.id.exercicioEditImage);
        exercicioEditObservacoes = findViewById(R.id.exercicioEditDescricao);
        exercicioEditButton = findViewById(R.id.exercicioEditButton);
        exercicioAddCategoria = findViewById(R.id.exercicioEditCategoria);
        exercicioAddDificuldade = findViewById(R.id.exercicioEditDificuldades);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        exercicio = (Exercicio) bundle.getSerializable("exercicio");
        docID = (String) bundle.getSerializable("docID");
        positionExercicio = Integer.parseInt(bundle.getSerializable("positionExercicio") + "");
        exercicioEditNome.setText(exercicio.getNumber() + "");
        exercicioEditObservacoes.setText(exercicio.getObservacoes() + "");
        exercicioAddCategoria.setText(exercicio.getCategoria() + "");
        exercicioAddDificuldade.setText(exercicio.getDificuldade() + "");





        imageURI = exercicio.getImage() + "";
        loadImage();


        exercicioAddCategoria = findViewById(R.id.exercicioEditCategoria);
        exercicioAddDificuldade = findViewById(R.id.exercicioEditDificuldades);

        exercicioAddCategoria1 = findViewById(R.id.exercicioEditCategoria1);
        exercicioAddDificuldade1 = findViewById(R.id.exercicioEditDificuldades1);

        String[] a = getResources().getStringArray(R.array.categorias);
        String[] b = getResources().getStringArray(R.array.difi);

        objectsCategoria = new ArrayList<Categorias>();
        objectsCategoria.add(new Categorias( "Aerobico", R.drawable.human));
        objectsCategoria.add(new Categorias( "Meditação", R.drawable.ic_baseline_lock_24));
        objectsCategoria.add(new Categorias( "Hipertrofia", R.drawable.ic_baseline_edit_24));
        objectsCategoria.add(new Categorias( "Musculação", R.drawable.ic_baseline_title_24));


        objectsDificuldade = new ArrayList<Categorias>();
        objectsDificuldade.add(new Categorias( "Facil", R.drawable.human_handsdown));
        objectsDificuldade.add(new Categorias( "Media", R.drawable.human));
        objectsDificuldade.add(new Categorias( "Dificil", R.drawable.human_handsup));



        arrayAdapterCategoria = new CategoriasAdapter(this, R.layout.dropdown_item,   a );

        arrayAdapterDificuldadade = new DificuldadesAdapter(this, R.layout.drop_down_item_2,   b );

        exercicioAddCategoria1 = findViewById(R.id.exercicioEditCategoria1);
        exercicioAddDificuldade1 = findViewById(R.id.exercicioEditDificuldades1);

        exercicioAddCategoria.setAdapter(arrayAdapterCategoria);
        exercicioAddDificuldade.setAdapter(arrayAdapterDificuldadade);

exercicioAddDificuldade.setHint(exercicio.getDificuldade());
exercicioAddCategoria.setHint(exercicio.getCategoria());

        //exercicioAddCategoria.setText(exercicioAddCategoria.getAdapter().getItem(setAutoCompleteTextSelection(exercicio.getCategoria())).toString(), false);
       // exercicioAddDificuldade.setText(exercicioAddDificuldade.getAdapter().getItem(setAutoCompleteTextSelection(exercicio.getDificuldade())).toString(), false);

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

        String categoria = exercicioAddCategoria.getText().toString();
        String dificuldade = exercicioAddDificuldade.getText().toString();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();
        if (nome.isEmpty()) {
            exercicioEditNome.setError("Por favor escreva um nome para o exercicio");
            exercicioEditNome.requestFocus();
        } else if (observacoes.isEmpty()) {
            exercicioEditObservacoes.setError("Por favor escreva alguma observação");
            exercicioEditObservacoes.requestFocus();
        } else {
            updateExercicioInFirebase(userID, docID, positionExercicio, nome, imageURI + "", observacoes, categoria, dificuldade);
        }
    }

    public void updateExercicioInFirebase(String userID, String docID, int positionExercicio, String nome, String image, String observacoes, String categoria, String dificuldade) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection(userID).document(docID);
        documentReference.update("exercicios." + positionExercicio + ".nome", nome,
                "exercicios." + positionExercicio + ".image", image,
                "exercicios." + positionExercicio + ".observacoes", observacoes, "exercicios." + positionExercicio + ".categoria", categoria, "exercicios." + positionExercicio + ".dificuldade", dificuldade)
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

    public int setAutoCompleteTextSelection(String eval){
        if(eval == "Aerobico")  return 0;
        else if(eval == "Meditação")  return 1;
        else if(eval == "Hipertrofia")  return 2;
        else if(eval == "Musculação")  return 3;

        else if(eval == "Fácil")  return 0;
        else if(eval == "Media")  return 1;
        else return 2;

        //(eval == "Difícil")
    }

}