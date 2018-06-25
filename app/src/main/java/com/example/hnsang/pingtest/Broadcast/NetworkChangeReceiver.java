package com.example.hnsang.pingtest.Broadcast;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.hnsang.pingtest.Activity.Loading;
import com.example.hnsang.pingtest.Activity.LoginActivity;
import com.example.hnsang.pingtest.Database.ConnectionDB;

import java.sql.Connection;

public class NetworkChangeReceiver extends BroadcastReceiver {

    ProgressDialog showDialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get state of network
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        NetworkInfo wifiInfo = null;
        ConnectionDB connectionDB = new ConnectionDB();
        Connection connection = connectionDB.CONN();

        if (connectivityManager != null) {
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            wifiInfo = connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI);
        }

        // Check state of network
        if ((networkInfo != null && networkInfo.isConnected()
                || wifiInfo != null && wifiInfo.isConnected())
                && connection != null) {
            Log.i("himiwari", "on");
            //dialog progressDialog custom lại
            intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }else {
            showDialog = ProgressDialog.show(context,
                    "Loading..",
                    "Không có kết nối mạng, hãy kiểm tra lại kết nối mạng",
                    true,
                    false);
        }
    }
}