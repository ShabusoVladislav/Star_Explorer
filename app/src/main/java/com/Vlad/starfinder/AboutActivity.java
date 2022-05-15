package com.Vlad.starfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private ActionBar ab;

    private Button btn_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        init();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void init(){
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.about);

        btn_code = findViewById(R.id.btn_code);

        btn_code.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.equals(btn_code)){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ShabusoVladislav/Star_Explorer/tree/main"));
            startActivity(intent);
        }
    }
}