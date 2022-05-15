package com.Vlad.starfinder.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.Vlad.starfinder.MenuActivity;
import com.Vlad.starfinder.R;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ConnectThread extends Thread{
    private static final String TAG = "MyLog";
    private Context context;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice device;
    private static BluetoothSocket btSocket;
    public static final String UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private boolean success = false;
    private static ReceiveThread rThread;

    @SuppressLint("MissingPermission")
    public ConnectThread(Context context, BluetoothAdapter btAdapter, BluetoothDevice device) {
        this.context = context;
        this.btAdapter = btAdapter;
        this.device = device;
        try{
            btSocket = device.createRfcommSocketToServiceRecord(java.util.UUID.fromString(UUID));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        btAdapter.cancelDiscovery();
        try {
            btSocket.connect();
            success = true;
            rThread = new ReceiveThread(btSocket);
            rThread.start();
        } catch (IOException e) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, context.getString(R.string.error_connect) + device.getName(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, context.getString(R.string.error_connect) + device.getName());
                }
            });

            closeConnection();
        }

        if(success){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, context.getString(R.string.successfully_connected) + device.getName());
                    Intent i = new Intent(context, MenuActivity.class);
                    i.putExtra("device", ConnectThread.this.device);
                    context.startActivity(i);
                    Toast.makeText(context, context.getString(R.string.successfully_connected) + device.getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void closeConnection(){
        try {
            btSocket.close();
            Log.d(TAG, "Соединение разорвано");
        } catch (IOException ee){
            Log.d(TAG, "Не удалось разорвать соединение");
        }
    }

    public static ReceiveThread getRThread() {
        return rThread;
    }

    public static void sendMessage(String message){
        rThread.sendMessage(message.getBytes(StandardCharsets.UTF_8));
    }
}
