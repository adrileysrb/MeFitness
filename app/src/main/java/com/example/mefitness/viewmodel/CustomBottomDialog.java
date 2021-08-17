package com.example.mefitness.viewmodel;


import android.app.Dialog;
import android.content.Context;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.SimpleColorFilter;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieValueCallback;
import com.example.mefitness.R;
import com.example.mefitness.model.RecentPlace;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class CustomBottomDialog extends BottomSheetDialogFragment implements RecentPlacesAdapter.RecentPlacesAdapterListener {
    Context context;
    ArrayList<RecentPlace> recentPlaces;
    private CustomBottomDialogListener listener;
    private Dialog dialog;

    /*public static CustomBottomDialog newInstance(Context context, ArrayList<RecentPlace> recentPlaces) {
        CustomBottomDialog fragment = new CustomBottomDialog();
        this.recentPlaces = recentPlaces;
        return fragment;
    }*/

    public CustomBottomDialog (Context context, ArrayList<RecentPlace> recentPlaces) {
        this.recentPlaces = recentPlaces;
        this.context = context;
        listener = (CustomBottomDialogListener) context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.custom_bottom_sheet_dialog, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        ImageView imageView = contentView.findViewById(R.id.custom_button_image);
        if(recentPlaces.isEmpty()) imageView.setVisibility(ImageView.VISIBLE);

       /* recentPlaces.add(new RecentPlace("Montevidiu do Norte", "1", "1"));
        recentPlaces.add(new RecentPlace("Urutai", "2", "4"));
        recentPlaces.add(new RecentPlace("Anápolis", "7", "1"));*/

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        RecentPlacesAdapter recentPlacesAdapter = new RecentPlacesAdapter(this,getContext(), recentPlaces);

        RecyclerView recyclerView = contentView.findViewById(R.id.rv_custom_dialog);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recentPlacesAdapter);
        //recentPlacesAdapter.notifyDataSetChanged();

        LottieAnimationView animationView2 = contentView.findViewById(R.id.lottie_custom_bottom_sheet_dialog);
        int yourColor = ContextCompat.getColor(getActivity(), R.color.primaryDarkColor);

        SimpleColorFilter filter = new SimpleColorFilter(yourColor);
        KeyPath keyPath = new KeyPath("**");
        LottieValueCallback<ColorFilter> callback = new LottieValueCallback<ColorFilter>(filter);
        animationView2.addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback);
        this.dialog = dialog;

    }

    @Override
    public void position(int position) {
        RecentPlace recentPlace = recentPlaces.get(position);
        listener.appyTexts(recentPlace.getLat(),recentPlace.getLon());
        dialog.dismiss();
    }



    public interface CustomBottomDialogListener{
        void appyTexts(String minutes, String seconds);
    }
}
