package com.example.mefitness.viewmodel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.SimpleColorFilter;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieValueCallback;
import com.example.mefitness.R;

public class CustomDialogLatLon extends AppCompatDialogFragment {

    private EditText editTextLongitude, editTextLatitude;
    private CustomDialogListenerLatLon listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog_lat_lon, null);

        LottieAnimationView animationView2 = view.findViewById(R.id.lottie_custom_dialog);
        int yourColor = ContextCompat.getColor(getActivity(), R.color.primaryDarkColor);

        SimpleColorFilter filter = new SimpleColorFilter(yourColor);
        KeyPath keyPath = new KeyPath("**");
        LottieValueCallback<ColorFilter> callback = new LottieValueCallback<ColorFilter>(filter);
        animationView2.addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback);

        builder.setView(view)
                //    .setTitle("Digite o tempo")
                //  .setIcon(R.drawable.ic_baseline_add_reaction_24)
                .setNegativeButton("Cancel", (dialog, which) -> {

                })
                .setPositiveButton("Ok", (dialog, which) -> {
                    String minutes = editTextLatitude.getText().toString();
                    String seconds = editTextLongitude.getText().toString();
                    listener.apply(minutes, seconds);

                });

        editTextLatitude = view.findViewById(R.id.edit_lat);
        editTextLongitude = view.findViewById(R.id.edit_lon);
        return  builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (CustomDialogListenerLatLon) context;
        } catch (ClassCastException e){
            throw  new ClassCastException(context.toString() + "Must Implement Custom Dialog Listener");
        }

    }

    public interface CustomDialogListenerLatLon {
        void apply(String lat, String lon);
    }
}
