package com.example.settingsdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;


public class AboutActivity extends AppCompatActivity {
    private TextView Tv_SerialNumber;
    private TextView Tv_SystemVersion;
    private Button Btn_AdminMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        showSerialNumber();
        showSystemVersion();
        showRealSettings();
    }

    private static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    private void showSerialNumber() {
        String sn = getSerialNumber();
        Tv_SerialNumber = (TextView) findViewById(R.id.textView_serial_number);
        Tv_SerialNumber.setText(getResources().getString(R.string.str_dev_sn) + ": " + sn);
    }

    private void showSystemVersion() {
        String ver = "1.0.0";
        Tv_SystemVersion = (TextView) findViewById(R.id.textView_system_version);
        Tv_SystemVersion.setText(getResources().getString(R.string.str_dev_system_version) + ": " + ver);
    }

    private void showRealSettings() {
        Btn_AdminMode = (Button) findViewById(R.id.btn_admin_mode);

        Btn_AdminMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                 */
                showAdminMode();
            }
        });

    }

    private void showAdminMode() {
        final EditText inputServer = new EditText(AboutActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
        builder.setTitle("输入密码").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = inputServer.getText().toString();
                Log.d("hf", text);
                if(text.equals("1234")){
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                }else{
                    Toast.makeText(AboutActivity.this, "Password Incorect!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();

    }

}
