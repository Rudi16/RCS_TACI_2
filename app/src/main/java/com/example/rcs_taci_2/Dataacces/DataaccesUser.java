package com.example.rcs_taci_2.Dataacces;

import android.util.Log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.example.rcs_taci_2.Entity.E_user;
import com.example.rcs_taci_2.Helper.ConnectionHelper;

public class DataaccesUser {

    public static String status_action;
    public static String Error;
    public String InsertUser(E_user e_users, String action){

        try {
            ConnectionHelper con = new ConnectionHelper();
            Connection connect = ConnectionHelper.CONN();
            if(action.equals("register")) {
                String queryStmt = "EXEC sato_sp_users '" + e_users.getUserId() + "','" + e_users.getName() + "',"
                        + "'" + e_users.getPassword() + "','" + e_users.getAuthority() + "','insert'";
                PreparedStatement preparedStatement = connect
                        .prepareStatement(queryStmt);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return "Added successfully";
            }else{
                String queryStmt = "EXEC sato_sp_users '" + e_users.getUserId() + "','" + e_users.getName() + "',"
                        + "'" + e_users.getPassword() + "','" + e_users.getAuthority() + "','update'";
                PreparedStatement preparedStatement = connect
                        .prepareStatement(queryStmt);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return "Update successfully";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage().toString();
        } catch (Exception e) {
            return e.getMessage().toString();
        }
    }

    public ResultSet GetDataLogin(String UserID) throws SQLException {
        ResultSet Rs ;
        ConnectionHelper con = new ConnectionHelper();
        Connection connect = ConnectionHelper.CONN();
        if (connect == null){
            Rs = null;
            return Rs;
        }else {
            String query = "EXEC sato_spall \"Select * from sato_User where UserID='" + UserID + "'\"";
            PreparedStatement ps = connect.prepareStatement(query);
            Log.e("query", query);
            Rs = ps.executeQuery();
            return Rs;
        }

    }

    public ResultSet GetDataUsers() throws SQLException {
        ResultSet Rs ;
        ConnectionHelper con = new ConnectionHelper();
        Connection connect = ConnectionHelper.CONN();
        if (connect == null){
            Rs = null;
            return Rs;
        }else {
            String query = "EXEC sato_spall 'Select * from sato_UserId'";
            PreparedStatement ps = connect.prepareStatement(query);
            Log.e("query", query);
            Rs = ps.executeQuery();
            return Rs;
        }

    }

    public String DeleteUserID(String UserID){

        String queryStmt = "";
        try {
            ConnectionHelper con = new ConnectionHelper();
            Connection connect = ConnectionHelper.CONN();

            queryStmt = "Exec sato_spall \"Delete From sato_UserId Where UserID='" + UserID + "'\"";
            PreparedStatement preparedStatement = connect
                    .prepareStatement(queryStmt);

            preparedStatement.executeUpdate();
            preparedStatement.close();
            return "Delete successfully";

        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage().toString() + queryStmt;
        } catch (Exception e) {
            return e.getMessage().toString() + queryStmt;
        }
    }
}
