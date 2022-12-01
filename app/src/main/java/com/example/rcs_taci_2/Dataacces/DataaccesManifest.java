package com.example.rcs_taci_2.Dataacces;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.util.Log;

import com.example.rcs_taci_2.Entity.E_Manifest;
import com.example.rcs_taci_2.Entity.E_user;
import com.example.rcs_taci_2.Helper.ConnectionHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.net.ssl.SSLEngineResult;

public class DataaccesManifest {

    public String CheckManifest(E_Manifest e_manifest){

        try {
            String Check = null;
            ResultSet Rs ;
            ConnectionHelper con = new ConnectionHelper();
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                Check= "ER - Tidak Bisa Konek ke Database Server!";
            }else {
                String query="";
                query = "exec [SP_RECEIVING_MANIFEST] '" + e_manifest.getManifestNo() + "','" +
                        e_manifest.getDnNo() + "','" + e_manifest.getSupplierCode() + "','" +
                        e_manifest.getSupplierName() + "','" + e_manifest.getPartNo() + "','" +
                        e_manifest.getQtyManifest() + "','" + E_user.UserId + "','" + E_user.DeviceID + "'";
                PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs =  ps.executeQuery();
                if (Rs != null) {
                    if (Rs.next()) {
                        Check = Rs.getString("STS");
                    }
                }
            }
            return  Check;
        } catch (Exception e) {
            return "ER - " + e.toString();
        }
    }
    public String CheckManifestQc(E_Manifest e_manifest){

        try {
            String Check = null;
            ResultSet Rs ;
            ConnectionHelper con = new ConnectionHelper();
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                Check= "ER - Tidak Bisa Konek ke Database Server!";
            }else {
                String query="";
                query = "exec [SP_QC_MANIFEST] '" + e_manifest.getManifestNo() + "','" +
                        e_manifest.getDnNo() + "','" + e_manifest.getSupplierCode() + "','" +
                        e_manifest.getSupplierName() + "','" + e_manifest.getPartNo() + "','" +
                        e_manifest.getQtyManifest() + "','" + E_user.UserId + "','" + E_user.DeviceID + "'";
                PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs =  ps.executeQuery();
                if (Rs != null) {
                    if (Rs.next()) {
                        Check = Rs.getString("STS");
                    }
                }
            }
            return  Check;
        } catch (Exception e) {
            return "ER - " + e.toString();
        }
    }
    public String CheckManifestQc1(E_Manifest e_manifest){

        try {
            String Check = null;
            ResultSet Rs ;
            ConnectionHelper con = new ConnectionHelper();
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                Check= "ER - Tidak Bisa Konek ke Database Server!";
            }else {
                String query="";
                query = "exec [SP_QC_MANIFEST_CHECK] '" + e_manifest.getManifestNo() + "','" +
                        e_manifest.getDnNo() + "','" + e_manifest.getSupplierCode() + "','" +
                        e_manifest.getSupplierName() + "','" + e_manifest.getPartNo() + "','" +
                        e_manifest.getQtyManifest() + "','" + E_user.UserId + "'";
                PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs =  ps.executeQuery();
                if (Rs != null) {
                    if (Rs.next()) {
                        Check = Rs.getString("STS");
                    }
                }
            }
            return  Check;
        } catch (Exception e) {
            return "ER - " + e.toString();
        }
    }
    public ResultSet CheckKanban(String Manifest,String SuplierCode,String PartNo,int Qty,String Serial){
        try {
            ResultSet Rs ;
            ConnectionHelper con = new ConnectionHelper();
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                return  null;
            }else {
                String query = "";
                query = "exec [SP_RECEIVING_KANBAN] '" + Manifest + "','" +
                        SuplierCode + "','" + PartNo + "','" +
                        Qty + "','" + Serial + "','" + E_user.UserId + "','" + E_user.Version + "','" + E_user.DeviceID + "'" ;
                PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs = ps.executeQuery();
            }
            return  Rs;
        } catch (Exception e) {
           return  null ;
        }
    }
    public ResultSet CheckKanbanQCFirst(String Manifest,String SuplierCode,String PartNo,int Qty,String Serial){
        try {
            ResultSet Rs ;
            ConnectionHelper con = new ConnectionHelper();
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                return  null;
            }else {
                String query = "";
            query = "exec [SP_QC_KANBANFIRST] '" + Manifest + "','" +
                        SuplierCode + "','" + PartNo + "','" +
                        Qty + "','" + Serial + "','" + E_user.UserId + "','" + E_user.Version + "','" + E_user.DeviceID + "'";
                PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs = ps.executeQuery();
            }
            return  Rs;
        } catch (Exception e) {
            return  null ;
        }
    }


    public ResultSet CheckKanbanQC(String Manifest,String SuplierCode,String PartNo,int Qty,String Serial){
        try {
            ResultSet Rs ;
            ConnectionHelper con = new ConnectionHelper();
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                return  null;
            }else {
                String query = "";
                query = "exec [SP_QC_KANBAN] '" + Manifest + "','" +
                        SuplierCode + "','" + PartNo + "','" +
                        Qty + "','" + Serial + "','" + E_user.UserId + "','" + E_user.Version + "'" ;
                PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs = ps.executeQuery();
            }
            return  Rs;
        } catch (Exception e) {
            return  null ;
        }
    }

    public ResultSet SaveKanbanQC(String Manifest,String SuplierCode,String PartNo,int Qty,String Serial,String Status_Part,String Remark,int Hasil){
        try {
            ResultSet Rs ;
            ConnectionHelper con = new ConnectionHelper();
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                return  null;
            }else {
                String query = "";
                query = "exec [SP_QC_INSERT_KANBAN] '" + Manifest + "','" +
                        SuplierCode + "','" + PartNo + "','" +
                        Qty + "','" + Serial + "','" + E_user.UserId + "','" + E_user.Version + "','" + Status_Part + "','" + Remark + "','" + Hasil + "','" + E_user.DeviceID + "'";
                PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs = ps.executeQuery();
            }
            return  Rs;
        } catch (Exception e) {
            return  null ;
        }
    }
    public String ManualComplete(String manifest) {
        try {
            String Check = null;
            ResultSet Rs ;
            ConnectionHelper con = new ConnectionHelper();
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                Check= "ER - Tidak Bisa Konek ke Database Server!";
            }else {
                String query="";
                query = "exec [SP_RECEIVING_MANUALCOMPLETE] '" + manifest + "'";
                PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs =  ps.executeQuery();
                if (Rs != null) {
                    if (Rs.next()) {
                        Check = Rs.getString("STS");
                    }
                }
            }
            return  Check;
        } catch (Exception e) {
            return "ER - " + e.toString();
        }
    }

    public String SetQuarantine(String manifest ,String partNo) {
        try {
            String Check = null;
            ResultSet Rs ;
            ConnectionHelper con = new ConnectionHelper();
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                Check= "ER - Tidak Bisa Konek ke Database Server!";
            }else {
                String query="";
                query = "exec [SP_QC_QUARANTINE] '" + manifest + "','" + partNo + "'";
                PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs =  ps.executeQuery();
                if (Rs != null) {
                    if (Rs.next()) {
                        Check = Rs.getString("STS");
                    }
                }
            }
            return  Check;
        } catch (Exception e) {
            return "ER - " + e.toString();
        }
    }

    public ResultSet GetQuarantineData(String Manifest, String PartNo) {
        try {
            ResultSet Rs ;
            ConnectionHelper con = new ConnectionHelper();
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                return  null;
            }else {
                String query = "";
                query = "exec [SP_QC_QUARANTINE_GETDATA] '" + Manifest + "','" + PartNo + "'";
                PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs = ps.executeQuery();
            }
            return  Rs;
        } catch (Exception e) {
            return  null ;
        }
    }

    public ResultSet GetManifestOnline(String ManifestNo) {
        try {
            ResultSet Rs ;
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                return  null;
            }else {
                String query = "";
                query = "exec [SP_RECEIVING_GETMANIFESTONLINE] '" + ManifestNo + "'";
                PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs = ps.executeQuery();
            }
            return  Rs;
        } catch (Exception e) {
            return  null ;
        }
    }
    public ResultSet GetHistoryOnline(String manifestNo) {
        try {
            ResultSet Rs ;
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                return  null;
            }else {
                String query = "";
                query = "exec [SP_RECEIVING_GETHISTORYONLINE] '" + manifestNo + "'";
                PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs = ps.executeQuery();
            }
            return  Rs;
        } catch (Exception e) {
            return  null ;
        }
    }

    public boolean UploadReceiving(String manifestNo, String dn_no, String supplierCode, String supplierName, int qty_manifest, String partNo,
                                  int qty_part, String serial, String npk_id,String NPK_RCV, String version, String deviceID,String DeviceRCV,String Manual_Complete) {
        boolean returns = false;
        try {
            ResultSet Rs ;
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                return  false;
            }else {
                String query = "";
                query = "exec [SP_RECEIVING_UPLOAD] '" + manifestNo + "','" + dn_no + "','" + supplierCode + "','" + supplierName + "'" +
                        ",'" + qty_manifest + "','" + partNo + "','" + qty_part + "','" + serial + "','" + npk_id + "','" + NPK_RCV + "','" + version + "','" + deviceID + "','" + DeviceRCV + "','" + Manual_Complete + "'";
                PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs = ps.executeQuery();
                if (Rs != null) {
                    try {
                        if (Rs.next()) {
                            returns = true;
                        }
                    } catch (SQLException exception) {
                        returns = false;
                        exception.printStackTrace();
                    }
                }
            }
            return  returns;
        } catch (Exception e) {
            return false;
        }
    }

    public String CheckManifestDownload(String role,String Status) {
        try {
            String Check = null;
            ResultSet Rs ;
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                return  null;
            }else {
                String query = "";
                if (role.equals("Operator Receiving")) {
                    query = "SELECT * from SATO_RECEIVING WHERE Status_Manifest<>'Complete'";
                } else {
                    query = "[SP_RECEIVING_DOWNLOAD] 'Operator QC','" + E_user.UserId + "','" + E_user.DeviceID + "'";
                }
                PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs = ps.executeQuery();
                if (Rs != null) {
                    try {
                        if (Rs.next()) {
                            Check = "ADA DATA";
                        }else{
                            ResultSet Rs2 ;
                            String query2 = "[SP_RECEIVING_DOWNLOAD] 'Operator QC','QC ADA'";
                            PreparedStatement ps2 = connect.prepareStatement(query);
                            Log.e("query", query2);
                            Rs2 = ps2.executeQuery();
                            if (Rs2 != null) {
                                try {
                                    if (Rs2.next()) {
                                        Check = "ADA DATA";
                                    }else{
                                        Check = "TIDAK ADA DATA";
                                    }
                                } catch (SQLException exception) {
                                    exception.printStackTrace();
                                }
                            }else{
                                Check = "TIDAK ADA DATA";
                            }
                        }
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                }else{
                    Check = "TIDAK ADA DATA";
                }
            }
            return  Check;
        } catch (Exception e) {
            return "ER - " + e.toString();
        }
    }
    public ResultSet GetDataDownload(String role) {
        try {
            ResultSet Rs ;
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                return  null;
            }else {
                String query = "";
                if (role.equals("Operator Receiving")) {
                    query = "EXEC [SP_RECEIVING_DOWNLOAD] 'Operator Receiving','',''" ;
                } else {
                    query = "EXEC [SP_RECEIVING_DOWNLOAD] 'Operator QC','" + E_user.UserId + "','" + E_user.DeviceID + "'" ;
                }
                Statement statement = connect.createStatement();
                PreparedStatement pstmt ;
                pstmt=connect.prepareStatement(query,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE+1);
                //PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs = pstmt.executeQuery();
            }
            /*while(Rs.next()) {
                if(Rs.isLast()) {
                    int xx = Rs.getRow();
                    // is last row in ResultSet
                }
            }
            Rs.beforeFirst();*/
            return  Rs;
        } catch (Exception e) {
            return null;
        }
    }

    public String UploadDataQC(String manifestno,
                                String Part_No,  int qty_scan, int qty_scan_qc, String serial, String serial_qc, String date_scan,
                                String date_scan_qc, String npk_id, String npk_id_qc, String status_part, String remark, String status_process,
                                String deviceID, String status_scan,String Status_Quarantine,String Date_Quarantine,int result) {
        String returns = null;
        try {
            ResultSet Rs ;
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                returns = "ER - TIDAK KONEK DATABASE";
            }else {
                String query = "";
                query = "exec [SP_QC_UPLOAD] '" +
                        manifestno + "','" +
                        Part_No + "','" +
                        qty_scan + "','" +
                        qty_scan_qc + "','" +
                        serial + "','" +
                        serial_qc + "'" + ",'" +
                        date_scan + "','" +
                        date_scan_qc + "','" +
                        npk_id + "','" +
                        npk_id_qc + "','" +
                        status_part + "','" +
                        remark + "','" +
                        status_process + "','" +
                        deviceID + "'" + ",'" +
                        status_scan + "','" +
                        Status_Quarantine + "','" +
                        Date_Quarantine + "','" +
                        result + "','" +
                        E_user.Version + "'";
                        PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs = ps.executeQuery();
                if (Rs != null) {
                    try {
                        if (Rs.next()) {
                            String Status_Scan = Rs.getString("STS");
                            returns = Status_Scan;
                        }
                    } catch (SQLException exception) {
                        returns = "ER - " + exception.toString();;
                        exception.printStackTrace();
                    }
                }
            }
            connect.close();
            return  returns;
        } catch (Exception e) {
            return "ER - " + e.toString();
        }

    }
    public String UploadDataQCManifest(String manifestno, String dn_no, String suplier_code, String suplier_name,
                                       int qty_scan_awal, String part_no, String status_quarantine, String date_quarantine, int qty_manifest, int qty_sampling,String DeviceID,String NPK_ID) {
        String returns = null;
        try {
            ResultSet Rs ;
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                returns = "ER - TIDAK KONEK DATABASE";
            }else {
                String query = "";
                query = "exec [SP_QC_UPLOAD_MANIFEST] '" +
                        manifestno + "','" +
                        dn_no + "','" +
                        suplier_code + "','" +
                        suplier_name + "'" +",'"+
                        qty_manifest + "','" +
                        qty_scan_awal + "','" +
                        part_no + "','" +
                        status_quarantine + "','" +
                        date_quarantine + "','" +
                        qty_sampling + "','" +
                        DeviceID + "','" +
                        NPK_ID + "','" +
                        E_user.Version + "'";

                PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs = ps.executeQuery();
                if (Rs != null) {
                    try {
                        if (Rs.next()) {
                            String Status_Scan = Rs.getString("STS");
                            returns = Status_Scan;
                        }
                    } catch (SQLException exception) {
                        returns = "ER - " + exception.toString();;
                        exception.printStackTrace();
                    }
                }
            }
            return  returns;
        } catch (Exception e) {
            return "ER - " + e.toString();
        }

    }

    public String CheckManifestComplete(String manifestNo, String partNo, String role) {
        String returns = null;
        try {
            ResultSet Rs ;
            Connection connect = ConnectionHelper.CONN();
            if (connect == null){
                returns = "ER - TIDAK KONEK DATABASE";
            }else {
                String query = "";
                query = "exec [SATO_CHECK_MANIFESTCOMPLETE] '" +
                        manifestNo + "','" +
                        partNo + "','" + role + "';";
                PreparedStatement ps = connect.prepareStatement(query);
                Log.e("query", query);
                Rs = ps.executeQuery();
                if (Rs != null) {
                    try {
                        if (Rs.next()) {
                            String Status_Scan = Rs.getString("STATUS_MANIFEST");
                            returns = Status_Scan;
                        }
                    } catch (SQLException exception) {
                        returns = "ER - " + exception.toString();;
                        exception.printStackTrace();
                    }
                }else{
                    return "NOT COMPLETE";
                }
            }
            return  returns;
        } catch (Exception e) {
            return "ER - " + e.toString();
        }
    }
}
