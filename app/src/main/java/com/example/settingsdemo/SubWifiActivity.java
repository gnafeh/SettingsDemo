package com.example.settingsdemo;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.settingsdemo.wifi.MainAdapter;
import com.example.settingsdemo.wifi.bean.WifiListBean;
import com.example.settingsdemo.wifi.utils.MyWifiManager;
import com.example.settingsdemo.wifi.utils.PermissionsChecker;

import java.util.ArrayList;
import java.util.List;

public class SubWifiActivity extends AppCompatActivity {
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private final int RESULT_CODE_LOCATION = 0x001;
    //定位权限,获取app内常用权限
    String[] permsLocation = {"android.permission.ACCESS_WIFI_STATE"
            , "android.permission.CHANGE_WIFI_STATE"
            , "android.permission.ACCESS_COARSE_LOCATION"
            , "android.permission.ACCESS_FINE_LOCATION"};

    RecyclerView recyclerView;
    Button btnOpenWifi;
    Button btnGetWifi;
    Button btnCloseWifi;
    MainAdapter adapter;
    private WifiManager mWifiManager;
    private List<ScanResult> mScanResultList;//wifi列表
    private List<WifiListBean> wifiListBeanList;
    private Dialog dialog;
    private View inflate;
    private WifiBroadcastReceiver wifiReceiver;
    private TextView tv_wifiState;

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiverWifi();//监听wifi变化
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subwifi);
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        }
        getPerMission();//权限
        initView();//控件初始化
        initClickListener();//获取wifi
        setAdapter();//wifi列表
        openWifi();
        closeWifi();
    }

    //监听wifi变化
    private void registerReceiverWifi() {
        wifiReceiver = new WifiBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//监听wifi是开关变化的状态
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//监听wifi连接状态
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);//监听wifi列表变化（开启一个热点或者关闭一个热点）
        filter.addAction(WifiManager.EXTRA_NEW_RSSI);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        registerReceiver(wifiReceiver, filter);
    }

    //setAdapter
    private void setAdapter() {
        adapter = new MainAdapter(wifiListBeanList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setmOnItemClickListerer(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //连接wifi
                showCentreDialog(wifiListBeanList.get(position).getName(), position);
            }
        });
    }
    private void clearScanList(){
        Log.e("hefang","clearScanList" );
        wifiListBeanList.clear();
        mScanResultList.clear();
        adapter.notifyDataSetChanged();
    }

    //获取权限
    private void getPerMission() {
        mPermissionsChecker = new PermissionsChecker(SubWifiActivity.this);
        if (mPermissionsChecker.lacksPermissions(permsLocation)) {
            ActivityCompat.requestPermissions(SubWifiActivity.this, permsLocation, RESULT_CODE_LOCATION);
        }
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        btnOpenWifi = findViewById(R.id.btnOpen);
        btnGetWifi = findViewById(R.id.btnGetWifi);
        btnCloseWifi = findViewById(R.id.btnClose);
        tv_wifiState = findViewById(R.id.tv_wifiState);
        wifiListBeanList = new ArrayList<>();
        mScanResultList = new ArrayList<>();
    }

    private void initClickListener() {
        //获取wifi列表
        btnGetWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                freshScanList();
                if (wifiListBeanList.size() > 0) {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(SubWifiActivity.this, "获取wifi列表成功", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(SubWifiActivity.this, "wifi列表为空，请检查wifi页面是否有wifi存在", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void freshScanList(){
        clearScanList();

        //开启wifi
        MyWifiManager.openWifi(mWifiManager);
        //获取到wifi列表
        mScanResultList = MyWifiManager.getWifiList(mWifiManager);
        for (int i = 0; i < mScanResultList.size(); i++) {
            WifiListBean wifiListBean = new WifiListBean();
            wifiListBean.setName(mScanResultList.get(i).SSID);
            wifiListBean.setLevel(mScanResultList.get(i).level);
            wifiListBean.setEncrypt(MyWifiManager.getEncrypt(mWifiManager, mScanResultList.get(i)));
            wifiListBeanList.add(wifiListBean);
        }

    }

    //中间显示的dialog
    public void showCentreDialog(final String wifiName, final int position) {
        //自定义dialog显示布局
        inflate = LayoutInflater.from(SubWifiActivity.this).inflate(R.layout.dialog_centre, null);
        //自定义dialog显示风格
        dialog = new Dialog(SubWifiActivity.this, R.style.DialogCentre);
        //点击其他区域消失
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(inflate);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);
        dialog.show();
        TextView tvName, tvMargin;
        final EditText et_password;
        tvName = dialog.findViewById(R.id.tvName);
        tvMargin = dialog.findViewById(R.id.tvMargin);
        et_password = dialog.findViewById(R.id.et_password);

        tvName.setText("wifi：" + wifiName);
        tvMargin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确定
                MyWifiManager.disconnectNetwork(mWifiManager);//断开当前wifi
                String type = MyWifiManager.getEncrypt(mWifiManager, mScanResultList.get(position));//获取加密方式
                Log.e("=====连接wifi:", wifiName + "；加密方式" + type);
                MyWifiManager.connectWifi(mWifiManager, wifiName, et_password.getText().toString(), type);//连接wifi
                dialog.dismiss();
            }
        });
    }


    //监听wifi状态广播接收器
    public class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Log.e("=====", "wifi state changed!!!!");
                //wifi开关变化
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (state) {
                    case WifiManager.WIFI_STATE_DISABLED: {
                        //wifi关闭
                        Log.e("=====", "wifi state disabled!");
                        tv_wifiState.append("\n 打开变化：wifi已经关闭");
                        break;
                    }
                    case WifiManager.WIFI_STATE_DISABLING: {
                        //wifi正在关闭
                        Log.e("=====", "wifi state disabling...");
                        tv_wifiState.append("\n 打开变化：wifi正在关闭");
                        break;
                    }
                    case WifiManager.WIFI_STATE_ENABLED: {
                        //wifi已经打开
                        Log.e("=====", "wifi state enabled");
                        tv_wifiState.append("\n 打开变化：wifi已经打开");
                        break;
                    }
                    case WifiManager.WIFI_STATE_ENABLING: {
                        //wifi正在打开
                        Log.e("=====", "wifi state enabling...");
                        tv_wifiState.append("\n 打开变化：wifi正在打开");
                        break;
                    }
                    case WifiManager.WIFI_STATE_UNKNOWN: {
                        //未知
                        Log.e("=====", "unknow");
                        tv_wifiState.append("\n 打开变化：wifi未知状态");
                        break;
                    }
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Log.e("=====", "network state changed!!!!");
                //监听wifi连接状态
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                Log.e("=====", "--NetworkInfo--" + info.toString());
                if (NetworkInfo.State.DISCONNECTED == info.getState()) {//wifi没连接上
                    Log.e("=====", "wifi disconnected!");
                    tv_wifiState.append("\n 连接状态：wifi没连接上");
                } else if (NetworkInfo.State.CONNECTED == info.getState()) {//wifi连接上了
                    Log.e("=====", "wifi connected!");
                    tv_wifiState.append("\n 连接状态：wifi以连接，wifi名称：" + MyWifiManager.getWiFiName(mWifiManager));
                    //After connect, refresh scan list
                    freshScanList();
                    adapter.notifyDataSetChanged();
                } else if (NetworkInfo.State.CONNECTING == info.getState()) {//正在连接
                    Log.e("=====", "wifi is connecting...");
                    tv_wifiState.append("\n 连接状态：wifi正在连接");
                }
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                //监听wifi列表变化
                Log.e("=====", "wifi list has changed");
                freshScanList();
                adapter.notifyDataSetChanged();
            }else if(WifiManager.EXTRA_NEW_RSSI.equals(intent.getAction())){
                Log.e("=====", "wifi new rssi");
            }else if(WifiManager.RSSI_CHANGED_ACTION.equals(intent.getAction())){
                Log.e("=====", " rssi changed action");

            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //取消监听
        unregisterReceiver(wifiReceiver);
    }

    public void openWifi(){
        btnOpenWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int state = mWifiManager.getWifiState();
                Log.d("hefangtest","wifi state" + state);
                if (state == WifiManager.WIFI_STATE_DISABLED) {
                    //关闭状态则打开
                    mWifiManager.setWifiEnabled(true);
                }
            }
        });

    }
    public void closeWifi(){
        btnCloseWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int state = mWifiManager.getWifiState();
                Log.d("hefangtest","wifi state" + state);
                if (state != WifiManager.WIFI_STATE_DISABLED) {
                    //关闭状态则打开
                    mWifiManager.setWifiEnabled(false);
                }
                clearScanList();

            }
        });
    }
}
