package com.Vlad.starfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.Vlad.starfinder.adapters.StarsListAdapter;
import com.Vlad.starfinder.database.DatabaseHelper;

import java.util.ArrayList;

public class StarsActivity extends AppCompatActivity {

    private static final String TAG = "MyLog";

    private ActionBar ab;
    private TextView stars_count;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    Cursor myCursor;

    ArrayList<Integer> stars_id;
    ArrayList<String> stars_name;
    ArrayList<Double> stars_ascension;
    ArrayList<Double> stars_declination;

    RecyclerView recyclerView;
    StarsListAdapter starsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stars);

        init();

        databaseHelper.createDB();

        starsListAdapter = new StarsListAdapter(this, stars_id, stars_name, stars_ascension,
                stars_declination);
        recyclerView.setAdapter(starsListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        readData();
    }

    private void init(){
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.list_of_stars);

        stars_count = findViewById(R.id.stars_count);

        stars_id = new ArrayList<>();
        stars_name = new ArrayList<>();
        stars_ascension = new ArrayList<>();
        stars_declination = new ArrayList<>();

        recyclerView = findViewById(R.id.list_of_stars);

        databaseHelper = new DatabaseHelper(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    private void readData(){
        db = databaseHelper.open();
        Log.d(TAG, "DB: " + db.toString());
        // Считываем данные из базы
        myCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME, null);

        if(myCursor.getCount() == 0){
            Toast.makeText(this, "Нет данных о звездах", Toast.LENGTH_SHORT).show();
        } else {

            while(myCursor.moveToNext()){
                stars_id.add(myCursor.getInt(0));
                stars_name.add(String.join(" ", myCursor.getString(1).split("_")));
                stars_ascension.add(myCursor.getDouble(2));
                stars_declination.add(myCursor.getDouble(3));
            }
            stars_count.setText("Всего звезд: " + stars_id.size());
        }

    }
}