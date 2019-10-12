package com.example.settingsdemo.bluetooth.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.settingsdemo.R;
import com.example.settingsdemo.bluetooth.util.BluetoothUtil;

/**
 * 修改蓝牙名
 */

public class NameActivity extends AppCompatActivity {

    EditText editText;

    String mDevicename="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_name);

        editText = (EditText) findViewById(R.id.name);
        if (getIntent()!=null){
            editText.setText(mDevicename=getIntent().getStringExtra("name"));
        }



        Button button = (Button) findViewById(R.id.ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothUtil.mBluetoothAdapter().setName(mDevicename);
                finish();
            }
        });
    }
}
