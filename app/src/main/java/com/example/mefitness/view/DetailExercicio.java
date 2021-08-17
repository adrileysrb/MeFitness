package com.example.mefitness.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mefitness.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailExercicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_exercicio);

        SharedPreferences sharedPreferences = getSharedPreferences("AppSettingPref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean isNightMode = sharedPreferences.getBoolean("NightMode", false);

        if(isNightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String image = (String) bundle.getSerializable("image");
        String nome = (String) bundle.getSerializable("nome");
        String observacoes = (String) bundle.getSerializable("observacoes");
        String categoria = (String) bundle.getSerializable("categoria");
        String dificuldades = (String) bundle.getSerializable("dificuldade");

        TextView categoriaa = findViewById(R.id.detalhe_exercicio_categoria);
        TextView dificuldadee = findViewById(R.id.detalhe_exercicio_dificuldade);
        TextView observacoess = findViewById(R.id.detalhe_exercicio_observacoes);

        ImageView imageView = findViewById(R.id.second_activity_imageView);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);

        //RequestOptions options = new RequestOptions().error(R.drawable.ic_baseline_directions_run_24);
        Glide.with(this).load(image)
                //.apply(options)
                .into(imageView);

        categoriaa.setText(categoria);
        dificuldadee.setText(dificuldades);
        observacoess.setText(observacoes);

        collapsingToolbarLayout.setTitle(nome);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getColor(R.color.white));



    }
}