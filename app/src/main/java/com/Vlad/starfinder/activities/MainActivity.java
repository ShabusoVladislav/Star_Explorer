package com.Vlad.starfinder.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.Vlad.starfinder.R;
import com.Vlad.starfinder.adapters.BtListAdapter;
import com.Vlad.starfinder.bluetooth.ConnectThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements
        CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemClickListener,
        View.OnClickListener {

    private static final String TAG = "MyLog";
    private static final int REQ_ENABLE_BT = 10;
    public static final int BT_BOUNDED     = 21;
    public static final int BT_SEARCH     = 22;
    public static final int REQUEST_CODE_LOC = 1;

    private FrameLayout frameMessage;
    private LinearLayout frameControls;

    private Switch switchEnableBt;
    private Button btnEnableSearch;
    private ProgressBar pbProgress;
    private ListView listBtDevices;

    private BluetoothAdapter bluetoothAdapter;
    private IntentFilter filter;
    private BtListAdapter listAdapter;
    private ArrayList<BluetoothDevice> bluetoothDevices;

    private ConnectThread connectThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        //Если Bluetooth не поддерживается
        if (bluetoothAdapter == null){
            Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCreate: " + getString(R.string.bluetooth_not_supported));
            finish();
        }
        //Если Bluetooth уже был включен
        if (bluetoothAdapter != null){
            if(bluetoothAdapter.isEnabled()){
                showFrameControls();
                switchEnableBt.setChecked(true);
                setListAdapter(BT_BOUNDED);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);

        if(connectThread != null){
            ConnectThread.closeConnection();
        }

        if(ConnectThread.getRThread() != null){
            ConnectThread.getRThread().closeConnection();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListAdapter(BT_BOUNDED);
    }

    //Инициализация всех переменных
    private void init(){
        frameMessage = findViewById(R.id.frame_message);
        frameControls = findViewById(R.id.frame_control);

        switchEnableBt = findViewById(R.id.switch_enable_bt);
        btnEnableSearch = findViewById(R.id.btn_enable_search);
        pbProgress = findViewById(R.id.pb_progress);
        listBtDevices = findViewById(R.id.lv_bt_device);

        switchEnableBt.setOnCheckedChangeListener(this);
        btnEnableSearch.setOnClickListener(this);
        listBtDevices.setOnItemClickListener(this);

        bluetoothDevices = new ArrayList<>();

        filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    //Обработчик событий на нажатие на Button
    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: Нажата кнопка");
        if(isGeoDisabled()){
            buildAlertMessageNoLocationService(false);
        }
        if(view.equals(btnEnableSearch)){
            enableSearch();
        }
        showFrameControls();
    }

    //Обработчик событий на клик по устройству из списка
    //Подключение к устройству
    @SuppressLint("MissingPermission")
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.equals(listBtDevices)){
            BluetoothDevice device = bluetoothDevices.get(i);
            if (device != null){
                connectThread = new ConnectThread(this, bluetoothAdapter, device);
                connectThread.start();
            }
        }
    }

    //Обработчик событий на переключателе
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton.equals(switchEnableBt)){
            enableBt(b);
            if(!b){
                showFrameMessage();
            }
        }
    }

    //Отслеживание, разрешили ли мы включить Bluetooth
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_ENABLE_BT){
            if(resultCode == RESULT_OK){
                showFrameControls();
                setListAdapter(BT_BOUNDED);
            } else if (resultCode == RESULT_CANCELED){
                showFrameMessage();
                switchEnableBt.setChecked(false);
            }
        }
    }

    //Показывает сообщение, если Bluetooth выключен
    private void showFrameMessage(){
        frameMessage.setVisibility(View.VISIBLE);
        frameControls.setVisibility(View.GONE);
    }

    //Показываем кнопки для подключения, если Bluetooth включен
    private void showFrameControls(){
        frameMessage.setVisibility(View.GONE);
        frameControls.setVisibility(View.VISIBLE);
    }

    //Функция включения Bluetooth-a
    @SuppressLint("MissingPermission")
    private void enableBt(boolean flag){
        if (flag) {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //Передаем ENABLE_REQUEST для отслеживания именно Bluetooth-а
            startActivityForResult(i, REQ_ENABLE_BT);
        } else {
            bluetoothAdapter.disable();
        }
    }

    public void setListAdapter(int type){

        bluetoothDevices.clear();
        int iconType = R.drawable.ic_bluetooth_bounded_device;

        switch (type) {
            case BT_BOUNDED:
                bluetoothDevices = getBoundedBtDevices();
                iconType = R.drawable.ic_bluetooth_bounded_device;
                listAdapter = new BtListAdapter(this, bluetoothDevices, iconType);
                break;
            case BT_SEARCH:
                iconType = R.drawable.ic_bluetooth_search_device;
                listAdapter = new BtListAdapter(this, bluetoothDevices, iconType);
                break;
        }
        listBtDevices.setAdapter(listAdapter);
    }

    private ArrayList<BluetoothDevice> getBoundedBtDevices(){
        @SuppressLint("MissingPermission") Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
        ArrayList<BluetoothDevice> tmpArrayList = new ArrayList<>();
        if(deviceSet.size() > 0){
            for (BluetoothDevice device: deviceSet) {
                tmpArrayList.add(device);
            }
        }

        return tmpArrayList;
    }

    @SuppressLint("MissingPermission")
    private void enableSearch(){
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "enableSearch: bluetoothAdapter.isDiscovering()");
        } else{
            accessLocationPermission();
            bluetoothAdapter.startDiscovery();
            Log.d(TAG, "enableSearch: !bluetoothAdapter.isDiscovering()");
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            switch (action){
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    btnEnableSearch.setText(R.string.stop_search);
                    pbProgress.setVisibility(View.VISIBLE);
                    setListAdapter(BT_SEARCH);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    btnEnableSearch.setText(R.string.start_search);
                    pbProgress.setVisibility(View.GONE);
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(device != null && !bluetoothDevices.contains(device)){
                        bluetoothDevices.add(device);
                        listAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };


    //Запрос на разрешение данных о местоположении (для Marshmallow 6.0+)
    private void accessLocationPermission() {
        int accessCoarseLocation = this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFineLocation   = this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listRequestPermission = new ArrayList<String>();

        if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listRequestPermission.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (accessFineLocation != PackageManager.PERMISSION_GRANTED) {
            listRequestPermission.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listRequestPermission.isEmpty()) {
            String[] strRequestPermission = listRequestPermission.toArray(new String[listRequestPermission.size()]);
            this.requestPermissions(strRequestPermission, REQUEST_CODE_LOC);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_LOC:
                if (grantResults.length > 0) {
                    for (int gr : grantResults) {
                        if (gr != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                }
                break;
            default:
                return;
        }
    }

    public boolean isGeoDisabled() {
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean mIsGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean mIsNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean mIsGeoDisabled = !mIsGPSEnabled && !mIsNetworkEnabled;
        return mIsGeoDisabled;
    }

    //Диалоговое окно для включения GPS
    private boolean buildAlertMessageNoLocationService(boolean network_enabled) {
        String title = getResources().getString(R.string.title_switch_network);
        String msg = getString(R.string.msg_switch_network);
        if (msg != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title)
                    .setMessage(msg)
                    .setPositiveButton("Включить", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity.this, "Отмена включения GPS", Toast.LENGTH_SHORT).show();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.secondaryDarkColor));
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.secondaryDarkColor));
            return true;
        }
        return false;
    }
}