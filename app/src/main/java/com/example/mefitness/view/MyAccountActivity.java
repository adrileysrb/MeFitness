package com.example.mefitness.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mefitness.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.UUID;

public class MyAccountActivity extends AppCompatActivity {
    ImageView imageUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        imageUser = findViewById(R.id.my_account_user_image);
        EditText nameUser = findViewById(R.id.my_account_user_name);
        TextView emailUser = findViewById(R.id.my_account_user_email);
        MaterialButton buttonSave = findViewById(R.id.my_account_save_button);


        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageInPhone();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("AppSettingPref", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name"+FirebaseAuth.getInstance().getUid(), nameUser.getText().toString());
                // editor.putString("image", imageURI);
                //editor.putString("email"+FirebaseAuth.getInstance().getUid(), emailUser.getText().toString());
                editor.apply();
                startActivity(new Intent(MyAccountActivity.this, MainActivity.class));
                finish();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("AppSettingPref", 0);
        nameUser.setText(sharedPreferences.getString("name"+FirebaseAuth.getInstance().getUid(), "Usuário"));
        emailUser.setText(sharedPreferences.getString("email"+FirebaseAuth.getInstance().getUid(), "usuario@email.com"));

        RequestOptions options = new RequestOptions()
                .error(R.drawable.ic_baseline_person_24)
                .circleCrop();
        Glide.with(this).load(sharedPreferences.getString("image"+ FirebaseAuth.getInstance().getUid(), "1")).apply(options).into(imageUser);
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
            uploadPicture(MyAccountActivity.this, FirebaseStorage.getInstance().getReference(), data.getData(), imageUser);
        }
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

            SharedPreferences sharedPreferences = getSharedPreferences("AppSettingPref", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("image"+FirebaseAuth.getInstance().getUid(), imageURI);
            editor.apply();
        }).addOnFailureListener(exception -> imageURI = "1");
    }

    private String imageURI = "1";
    public void nameEmailImage(){
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettingPref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", "Adriley");
        // editor.putString("image", imageURI);
        editor.putString("email", "adrileysrb@gmail.com");
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MyAccountActivity.this, MainActivity.class));
        finish();
    }
}