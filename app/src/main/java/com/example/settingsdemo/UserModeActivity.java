package com.example.settingsdemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserModeActivity extends AppCompatActivity {
    private String TAG = "UserModeActivity";
    private WifiManager wifiManager;
    private Switch switch_wifi, switch_bt;
    private Button btn_about;
    private TextView textView_Brightness, textView_AudioMedia, textView_AudioRing, textView_AudioAlarm;
    private SeekBar seekBar_Brightness, seekBar_AudioMedia, seekBar_AudioRing, seekBar_AudioAlarm;
    private AudioManager am;
    private String strStreamType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usermode);
        initView();//控件初始化
        InitDataWifi();
        InitDataBT();
        InitDataAbout();
        showSeekBarBrightness();
        showSeekbars();
    }

    private void initView() {
        switch_wifi = (Switch) findViewById(R.id.switch_wifi);
        switch_bt = (Switch) findViewById(R.id.switch_bluetooth);
        btn_about = (Button) findViewById(R.id.btn_about);
        seekBar_Brightness = (SeekBar) findViewById(R.id.seekbar_brightness);
        seekBar_AudioMedia = (SeekBar) findViewById(R.id.seekbar_audio_media);
        seekBar_AudioRing = (SeekBar) findViewById(R.id.seekbar_audio_ring);
        seekBar_AudioAlarm = (SeekBar) findViewById(R.id.seekbar_audio_alarm);
        textView_Brightness = (TextView) findViewById(R.id.textview_brightness_value);
        textView_AudioMedia = (TextView) findViewById(R.id.textview_audio_media);
        textView_AudioRing = (TextView) findViewById(R.id.textview_audio_ring);
        textView_AudioAlarm = (TextView) findViewById(R.id.textview_audio_alarm);
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    private void InitDataWifi() {
        //获取wifi管理服务
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        int state = wifiManager.getWifiState();
        switch_wifi.setChecked(state == WifiManager.WIFI_STATE_ENABLED ? true : false);

        switch_wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int state = wifiManager.getWifiState();
                if (buttonView.isPressed()) {
                    //Log.d(TAG, "isChecked = " + isChecked);
                    if (isChecked) {
                        if (state == WifiManager.WIFI_STATE_DISABLED) {
                            //关闭状态则打开
                            wifiManager.setWifiEnabled(true);
                        }
                    } else {
                        if (state == WifiManager.WIFI_STATE_ENABLED) {
                            //wifi打开状态则关闭
                            wifiManager.setWifiEnabled(false);
                        }
                    }
                    return;
                }
//                if (!buttonView.isPressed()) { // 每次 setChecked 时会触发onCheckedChanged 监听回调，而有时我们在设置setChecked后不想去自动触发 onCheckedChanged 里的具体操作, 即想屏蔽掉onCheckedChanged;加上此判断
//                    Log.d(TAG, "btn is NOT pressed ** isChecked = " + isChecked);
//                    return;
//                }
            }

        });
    }

    private void InitDataBT() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "This device not support Bluetooth!", Toast.LENGTH_SHORT).show();
        }
        switch_bt.setChecked(bluetoothAdapter.getState() == bluetoothAdapter.STATE_ON ? true : false);
        switch_bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                int state = bluetoothAdapter.getState();
                Log.d(TAG, "state = " + state);
                if (buttonView.isPressed()) {
                    Log.d(TAG, "isChecked = " + isChecked);
                    if (isChecked) {
                        if (state == bluetoothAdapter.STATE_OFF) {
                            //关闭状态则打开
                            bluetoothAdapter.enable();
                            Log.d(TAG, "then state = " + state);
                        }
                    } else {
                        if (state == bluetoothAdapter.STATE_ON) {
                            //打开状态则关闭
                            bluetoothAdapter.disable();
                            Log.d(TAG, "then2 state = " + state);
                        }
                    }
                    return;
                }
            }

        });

    }

    private void getBTState(BluetoothAdapter bluetoothAdapter) {
        int state = bluetoothAdapter.getState();
        switch (state) {
            case BluetoothAdapter.STATE_ON:
                Toast.makeText(this, "蓝牙已经打开", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                Toast.makeText(this, "蓝牙正在打开。。", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                Toast.makeText(this, "蓝牙正在关闭。。", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_OFF:
                Toast.makeText(this, "蓝牙已经关闭", Toast.LENGTH_SHORT).show();
                break;


        }
    }

    private String getBTname(BluetoothAdapter bluetoothAdapter) {
        String name = bluetoothAdapter.getName();
        return name;
    }

    private String getBTmac(BluetoothAdapter bluetoothAdapter) {
        String mac = bluetoothAdapter.getAddress();
        return mac;
    }

    private void InitDataAbout() {
        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(UserModeActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

    }

    private void showSeekBarBrightness() {
        int screen_brightness = getScreenBrightness();
        Log.d(TAG, "getCurrentBrightness = " + screen_brightness);
        seekBar_Brightness.setProgress(screen_brightness);
        textView_Brightness.setText("Brightness: " + screen_brightness);

        seekBar_Brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView_Brightness.setText("Brightness: " + progress);

                changeAppBrightness(seekBar.getContext(), progress);
                saveScreenBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });
    }

    /**
     * 获得当前屏幕亮度值 0--255
     */
    private int getScreenBrightness() {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception localException) {

        }
        return screenBrightness;
    }

    public void changeAppBrightness(Context context, int brightness) {
        Window window = ((Activity) context).getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
        }
        window.setAttributes(lp);
    }

    /**
     * 设置当前屏幕亮度值 0--255
     */
    private void saveScreenBrightness(int paramInt) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(getApplicationContext())) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 200);
                } else {

                    Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, paramInt);
                }
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    private void showSeekbars() {
        showAudioView(am, seekBar_AudioMedia, textView_AudioMedia, AudioManager.STREAM_MUSIC);
        showAudioView(am, seekBar_AudioRing, textView_AudioRing, AudioManager.STREAM_RING);
        showAudioView(am, seekBar_AudioAlarm, textView_AudioAlarm, AudioManager.STREAM_ALARM);

    }

    private void showAudioView(final AudioManager am, SeekBar sbar, final TextView tv, final int streamType) {
        //获取系统最大音量
        int maxVolume = am.getStreamMaxVolume(streamType);
        //获取当前音量
        int currentVolume = am.getStreamVolume(streamType);
        sbar.setMax(maxVolume);
        sbar.setProgress(currentVolume);

        switch(streamType)
        {
            case 2:
                strStreamType = "Ring";
                break;
            case 3:
                strStreamType = "Media";
                break;
            case 4:
                strStreamType = "Alarm";
                break;
                default:
                    strStreamType = "audio";
        }
        tv.setText(strStreamType + " Vol: " + currentVolume);

        sbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    //设置系统音量
                    am.setStreamVolume(streamType, progress, 0);
                    int currentVolume = am.getStreamVolume(streamType);
                    seekBar.setProgress(currentVolume);
                    tv.setText(strStreamType +" Vol: " + currentVolume);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


}
