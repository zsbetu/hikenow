package com.example.hikenow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TripAdapter  extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    private List<Trip> tripList;

    public TripAdapter(List<Trip> tripList){
        this.tripList = tripList;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_card, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TripViewHolder holder, int position){
        Trip trip = tripList.get(position);
        holder.title.setText(trip.getTitle());
        holder.description.setText(trip.getDescription());
        holder.difficulty.setText(trip.getDifficulty());
        holder.image.setImageResource(trip.getImageResId());
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, difficulty;
        ImageView image;

        TripViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.trip_title);
            description = itemView.findViewById(R.id.trip_description);
            difficulty = itemView.findViewById(R.id.trip_difficulty);
            image = itemView.findViewById(R.id.trip_image);
        }
    }
}
