package com.example.halloworld.Utility;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClass {

    String SqlIp= "192.168.178.32";
    String SqlPort= "1433";
    String SqlDriverClass = "net.sourceforge.jtds.jdbc.Driver";
    String SqlDb = "Android";
    String SqlUser = "android";
    String SqlPassword = "android";

    String MySqlIp= "sql7.freemysqlhosting.net";
    String MySqlDriverClass = "com.mysql.jdbc.Driver";
    String MySqlDb = "sql7372914";
    String MySqlPort = "3306";
    String MySqlUser = "sql7372914";
    String MySqlPassword= "JPNwrSRUEe";

    public Connection Connection(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn= null;
        String ConnUrl;
        try {
            //SQL
            /*Class.forName(SqlDriverClass);
            ConnUrl = "jdbc:jtds:sqlserver://" + SqlIp + "/" +SqlDb;
            conn= DriverManager.getConnection(ConnUrl,SqlUser,SqlPassword);*/

            // MySql
            Class.forName(MySqlDriverClass);
            ConnUrl = "jdbc:mysql://" + MySqlIp + "/" +MySqlDb;
            conn= DriverManager.getConnection(ConnUrl,MySqlUser,MySqlPassword);

        }catch (SQLException e){
            Log.e("Error 1:", e.getMessage());
        }
        catch (ClassNotFoundException e){
            Log.e("Error 2", e.getMessage());
        }
        catch (UnsupportedOperationException u){
            Log.e("Error 3", u.getMessage());
        }
        catch (Exception e){
            Log.e("Error 4", e.getMessage());
        }
        return conn;
    }

}
