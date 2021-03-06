package com.Vlad.starfinder.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Vlad.starfinder.R;
import com.Vlad.starfinder.bluetooth.ConnectThread;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class StarsListAdapter extends RecyclerView.Adapter<StarsListAdapter.MyViewHolder> {

    private static final String TAG = "MyLog";
    private Context context;
    private ArrayList stars_id, stars_name, stars_ascension, stars_declination;

    public StarsListAdapter(Context context, ArrayList stars_id, ArrayList stars_name,
                     ArrayList stars_ascension, ArrayList stars_declination){
        this.context = context;
        this.stars_id = stars_id;
        this.stars_name = stars_name;
        this.stars_ascension = stars_ascension;
        this.stars_declination = stars_declination;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.star_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.star_order.setText(String.valueOf((Integer) stars_id.get(position) + 1));
        holder.star_name.setText(String.valueOf(stars_name.get(position)));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = Integer.parseInt(String.valueOf(holder.star_order.getText())) - 1;

                @SuppressLint({"NewApi", "LocalSuppress"})
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                @SuppressLint({"NewApi", "LocalSuppress"})
                LocalDateTime dateTime1 = LocalDateTime.parse("2021-05-29 00:00:00", formatter);

                @SuppressLint({"NewApi", "LocalSuppress"})
                LocalDateTime dateNow = LocalDateTime.now();

                @SuppressLint({"NewApi", "LocalSuppress"})
                long diffInSeconds = java.time.Duration.between(dateTime1, dateNow).getSeconds();

                Toast.makeText(context, stars_ascension.get(position) + ", "
                        + stars_declination.get(position) + " ???????????????? ?? "
                        + stars_name.get(position) + "!", Toast.LENGTH_LONG).show();
                ConnectThread.sendMessage(String.valueOf(stars_ascension.get(position)));
                ConnectThread.sendMessage("+");
                ConnectThread.sendMessage(String.valueOf(stars_declination.get(position)));
                ConnectThread.sendMessage("+");
                ConnectThread.sendMessage(String.valueOf(diffInSeconds));
                ConnectThread.sendMessage("+");

                Log.d(TAG, "Right ascension: " + String.valueOf(stars_ascension.get(position)));
                Log.d(TAG, "Declination: " + String.valueOf(stars_declination.get(position)));
                Log.d(TAG, "Date: " +  dateNow);
                Log.d(TAG, "?????????????? ??????????????: " + String.valueOf(diffInSeconds));
            }
        });
    }

    @Override
    public int getItemCount() {
        return stars_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView star_order, star_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            star_order = itemView.findViewById(R.id.star_order);
            star_name = itemView.findViewById(R.id.star_name);
        }
    }
}
