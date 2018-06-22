package com.example.hnsang.pingtest.Activity;
/**
 * Created by Ho Ngoc Sang on 4/6/2018.
 */

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hnsang.pingtest.Database.ConnectionDB;
import com.example.hnsang.pingtest.Object.Constant;
import com.example.hnsang.pingtest.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPingActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEdtIdArduino;
    private EditText mEdtUserName;
    private EditText mEdtPacket;
    private EditText mEdtTime;
    private Button mBtnPing;

    final String username = "obxynjsd";
    final String password = "MJzGK5ooAeCP";
    MqttAndroidClient client;

    private String mStrDateFormat = "yyyy-MM-dd";
    private String mStrTimeFormat = "HH:mm:ss";
    private String mStrTimeStart;

    private SimpleDateFormat mSimpleDateFormat;
    private SimpleDateFormat mSimpleTimeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ping);

        mAddControls();

        mAddEvents();

        mConnetArduino();

        //mTimeStart();
    }

    private void mConnetArduino() {
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(),
                "tcp://m11.cloudmqtt.com:14096",
                clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.i("hinata", "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.i("hinata", "onFailure");

                }
            });
            //send


        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void mAddControls() {
        mEdtIdArduino = findViewById(R.id.edt_id_arduino);
        mEdtUserName = findViewById(R.id.edt_username);
        mEdtPacket = findViewById(R.id.edt_packet);
        mEdtTime = findViewById(R.id.edt_time);

        mBtnPing = findViewById(R.id.btn_ping);
    }

    private void mAddEvents() {
        mBtnPing.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ping:
                mPing();
                break;
        }
    }

    private void mPing() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.dialog));
        progressDialog.setCancelable(false);

        String idArduino = mEdtIdArduino.getText().toString();
        if (getIntent() != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);
            String idnv = sharedPreferences.getString(Constant.PREFERENCE_KEY_ID, "");

            if (idnv != null) {
                progressDialog.show();
                ConnectionDB connectionDB = new ConnectionDB();
                Connection connection = connectionDB.CONN();

                try {
                    if (connection == null) {
                        Log.i("hinata", "Error in connection with SQL server");
                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        Log.i("hinata", "connect databases");
                        String query = "select * from device where iddevice='" + idArduino + "'";
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if (rs.next()) {
                            //send
                            mTimeStart();
                            client.publish(idArduino + "/timestart",
                                    mStrTimeStart.getBytes(),
                                    0,
                                    false);

                            client.publish(idArduino + "/user",
                                    mEdtUserName.getText().toString().getBytes(),
                                    0,
                                    false);

                            client.publish(idArduino + "/idcustomer",
                                    idnv.getBytes(),
                                    0,
                                    false);

                            client.publish(idArduino + "/packet",
                                    mEdtPacket.getText().toString().getBytes(),
                                    0,
                                    false);

                            client.publish(idArduino + "/time",
                                    mEdtTime.getText().toString().getBytes(),
                                    0,
                                    false);

                            Log.i("hinata", "ok");
                        } else {
                            Log.i("hinata", "no ok");
                        }
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    Toast.makeText(this, "Exceptions", Toast.LENGTH_SHORT).show();
                    Log.i("hinata", "Exceptions");
                }
            }
        }
    }

    private void mTimeStart() {
        Date date = new Date();
        mSimpleDateFormat = new SimpleDateFormat(mStrDateFormat);
        Log.i("kanna1", mSimpleDateFormat.format(date));
        mSimpleTimeFormat = new SimpleDateFormat(mStrTimeFormat);
        Log.i("kanna2", mSimpleTimeFormat.format(date));
        mStrTimeStart = mSimpleDateFormat.format(date) + "%20" + mSimpleTimeFormat.format(date);
        Log.i("kanna3",mStrTimeStart);
    }
}
