package com.mdevsolutions.androidbluetoothr1_0;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SelectedDeviceActivity extends AppCompatActivity {
    
    private String mName;
    private String mAddress;
    private TextView mNameTv;
    private TextView mAddressTv;
    private Button mConnectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_device);

        TextView mNameEt = (TextView) findViewById(R.id.selected_device_name_tv);
        TextView mAddressEt = (TextView) findViewById(R.id.selected_device_address_tv);
        Button mConnectBtn = (Button)findViewById(R.id.connectBtn);

        Intent intent = getIntent();
        mName = intent.getStringExtra(Constants.EXTRA_DEVICE_NAME);
        mAddress = intent.getStringExtra(Constants.EXTRA_DEVICE_ADDRESS);
        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mNameEt.setText(mName);
        mAddressEt.setText(mAddress);

        
    }




}
