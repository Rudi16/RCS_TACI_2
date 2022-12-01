package com.example.rcs_taci_2.Helper;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {
    public static  String server;
    public static String user;
    public static String passwd;
    public static String connection;
    public static String Instance;
    public String Server = null;
    public static String setting;
    @SuppressLint("NewApi")
    public static Connection CONN() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {
            String Server = null;
            String Instance = null ;
            String[] checkInstance = server.split("\\\\");
            if(checkInstance.length > 1){
                Server = checkInstance[0];
                Instance = checkInstance[1];
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                ConnURL = "jdbc:jtds:sqlserver://" + Server + ";"
                        + "databaseName=DB_RCS_TACI;user=" + user    + ";password=" + passwd + ";instance=" + Instance + ";";
            }else{
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                ConnURL = "jdbc:jtds:sqlserver://" + server + ";"
                        + "databaseName=DB_RCS_TACI;user=" + user    + ";password=" + passwd + ";";
            }
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }

}
