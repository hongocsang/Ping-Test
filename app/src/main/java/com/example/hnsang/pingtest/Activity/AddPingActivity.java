package com.example.hnsang.pingtest.Activity;
/**
 * Created by Ho Ngoc Sang on 4/6/2018.
 */

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private Button mBtnTestArduino;
    private TextView mTvStatusArduino;


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
        mBtnTestArduino = findViewById(R.id.btn_test_arduino);
        mBtnTestArduino.setText("kiểm tra");
//        mBtnRetype = findViewById(R.id.btn_retype);

        mTvStatusArduino = findViewById(R.id.tv_status_arduino);
        mTvStatusArduino.setText("Hãy nhập tên thiết bị");


        mEdtEnable(false);
    }

    private void mEdtEnable(boolean answer) {
        mEdtUserName.setEnabled(answer);
        mEdtPacket.setEnabled(answer);
        mEdtTime.setEnabled(answer);

        mBtnPing.setEnabled(answer);

        //mTvStatusArduino.setEnabled(answer);
    }

    private void mAddEvents() {
        mBtnPing.setOnClickListener(this);
        mBtnTestArduino.setOnClickListener(this);
    }

    public void mCheckEdtIdArduino() {
        if (!mEdtIdArduino.getText().equals("")) {
            mTestArduino();
        } else {
            mTvStatusArduino.setText("Chưa nhập tên thiết bị");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ping:
                mPing();
                break;
            case R.id.btn_test_arduino:
                //mTestArduino();
                mCheckEdtIdArduino();
                break;
        }
    }

    private void mTestArduino() {
        if (mBtnTestArduino.getText().equals("kiểm tra")) {
            ConnectionDB connectionDB = new ConnectionDB();
            Connection connection = connectionDB.CONN();
            String idArduino = mEdtIdArduino.getText().toString();
            try {
                if (connection == null) {
                    //not connect
                } else {
                    Log.i("hinata", "connect databases");
                    String query = "select status from device where iddevice='" + idArduino + "'";
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        switch (rs.getString("status")) {
                            case "ON":
                                mTvStatusArduino.setText("Thiết bị đang bật, bạn hãy thử với thiết bị khác");
                                mBtnTestArduino.setText("nhập lại");
                                mEdtIdArduino.setEnabled(false);
                                break;
                            case "OFF":
                                mTvStatusArduino.setText("Thiết bị đã tắt, bạn hãy bật thiết bị lại");
                                mBtnTestArduino.setText("nhập lại");
                                mEdtIdArduino.setEnabled(false);
                                break;
                            case "READY":
                                mTvStatusArduino.setText("Thiết bị đã sẵn sàng");
                                mBtnTestArduino.setText("nhập lại");
                                mEdtIdArduino.setEnabled(false);
                                mEdtEnable(true);
                                break;
                        }
                    } else {
                        mTvStatusArduino.setText("Không tìm thấy thiết bị, bạn hãy thử với thiết bị khác");
                        mBtnTestArduino.setText("nhập lại");
                        mEdtIdArduino.setEnabled(false);
                    }
                }
            } catch (Exception ex) {
            }
        } else {
            mEdtEnable(false);
            mBtnTestArduino.setText("kiểm tra");
            mTvStatusArduino.setText("Hãy nhập tên thiết bị");
            mEdtIdArduino.setEnabled(true);
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

            if (idnv != null && mCheckEdtPacket() && mCheckEdtTime() && mCheckEdtUserName()) {
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
                            Intent intent = new Intent(AddPingActivity.this, MainActivity.class);
                            finish();
                            Toast.makeText(this, "Thiết bị đang ping, bạn hãy chờ khoảng "
                                    + mEdtTime.getText().toString() +
                                    " phút để có dữ liệu của thiết bị", Toast.LENGTH_LONG).show();

                            startActivity(intent);
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
            }//else {
            //Toast.makeText(this, "Bạn chưa nhập đầy đủ các thông tin", Toast.LENGTH_SHORT).show();
            //}
        }
    }

    public boolean mCheckEdtUserName() {
        ConnectionDB connectionDB = new ConnectionDB();
        Connection connection = connectionDB.CONN();
        String strUserName = mEdtUserName.getText().toString();
        if (!mEdtUserName.getText().toString().equals("")) {
            try {
                if (connection == null) {
                    return false;
                } else {
                    Log.i("hinata", "connect databases");
                    String query = "select idcustomer from customer where idcustomer='" + strUserName + "'";
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        return true;
                    } else {
                        Toast.makeText(this, "Bạn nhập sai tên người dùng", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            } catch (Exception ex) {
                return false;
            }
        } else {
            Toast.makeText(this, "Bạn chưa nhập tên người dùng", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean mCheckEdtPacket() {
        if (!mEdtPacket.getText().toString().equals("")) {
            if (50 <= Integer.parseInt(mEdtPacket.getText().toString())
                    && Integer.parseInt(mEdtPacket.getText().toString()) <= 1000) {
                return true;
            } else {
                Toast.makeText(this, "Số gói tin không nằm trong khoảng 50-1000", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(this, "Bạn chưa nhập số gói tin", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public boolean mCheckEdtTime() {
        if (!mEdtTime.getText().toString().equals("")) {

            if (5 <= Integer.parseInt(mEdtTime.getText().toString())
                    && Integer.parseInt(mEdtTime.getText().toString()) <= 30) {
                return true;
            } else {
                Toast.makeText(this, "Thời gian delay không nằm trong khoảng 5-30 phút", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(this, "Bạn chưa nhập thời gian delay", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void mTimeStart() {
        Date date = new Date();
        mSimpleDateFormat = new SimpleDateFormat(mStrDateFormat);
        Log.i("kanna1", mSimpleDateFormat.format(date));
        mSimpleTimeFormat = new SimpleDateFormat(mStrTimeFormat);
        Log.i("kanna2", mSimpleTimeFormat.format(date));
        mStrTimeStart = mSimpleDateFormat.format(date) + "%20" + mSimpleTimeFormat.format(date);
        Log.i("kanna3", mStrTimeStart);
    }

    @Override
    public void onBackPressed() {
        if (mEdtUserName.getText().toString().equals("")
                && mEdtIdArduino.getText().toString().equals("")
                && mEdtPacket.getText().toString().equals("")
                && mEdtTime.getText().toString().equals("")) {
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);

            builder.setMessage(getString(R.string.message_edit_and_save));

            builder.setPositiveButton(getString(R.string.message_positive_save), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });

            builder.setNegativeButton(getString(R.string.message_negative_save), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void mCheckEditNull(){
        // kiểm tra các trường hợp không nhập edt
    }
}
