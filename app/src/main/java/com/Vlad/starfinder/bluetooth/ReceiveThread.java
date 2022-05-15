package com.Vlad.starfinder.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReceiveThread extends Thread{
    private static final String TAG = "MyLog";
    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private byte[] buffer;

    public ReceiveThread(BluetoothSocket socket) {
        this.socket = socket;
        try{
            inputStream = socket.getInputStream();
        } catch (IOException e){
            Log.d(TAG, "Ошибка подключения Input Stream");
        }
        try{
            outputStream = socket.getOutputStream();
        } catch (IOException e){
            Log.d(TAG, "Ошибка подключения Output Stream");
        }
    }

    @Override
    public void run() {
        buffer = new byte[255];
        while (true){
            try{
                int size = inputStream.read(buffer);
                String message = new String(buffer,0, size);
                Log.d(TAG, "Message: " + message);
            } catch (IOException e) {
                Log.d(TAG, "Ошибка в while inputStream");
                break;
            }
        }
    }

    public void sendMessage(byte[] byteArr){
        try{
            outputStream.write(byteArr);
        } catch (IOException e){

        }
    }

    public void closeConnection(){
        try {
            inputStream.close();
            outputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
