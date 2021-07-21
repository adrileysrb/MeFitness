package com.example.mefitness.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mefitness.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class DetailExercicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_exercicio);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String image = (String) bundle.getSerializable("image");
        String nome = (String) bundle.getSerializable("nome");
        String observacoes = (String) bundle.getSerializable("observacoes");

        TextView textView = findViewById(R.id.second_activity_textView);
        ImageView imageView = findViewById(R.id.second_activity_imageView);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);

        //RequestOptions options = new RequestOptions().error(R.drawable.ic_baseline_directions_run_24);
        Glide.with(this).load(image)
                //.apply(options)
                .into(imageView);

        textView.setText(observacoes);
        textView.setTextColor(getColor(R.color.black));
        collapsingToolbarLayout.setTitle(nome);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getColor(R.color.white));
    }
}