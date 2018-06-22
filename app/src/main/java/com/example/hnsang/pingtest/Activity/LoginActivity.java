package com.example.hnsang.pingtest.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hnsang.pingtest.Database.ConnectionDB;
import com.example.hnsang.pingtest.Object.Constant;
import com.example.hnsang.pingtest.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Ho Ngoc Sang on 4/6/2018.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText mUserNameView;
    private TextInputEditText mPasswordView;
    private ProgressBar mProgressBar;

    private TextInputLayout mTILPasswordView;
    private Button mSignInView;

    private boolean mIsShow = false;

    private ProgressDialog progressDialog;

    private ConnectionDB connectionDB = new ConnectionDB();
    private Connection con = connectionDB.CONN();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mCheckLogin();

        mAddControls();

        mAddEvents();
    }

    private void mCheckLogin() {

        SharedPreferences sharedPreferences = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);
        String name = sharedPreferences.getString(Constant.PREFERENCE_KEY_ID, "");
        String pass = sharedPreferences.getString(Constant.PREFERENCE_KEY_PASS, "");

        try {
            if (con == null) {
                Log.i("hnsangsang", "Error in connection with SQL server");

            } else if (!name.equals("") && !pass.equals("")) {

                String query = "select * from account where username='" + name + "' and password='" + pass + "'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                if (rs.next()) {
                    Intent intent = new Intent(this, MainActivity.class);
                    finish();
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    Toast.makeText(this, "Sai User hoặc Mật Khẩu", Toast.LENGTH_SHORT).show();
                    Log.i("hnsangsang", "Sai User hoặc Mật Khẩu");
                }
            }else {
                mSignIn();
            }
        } catch (Exception ex) {
//            Toast.makeText(this, "Exceptions", Toast.LENGTH_SHORT).show();
//            Log.i("hnsang", "Exceptions");
        }
    }


    private void mAddControls() {
        mUserNameView = findViewById(R.id.edt_username);
        mPasswordView = findViewById(R.id.edt_password);
        mTILPasswordView = findViewById(R.id.show_password);
        mSignInView = findViewById(R.id.btn_sign_in);

        mProgressBar = findViewById(R.id.login_progress);
        mProgressBar.setVisibility(View.GONE);

    }

    private void mAddEvents() {
        mSignInView.setOnClickListener(this);
        mTILPasswordView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.show_password:
                mShowPassword();
                break;

            case R.id.btn_sign_in:
                mSignIn();
                break;
        }
    }

    private void mShowPassword() {
        if (!mIsShow) {
            mPasswordView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mIsShow = true;
            mTILPasswordView.setPasswordVisibilityToggleEnabled(false);
        } else {
            mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mIsShow = false;
            mTILPasswordView.setPasswordVisibilityToggleEnabled(true);
        }
        mPasswordView.setSelection(mPasswordView.length());

    }

    private void mSignIn() {
        // get data
        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (userName.equals("")) {
            Toast.makeText(this, getString(R.string.toast_username_empty), Toast.LENGTH_SHORT).show();
        } else if (!checkInputPassword(password)) {
            Toast.makeText(this, getString(R.string.toast_password_incorrect), Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog progressDialog;
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getResources().getString(R.string.dialog));
            progressDialog.setCancelable(false);
            progressDialog.show();

            try {
                if (con == null) {
                    //Toast.makeText(this, "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
                    Log.i("hnsangsang", "Error in connection with SQL server");

                } else {
                    Log.i("hnsangsang", "connect databases");
                    String query = "select * from account where username='" + userName + "' and password='" + password + "'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if (rs.next()) {
                        SharedPreferences share = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = share.edit();
                        editor.putString(Constant.PREFERENCE_KEY_ID, userName);
                        editor.putString(Constant.PREFERENCE_KEY_PASS, password);
                        editor.apply();

                        Toast.makeText(this, "Đăng Nhập Thành Công", Toast.LENGTH_SHORT).show();
                        Log.i("hnsangsang", "Đăng Nhập Thành Công");

                        Intent intent = new Intent(this, MainActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("idnhanvien", userName);
                        finish();
                        startActivity(intent);

                    } else {
                        Toast.makeText(this, "Sai User hoặc Mật Khẩu", Toast.LENGTH_SHORT).show();
                        Log.i("hnsangsang", "Sai User hoặc Mật Khẩu");
                    }

                }
            } catch (Exception ex) {
//                Toast.makeText(this, "Exceptions", Toast.LENGTH_SHORT).show();
//                Log.i("hnsang123", "Exceptions");
            }
        }
    }

    private boolean checkInputPassword(String password) {
        password = password.trim();
        return !password.equals("") && password.length() >= 6;
    }
}

