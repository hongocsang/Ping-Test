package com.example.hnsang.pingtest.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hnsang.pingtest.R;

import java.util.ArrayList;
import java.util.List;

public class InformationFragment extends Fragment implements View.OnClickListener{

    private TextView mTVVersionName;
    private TextView mTVEmail;
    private TextView mTVPhone;
    private TextView mTVWeb;

    private PackageInfo mPackageInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_information,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkAndRequestPermissions();

        getActivity().setTitle("Information");

        mAddControl();

        mAddEvent();
    }

    private void mAddControl(){
        mTVVersionName = getActivity().findViewById(R.id.tv_version_name);
        mTVEmail = getActivity().findViewById(R.id.tv_email);
        mTVPhone = getActivity().findViewById(R.id.tv_phone);
        mTVWeb = getActivity().findViewById(R.id.tv_web);

        mPackageInfo = null;
        try {
            mPackageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mTVVersionName.setText(mPackageInfo.versionName.toString());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_email:
                mEmail();
                break;
            case R.id.tv_phone:
                mPhone(mTVPhone.getText().toString());
                break;

            case R.id.tv_web:
                //mWeb();
                break;
        }
    }

    private void mWeb() {
        String uri = "https://www.facebook.com/Job-Management-2126265037390832";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    private void mPhone(String phone) {
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        phoneIntent.setData(Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(phoneIntent);
    }

    private void mEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL , "sangb1401179@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Phản hồi góp ý");
        PackageInfo info = null;

        try {
            info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        intent.putExtra(Intent.EXTRA_TEXT, "Góp ý về cho nhà phát hành "+info.versionName.toString());
        intent.setData(Uri.parse("mailto: sangb1401179@gmail.com"));
        startActivity(Intent.createChooser(intent, "Phản hồi với"));
    }

    private void mAddEvent() {
        mTVEmail.setOnClickListener(this);
        mTVPhone.setOnClickListener(this);
        mTVWeb.setOnClickListener(this);
    }

    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.CALL_PHONE,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.READ_CONTACTS
        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }

}
