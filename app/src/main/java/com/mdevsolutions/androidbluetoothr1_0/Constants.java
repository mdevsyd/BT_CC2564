package com.mdevsolutions.androidbluetoothr1_0;

import java.util.UUID;

/**
 * Created by Michi on 12/02/2017.
 */

public class Constants {

    public static final String EXTRA_DEVICE_ADDRESS = "device_address";
    public static final String EXTRA_DEVICE_NAME = "device_name";
    public static final String LOCAL_DEVICE_NAME = "local_device_name";
    public static final UUID MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public static final String DEBUG_TAG = "debug";

    public static final int REQUEST_ENABLE_BT = 100;
    public static final int REQUEST_CONNECT_DEVICE_SECURE = 200;
    public static final int REQUEST_CONNECT_DEVICE_INSECURE = 300;

    // Messages sent from BT chat service handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
}
