package com.mdevsolutions.androidbluetoothr1_0;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SelectedDeviceActivity extends AppCompatActivity {
    
    private String mName;
    private String mAddress;
    private TextView mNameTv;
    private TextView mAddressTv;
    private Button mConnectBtn;
    private BluetoothAdapter mBtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_device);

        mNameTv = (TextView) findViewById(R.id.selected_device_name_tv);
        mAddressTv = (TextView) findViewById(R.id.selected_device_address_tv);
        mConnectBtn = (Button)findViewById(R.id.connectBtn);

        Intent intent = getIntent();
        mName = intent.getStringExtra(Constants.EXTRA_DEVICE_NAME);
        mAddress = intent.getStringExtra(Constants.EXTRA_DEVICE_ADDRESS);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDiscoverable();
                Intent intent = new Intent(SelectedDeviceActivity.this, ChatActivity.class);
                // pass the device name and MAC address to the chat activity
                intent.putExtra(Constants.EXTRA_DEVICE_NAME, mName);
                intent.putExtra(Constants.EXTRA_DEVICE_ADDRESS, mAddress);
                startActivity(intent);
                finish();

            }
        });

        mNameTv.setText(mName);
        mAddressTv.setText(mAddress);

    }

    /**
     * Checks BT adapter scan mode, prompts discoverability if required.
     * Makes device discoverable for 120 seconds
     */
    private void checkDiscoverable() {
        if(mBtAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent discoverableDeviceIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableDeviceIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
            startActivity(discoverableDeviceIntent);
            Toast.makeText(this,R.string.discoverable,Toast.LENGTH_LONG).show();
        }
    }


}
