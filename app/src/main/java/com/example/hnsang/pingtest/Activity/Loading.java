package com.example.hnsang.pingtest.Activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.hnsang.pingtest.Broadcast.NetworkChangeReceiver;
import com.example.hnsang.pingtest.R;


public class Loading extends AppCompatActivity {

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

//        receiver = new NetworkChangeReceiver();
//        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
//        registerReceiver(receiver, filter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Decalre & register broadcast receiver.
        receiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister receiver when activity is destroyed.
        unregisterReceiver(receiver);
    }
}
