package com.example.mefitness.viewmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mefitness.R;
import com.example.mefitness.model.Categorias;
import com.example.mefitness.view.ExercicioAddActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class DificuldadesAdapter extends ArrayAdapter<String> {
    Context context;
    int resource;
    private String[] objects;

    public DificuldadesAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }


    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(resource, parent, false);

        List<Categorias> ob = new ArrayList<>();
        ob.add(new Categorias( "Fácil", R.drawable.human_handsdown));
        ob.add(new Categorias( "Media", R.drawable.human));
        ob.add(new Categorias( "Difícil", R.drawable.human_handsup));

        TextView textView = view.findViewById(R.id.dropText2);
        ImageView imageView = view.findViewById(R.id.dropImage2);

        textView.setText(objects[position]);
        imageView.setImageResource(ob.get(position).getImage());

        return view;
    }



}
