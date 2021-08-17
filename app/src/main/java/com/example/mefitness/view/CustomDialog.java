package com.example.mefitness.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class CustomDialog extends AppCompatDialogFragment {

    private EditText editTextSeconds, editTextMinutes;
    private CustomDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog, null);

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
                    String minutes = editTextMinutes.getText().toString();
                    String seconds = editTextSeconds.getText().toString();
                    listener.appyTexts(minutes, seconds);

                });

        editTextMinutes = view.findViewById(R.id.edit_minutes);
        editTextSeconds = view.findViewById(R.id.edit_seconds);
        return  builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (CustomDialogListener) context;
        } catch (ClassCastException e){
            throw  new ClassCastException(context.toString() + "Must Implement Custom Dialog Listener");
        }

    }

    public interface CustomDialogListener{
        void appyTexts(String minutes, String seconds);
    }
}
