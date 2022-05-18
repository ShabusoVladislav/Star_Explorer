package com.Vlad.starfinder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.Vlad.starfinder.R;
import com.Vlad.starfinder.bluetooth.ConnectThread;
import com.Vlad.starfinder.bluetooth.ReceiveThread;

public class MenuActivity extends AppCompatActivity implements
        View.OnClickListener{

    private static final String TAG = "MyLog";

    private Button btnController;
    private Button btnAbout;
    private Button btnDisconnect;
    private Button btnStars;

    private BluetoothDevice device;

    private ReceiveThread rThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        init();
    }

    @SuppressLint("MissingPermission")
    private void init(){
        btnController = findViewById(R.id.btn_controller);
        btnAbout = findViewById(R.id.btn_about);
        btnDisconnect = findViewById(R.id.btn_disconnect_menu);
        btnStars = findViewById(R.id.btn_stars);

        btnController.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
        btnDisconnect.setOnClickListener(this);
        btnStars.setOnClickListener(this);

        device = getIntent().getExtras().getParcelable("device");

        rThread = ConnectThread.getRThread();
        Log.d(TAG, "rThread: " + rThread.toString());
    }


    @Override
    public void onClick(View view){
        if(view.equals(btnController)){
            Intent i = new Intent(MenuActivity.this, ControllerActivity.class);
            startActivity(i);
        } else if (view.equals(btnDisconnect)){
            if(device != null){
                ConnectThread.closeConnection();
                finish();
                Toast.makeText(this, "Соединение разорвано", Toast.LENGTH_SHORT).show();
            }
        } else if (view.equals(btnAbout)){
            Intent i = new Intent(MenuActivity.this, AboutActivity.class);
            startActivity(i);
        } else if (view.equals(btnStars)){
            Intent i = new Intent(MenuActivity.this, StarsActivity.class);
            startActivity(i);
        }
    }
}