package com.example.hnsang.pingtest.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hnsang.pingtest.Activity.MainActivity;
import com.example.hnsang.pingtest.Adapter.DataAdapter;
import com.example.hnsang.pingtest.Database.ConnectionDB;
import com.example.hnsang.pingtest.Object.Constant;
import com.example.hnsang.pingtest.Object.DataObject;
import com.example.hnsang.pingtest.Object.MainDataObject;
import com.example.hnsang.pingtest.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class HomeFragment extends Fragment{ //implements SearchView.OnQueryTextListener {

    private RecyclerView rvData;
    private List<MainDataObject> listData;
    private DataAdapter dataAdapter;

    private View rootView;

    private ConnectionDB mConnectionDB = new ConnectionDB();
    private Connection mConnection = mConnectionDB.CONN();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public HomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        getActivity().setTitle("Home");

        rvData = rootView.findViewById(R.id.rv_data);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        rvData.setLayoutManager(manager);

        SharedPreferences preferences = getActivity().getSharedPreferences(Constant.PREFERENCE_NAME, getActivity().MODE_PRIVATE);
        final String id = preferences.getString(Constant.PREFERENCE_KEY_ID, null);

        try {
            if (mConnection == null) {
                Log.i("kanna", "Error in connection with SQL server");
            } else {
                String query = "select DISTINCT iddevice, username, timestart " +
                        "from data " +
                        "where idcustomer='" + id + "' " +
                        "ORDER BY timestart DESC";

                Statement stmt = mConnection.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                listData = new ArrayList<>();
                while (rs.next()) {
                    listData.add(new MainDataObject(rs.getString("iddevice"),
                            rs.getString("username"),
                            rs.getString("timestart")));
                }
            }
        } catch (Exception ex) {
            //Toast.makeText(getActivity(), "Exceptions", Toast.LENGTH_SHORT).show();
            Log.i("kanna", "Exceptions");
        }
        dataAdapter = new DataAdapter(getActivity(),listData);
        rvData.setAdapter(dataAdapter);
        Log.i("hinataa","okkk");
        //reload();
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.search_view, menu);
//        MenuItem item = menu.findItem(R.id.search_view);
//        SearchView searchView = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
//        searchView.setOnQueryTextListener(this);
//            MenuItemCompat.setOnActionExpandListener(item,
//                new MenuItemCompat.OnActionExpandListener() {
//                    @Override
//                    public boolean onMenuItemActionCollapse(MenuItem item) {
//                        // Do something when collapsed
//                        dataAdapter.setFilter(listData);
//                        return true; // Return true to collapse action view
//                    }
//
//                    @Override
//                    public boolean onMenuItemActionExpand(MenuItem item) {
//                        // Do something when expanded
//                        return true; // Return true to expand action view
//                    }
//                });
//    }
//
//    @Override
//    public boolean onQueryTextChange(String newText) {
//        final List<MainDataObject> filteredModelList = filter(listData,newText);
//        dataAdapter.setFilter(filteredModelList);
//        return true;
//    }
//
//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        return false;
//    }
//
//    private List<MainDataObject> filter(List<MainDataObject> models, String query) {
//        query = query.toLowerCase();
//        final List<MainDataObject> filteredModelList = new ArrayList<>();
//        for (MainDataObject model : models) {
//            final String text = model.getIdDevice().toLowerCase();
//            Log.i("itachi",""+text);
//            if (text.contains(query)) {
//                filteredModelList.add(model);
//            }
//        }
//        return filteredModelList;
//    }
}
