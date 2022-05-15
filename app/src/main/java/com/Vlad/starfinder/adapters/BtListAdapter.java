package com.Vlad.starfinder.adapters;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Vlad.starfinder.R;

import java.util.ArrayList;

public class BtListAdapter extends BaseAdapter {

    private static final int RESOURCE_LAYOUT = R.layout.list_item;

    private ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    private LayoutInflater inflater;
    private int iconType;

    public BtListAdapter(Context context, ArrayList<BluetoothDevice> bluetoothDevices, int iconType) {
        this.bluetoothDevices = bluetoothDevices;
        this.iconType = iconType;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return bluetoothDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return getItem(i) ;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    //Инициализация List item (устройства)
    @SuppressLint("MissingPermission")
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = inflater.inflate(RESOURCE_LAYOUT, viewGroup, false);
        BluetoothDevice device = bluetoothDevices.get(i);
        if (device != null){
            ((TextView) view.findViewById(R.id.tv_name)).setText(device.getName());
            ((TextView) view.findViewById(R.id.tv_address)).setText(device.getAddress());
            ((ImageView) view.findViewById(R.id.tv_icon)).setImageResource(iconType);
        }
        return view;
    }
}
