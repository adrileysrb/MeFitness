package com.example.mefitness.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mefitness.R;

public class FragmentTools extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
     View view =  inflater.inflate(R.layout.fragment_tools, container, false);

        CardView stopwatch = view.findViewById(R.id.cardView_stopwatch);
        CardView timer = view.findViewById(R.id.cardView_timer);
        CardView imc = view.findViewById(R.id.cardView_imc);

        stopwatch.setOnClickListener(v -> {
            getActivity().startActivity(new Intent(getActivity(), StopwatchActivity.class));
        });

        timer.setOnClickListener(v -> {
            getActivity().startActivity(new Intent(getActivity(), TimerActivity.class));
        });

        imc.setOnClickListener(v -> {
            getActivity().startActivity(new Intent(getActivity(), MapsActivity.class));
        });

     return view;
    }
}