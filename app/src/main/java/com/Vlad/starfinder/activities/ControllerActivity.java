package com.Vlad.starfinder.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.Vlad.starfinder.R;
import com.Vlad.starfinder.bluetooth.ConnectThread;

public class ControllerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MyLog";

    private ActionBar ab;

    private Button btnPipeUp;
    private Button btnPipeDown;
    private Button btnPipeLeft;
    private Button btnPipeRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

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
        ab.setTitle(R.string.hand_controller);

        btnPipeUp = findViewById(R.id.btn_pipe_up);
        btnPipeDown = findViewById(R.id.btn_pipe_down);
        btnPipeLeft = findViewById(R.id.btn_pipe_left);
        btnPipeRight = findViewById(R.id.btn_pipe_right);

        btnPipeUp.setOnClickListener(this);
        btnPipeDown.setOnClickListener(this);
        btnPipeLeft.setOnClickListener(this);
        btnPipeRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.equals(btnPipeUp)){
            ConnectThread.sendMessage("1+");
            Log.d(TAG, "UP");
        }
        else if(view.equals(btnPipeDown)){
            ConnectThread.sendMessage("2+");
            Log.d(TAG, "DOWN");
        }else if(view.equals(btnPipeLeft)){
            ConnectThread.sendMessage("3+");
            Log.d(TAG, "LEFT");
        }else if(view.equals(btnPipeRight)){
            ConnectThread.sendMessage("4+");
            Log.d(TAG, "RIGHT");
        }
    }
}