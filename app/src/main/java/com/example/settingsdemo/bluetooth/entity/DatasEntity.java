package com.example.settingsdemo.bluetooth.entity;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *  数据层
 */

public class DatasEntity implements Serializable{


    public static List<BluetoothDevice> mPairedDevices = new ArrayList<>();//已匹配蓝牙

    public static List<BluetoothDevice> mBluetoothDevices = new ArrayList<>(); // 存储蓝牙列表


}
