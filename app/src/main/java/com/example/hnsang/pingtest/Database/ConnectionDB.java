package com.example.hnsang.pingtest.Database;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Ho Ngoc Sang on 2/26/2017.
 */

public class ConnectionDB {
    String ip = "192.168.43.151";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "PingTest";
    String un = "sa";

    String password = "hnsang";

    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        try {
            Class.forName(classs);
            String ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
            conn = DriverManager.getConnection(ConnURL);
            Log.i("minato", ""+ConnURL);
        }
        catch (SQLException se) {
            Log.i("minato1", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.i("minato2", e.getMessage());
        } catch (Exception e) {
            Log.i("minato3", e.getMessage());
        }
        return conn;
    }

}
