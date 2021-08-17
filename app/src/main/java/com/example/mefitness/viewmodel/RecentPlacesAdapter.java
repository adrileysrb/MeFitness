package com.example.mefitness.viewmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mefitness.R;
import com.example.mefitness.model.RecentPlace;

import java.util.ArrayList;

public class RecentPlacesAdapter extends RecyclerView.Adapter<RecentPlacesAdapter.RecentPlacesViewHolder> {

    private
    CustomBottomDialog
            customBottomDialog;
    Context context;
    private ArrayList<RecentPlace> recentPlaces;
    private RecentPlacesAdapterListener listener;

    public RecentPlacesAdapter(
            CustomBottomDialog
                    customBottomDialog, Context context, ArrayList<RecentPlace> recentPlaces) {
        this.context = context;
        this.customBottomDialog = customBottomDialog;
        this.recentPlaces = recentPlaces;
        listener = (RecentPlacesAdapterListener) customBottomDialog;
    }

    @NonNull
    @Override
    public RecentPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.item_recent_local, parent, false);
        return new RecentPlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentPlacesViewHolder holder, int position) {
        holder.textView.setText(recentPlaces.get(holder.getAdapterPosition()).getName());
        holder.linearLayout.setOnClickListener(v->{
            listener.position(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return recentPlaces.size();
    }

    public class RecentPlacesViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        LinearLayout linearLayout;
        public RecentPlacesViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView_item_recent_local);
            linearLayout = itemView.findViewById(R.id.item_recent_local);
        }
    }

    public interface RecentPlacesAdapterListener{
        void position(int position);
    }


}
