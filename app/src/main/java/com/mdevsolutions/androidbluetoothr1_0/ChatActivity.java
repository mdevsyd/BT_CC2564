package com.mdevsolutions.androidbluetoothr1_0;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {

    private BluetoothAdapter mBtAdapter;
    private String mConnectedDeviceName = null;
    private String mConnecterDeviceAddress = null;
    private ListView mChatListView;

    /**
     * Array Adapter for chat
     */
    private ArrayAdapter<String> mChatArrayAdapter;

    private EditText mOutEditText;
    private Button mSendBtn;
    //buffer for outgoing messages

    /**
     * String buffer for outgoing text
     */
    private StringBuffer mOutStringBuffer;


    //TODO BT CHAT SERVICE MEMBER
    private BluetoothChatService mChatService = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(Constants.DEBUG_TAG, "ChatActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);



        mOutEditText = (EditText) findViewById(R.id.textOutEt);
        mSendBtn = (Button) findViewById(R.id.sendBtn);
        mChatListView = (ListView)findViewById(R.id.chatLV);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        String mLocalDevice = mBtAdapter.getName();

        // get the device name and address to connect to passed from the selectedDeviceActivity
        Intent intent = getIntent();
        mConnectedDeviceName = intent.getStringExtra(Constants.EXTRA_DEVICE_NAME);
        mConnecterDeviceAddress = intent.getStringExtra(Constants.EXTRA_DEVICE_ADDRESS);

        // TODO where should this go?
        connectDevice(mConnecterDeviceAddress, true);
    }

    private void connectDevice(String address, boolean secure) {
        Log.d(Constants.DEBUG_TAG,"connectDevice-->" + address);
        BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
        mChatService.connect(device);
    }

    /**
     * Checks if BT is enabled, enables if needed. If enabled, calls to setup the chat session.
     */
    @Override
    protected void onStart() {
        Log.d(Constants.DEBUG_TAG,"ChatActivity, onStart()");
        super.onStart();
        //enable Bt if it is not on
        if (!mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();

        }
    }

    /**
     * Stops the BluetoothChatService when user exits.
     */
    @Override
    protected void onDestroy() {
        Log.d(Constants.DEBUG_TAG,"ChatActivity, onDestroy()");
        super.onDestroy();
        if (mChatService != null){
            mChatService.stop();
        }

    }

    /**
     * Starts BTChatService if it wasn't started in onStart().
     * Only starts if state=STATE_NONE
     */
    @Override
    protected void onResume() {
        Log.d(Constants.DEBUG_TAG,"ChatActivity, onResume()");
        super.onResume();
        if (mChatService != null){
            if (mChatService.getState() == BluetoothChatService.STATE_NONE){
                mChatService.start();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Bluetooth successfully enabled.", Toast.LENGTH_LONG).show();
                    setupChat();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "Bluetooth enabling failed. Exiting.", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

        }
    }


    private void setupChat() {
        Log.d(Constants.DEBUG_TAG,"ChatActivity, setupChat() method call");

        //initialise array adapter and set it to the listview
        mChatArrayAdapter = new ArrayAdapter<String>(this,R.layout.activity_chat);
        mChatListView.setAdapter(mChatArrayAdapter);

        //Initialise the edit text and make a listener for when user hits the return key when finished typing text
        mOutEditText.setOnEditorActionListener(mWriteListener);

        //Initialise the send button with on click listener
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send message including what user has typed into the edit text
                //TODO what if this s empty?
                String message = mOutEditText.getText().toString();
                sendMessage(message);
            }
        });


        // Initialise tge BluetoothChatService for BT connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialise the String buffer
        mOutStringBuffer = new StringBuffer("");

    }

    /**
     * An action listener for the text out edit text. this listens for return key
     */
    private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener(){

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = v.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    /**
     * Sends a message from one device to the other
     * @param message - the String of text taken from user input
     */
    private void sendMessage(String message) {
        //Check we are connected to chat service, display toast if not connected to any device.
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED){
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_LONG).show();
            return;
        }

        //check the message is not empty
        if (message.length() > 0){
            byte[] msgToSend = message.getBytes();
            mChatService.write(msgToSend);

            //clear the msg buffer
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    /**
     * Hndler to handle mesages back from the BluetoothChat Service
     */
    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case Constants.MESSAGE_STATE_CHANGE:
                    // Update the status by switching on arg1 --> state of BluetoothChatService
                    switch(msg.arg1){
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected)+" "+ mConnectedDeviceName);
                            //TODO if the getString doesn't work try getString(int, object)
                            mChatArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(getString(R.string.title_connecting));
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            //we are listening for a connection in the background
                            break;
                        case BluetoothChatService.STATE_NONE:
                            setStatus(getString(R.string.title_disconnected));
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuffer = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuffer);
                    //add the message to the listView adapter
                    mChatArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuffer = (byte[]) msg.obj;
                    String readMeassege = new String(readBuffer);
                    mChatArrayAdapter.add(mConnectedDeviceName +":  " + readMeassege);
                    break;

            }
        }
    };

    /**
     * Update the BluetoothChatService Status on action bar
     */
    private void setStatus(int resId){
        final android.app.ActionBar actionBar = getActionBar();
        //ensure there is an actionbar in the activity
        if (null == actionBar){
            return;
        }
        actionBar.setSubtitle(resId);
    }

    private void setStatus(CharSequence charSeq){
        final android.app.ActionBar actionBar = getActionBar();
        //ensure there is an actionbar in the activity
        if (null == actionBar){
            return;
        }
        actionBar.setSubtitle(charSeq);
    }


}
