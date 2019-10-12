package com.example.settingsdemo;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import com.example.settingsdemo.bluetooth.activity.SubBluetoothActivity;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    Button btn_usermode;
    Button btn_sub_wifi;
    Button btn_sub_bluetooth;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_usermode = (Button) findViewById(R.id.btn_user_mode);
        btn_usermode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentUserMode;
                intentUserMode = new Intent(MainActivity.this, UserModeActivity.class);
                startActivity(intentUserMode);
            }
        });

        btn_sub_wifi= (Button) findViewById(R.id.btn_wifi);
        btn_sub_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent= new Intent(MainActivity.this, SubWifiActivity.class);
                startActivity(intent);
            }
        });
        btn_sub_bluetooth= (Button) findViewById(R.id.btn_bluetooth);
        btn_sub_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent= new Intent(MainActivity.this, SubBluetoothActivity.class);
                startActivity(intent);
            }
        });
    }


}
