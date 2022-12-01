package com.example.rcs_taci_2.Dataacces;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.Display;

import com.example.rcs_taci_2.Entity.E_Manifest;
import com.example.rcs_taci_2.Entity.E_user;
import com.example.rcs_taci_2.Helper.ConnectionHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

public class DataaccesOffline {

    public static int QTY_SCAN_KANBAN = 0;
    public static  int QTY_SAMPLING = 0;
    public static int QTY_SCAN_NORMAL = 0;
    public static int QCQTYPART = 0;
    public static int QTY_SCAN = 0;
    public static String Error;
    myDbHelper helper;

    public DataaccesOffline(Context context)
    {
        helper = new myDbHelper(context);
    }




    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "rcs_taci.db";
        private static final int DATABASE_Version = 1;
        private static final String UID="_id";
        private static final String NAME = "Name";
        private static final String PASSWORD= "Password";

        // private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
        private Context context;

        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context=context;
            // Message.message(context,"Started...");
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                //create table again
            }catch (Exception e) {
                // Message.message(context,""+e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                //create table again
                onCreate(db);
            }catch (Exception e) {
                // Message.message(context,""+e);
            }
        }
    }
    public void CreateTable(){
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            String RCV = "";
            RCV = "CREATE TABLE IF NOT EXISTS [SATO_RECEIVING](" +
                    "[ID_Trans] INTEGER NOT NULL DEFAULT ((0))," +
                    "[ManifestNo] [varchar](50) NULL," +
                    "[DN_No] [varchar](50) NULL," +
                    "[Suplier_Code] [varchar](30) NULL," +
                    "[Suplier_Name] [varchar](50) NULL," +
                    "[Part_No] [varchar](50) NULL," +
                    "[Qty_Manifest] [int] NOT NULL DEFAULT ((0))," +
                    "[Qty_Part] [int] NOT NULL," +
                    "[Qty_Scan_Awal] [int] DEFAULT ((0))," +
                    "[NPK_ID] [varchar](15) NOT NULL," +
                    "[Date_Scan] [datetime] NULL," +
                    "[Status_Receiving] [varchar](20) NULL," +
                    "[Date_Receiving] [datetime] NULL," +
                    "[Date_Complete_Receiving] [datetime] NULL," +
                    "[Status_Manifest] [varchar](20) NULL," +
                    "[DeviceID] [varchar](2) NULL," +
                    "[Manual_Complete] [varchar](2) NULL)";
            db.execSQL(RCV);

            String QC = "";
            QC ="CREATE TABLE IF NOT EXISTS [SATO_QC](" +
                    "[ID_Trans] INTEGER NOT NULL DEFAULT ((0))," +
                    "[ManifestNo] [varchar](50) NULL," +
                    "[DN_No] [varchar](50) NULL," +
                    "[Suplier_Code] [varchar](30) NULL," +
                    "[Suplier_Name] [varchar](50) NULL," +
                    "[Part_No] [varchar](50) NULL," +
                    "[Qty_Manifest] [int] NOT NULL DEFAULT ((0))," +
                    "[Qty_Part] [int] NOT NULL," +
                    "[Qty_Sampling] [int] NOT NULL," +
                    "[Qty_Scan_Normal] [int] NOT NULL," +
                    "[Qty_Scan_Kanban] [int] NOT NULL," +
                    "[Qty_Scan_Awal] [int] NOT NULL  DEFAULT ((0))," +
                    "[Qty_NG] [int] NOT NULL DEFAULT ((0))," +
                    "[Date_Scan] [datetime] NULL," +
                    "[Status_QC] [varchar](20) NULL," +
                    "[Status_Quarantine] [varchar](20) NOT NULL  DEFAULT ('No')," +
                    "[Date_Quarantine] [datetime] NULL," +
                    "[Date_Complete_Part] [datetime] NULL," +
                    "[NPK_ID] [varchar](15) NOT NULL," +
                    "[Status_Manifest] [varchar](15) NULL," +
                    "[Status_Uploaded] [varchar](15) NULL," +
                    "[DeviceID] [varchar](2) NULL)";
            db.execSQL(QC);

            String History = "";
            History= "CREATE TABLE IF NOT EXISTS [SATO_HISTORY](" +
                    "[ID_History] INTEGER NOT NULL DEFAULT ((0)) PRIMARY KEY AUTOINCREMENT," +
                    "[ID_Trans] [int] NOT NULL DEFAULT ((0))," +
                    "[ManifestNo] [varchar](50) NULL DEFAULT ''," +
                    "[PartNo] [varchar](50) NOT NULL," +
                    "[Qty_Scan] [int] NOT NULL DEFAULT ((0))," +
                    "[Qty_Scan_QC] [int] NOT NULL  DEFAULT ((0))," +
                    "[Serial] [varchar](14) NOT NULL," +
                    "[Serial_QC] [varchar](14) NULL," +
                    "[Date_Scan] [datetime] NULL," +
                    "[Date_Scan_QC] [datetime] NULL," +
                    "[NPK_ID] [varchar](20) NULL," +
                    "[NPK_ID_QC] [nchar](10) NULL," +
                    "[Status_Part] [varchar](20) NULL," +
                    "[Remark] [varchar](100) NULL," +
                    "[Status_Process] [varchar](20) NULL," +
                    "[Version] [varchar](15) NULL," +
                    "[DeviceID] [varchar](2) NULL," +
                    "[Status_Scan] [varchar](2) NULL)";
            db.execSQL(History);
            //Message.message(context,"TABLE CREATED");
        } catch (Exception e) {
            Log.e("Error",e.toString());
        }finally {
            db.close();
        }
    }
    public boolean InsertManifestPerPart(E_Manifest e_manifest) {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            String Sql = "INSERT INTO SATO_RECEIVING([ManifestNo],[DN_No],[Suplier_Code],[Suplier_Name]," +
                    "[Part_No],[Qty_Manifest],[NPK_ID],[Status_Receiving],Qty_Part,Status_Manifest,DeviceID)" +
                    "VALUES('"+ e_manifest.getManifestNo()  +"','"+ e_manifest.getDnNo() + "' ,'" +e_manifest.getSupplierCode() + "'," +
                    "'" + e_manifest.getSupplierName() + "','" + e_manifest.getPartNo().trim() + "','" +e_manifest.getQtyManifest() + "','" + E_user.UserId + "'," +
                    "'Not Yet Scanned',0,'Not Yet Scanned','" + E_user.DeviceID + "')";
            db.execSQL(Sql);
            return true;
        }catch (Exception ex){
            return false;
        }
    }

    public String CheckManifestNo(E_Manifest e_manifest) {
        try{
            String Data ="";
            Cursor cursor;
            SQLiteDatabase db = helper.getWritableDatabase();
            String Sql = "select Status_Receiving,Status_Manifest from SATO_RECEIVING where ManifestNo='" + e_manifest.getManifestNo() + "'" +
                    " AND Part_No='" + e_manifest.getPartNo().trim() + "'";
            cursor = db.rawQuery(Sql,null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0){
                    String stsReceiving = cursor.getString(0);
                    String stsManifest = cursor.getString(1);
                    if (stsManifest.equals("Complete")) {
                        Data = "Complete";
                    }else {
                        Data = stsManifest;
                    }
            }else{
                Data = "INSERT";
            }
            return  Data;
        }catch (Exception ex){
            return  "ER - " + ex.toString();
        }
    }

    public String CheckManifestNoQC1(E_Manifest e_manifest) {
        try{
            String Data ="";

                    SQLiteDatabase db = helper.getWritableDatabase();
                    Cursor cursor1;
                    String Sql1 = "SELECT Status_Manifest from SATO_QC WHERE ManifestNo='" + e_manifest.getManifestNo() + "' ORDER BY Status_QC desc Limit 1";
                    cursor1 = db.rawQuery(Sql1,null);
                    cursor1.moveToFirst();
                    if (cursor1.getCount() != 0){
                        String stsQC = cursor1.getString(0);
                        if (stsQC.equals("Complete")) {
                            Data = "ER - Manifest Already Complete";
                        }else{
                            Data = "Lanjut";
                        }
                    }else{
                        Data = "ER - Manifest Not Found!";
                    }
            return  Data;
        }catch (Exception ex){
            return  "ER - " + ex.toString();
        }
    }
    public String CheckManifestQc(E_Manifest e_manifest) {
        try{
            String Data ="";
            Cursor cursor;
            SQLiteDatabase db = helper.getWritableDatabase();
            String Sql = "SELECT Status_Manifest from SATO_QC WHERE ManifestNo='" + e_manifest.getManifestNo() + "' Limit 1";
            cursor = db.rawQuery(Sql,null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0){
                String stsReceiving = cursor.getString(0);
                if (stsReceiving.equals("Complete")) {
                    Data = "ER - Manifest Already Complete";
                }else{
                    Data = stsReceiving;
                }
            }else{
               //Insert
                String strSQL = "INSERT INTO SATO_QC([ManifestNo],[DN_No],[Suplier_Code],[Suplier_Name],[Part_No],[Qty_Manifest],[NPK_ID],Status_QC,Qty_Part,Qty_Sampling" +
                        ",Qty_Scan_Kanban,Qty_Scan_Normal,Status_Manifest,DeviceID)" +
                        "SELECT ManifestNo,DN_No,Suplier_Code,Suplier_Name,Part_No,Qty_Part,'" + E_user.UserId + "','Not Yet Scanned',0," +
                        "CASE WHEN Qty_Part > 5	AND Qty_Part <= 100 THEN 5 " +
                        "WHEN Qty_Part > 101 AND Qty_Part <= 300 THEN 10 " +
                        "WHEN Qty_Part > 301 AND Qty_Part <= 500 THEN 20 " +
                        "WHEN Qty_Part > 501 AND Qty_Part <= 1200 THEN 32 " +
                        "WHEN Qty_Part > 1201 AND Qty_Part <= 3200 THEN 50 " +
                        "WHEN Qty_Part > 3201 AND Qty_Part <= 10000 THEN 80 " +
                        "WHEN Qty_Part > 10001 AND Qty_Part <= 35000 THEN 125 " +
                        "WHEN Qty_Part < 5 THEN Qty_Part " +
                        "END AS result,0,0,'Not Yet Scanned','" + E_user.DeviceID + "' From SATO_RECEIVING WHERE " +
                        "ManifestNo='" + e_manifest.getManifestNo() + "' and DN_No='"+e_manifest.getDnNo() +"' AND Qty_Part<>0";
                db.execSQL(strSQL);
                Data = "Not Yet Scanned";
            }
            return  Data;
        }catch (Exception ex){
            return  "ER - " + ex.toString();
        }
    }
    public String CheckKanbanReceiving(String ManifestNo, String suplierCode, String partNo, int qty_kanban, String serial) {
        try{
            String Data ="";
            Cursor cursor;
            SQLiteDatabase db = helper.getWritableDatabase();
            String Sql = "SELECT Status_Receiving,ID_TRANS, Qty_Manifest, Qty_Part , Qty_Scan_Awal" +
                    " from SATO_RECEIVING WHERE ManifestNo='" + ManifestNo + "' AND Part_No='" + partNo.trim() + "'";
            cursor = db.rawQuery(Sql,null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0){
                String Status_Receiving = cursor.getString(0);
                int  ID_TRANS = cursor.getInt(1);
                int  Qty_Manifest = cursor.getInt(2);
                int  Qty_Part = cursor.getInt(3);
                int  Qty_Scan_Awal = cursor.getInt(4);

               if (Qty_Part >= Qty_Manifest){
                   Data = "ER - Part No Already Complete";
               }else if ((Qty_Part+qty_kanban)>Qty_Manifest){
                   Data = "ER - Qty Kanban Over Total Qty Part";
               }else{
                    Cursor cursor1;
                   String strSQL = "SELECT Serial from SATO_HISTORY WHERE Serial='" + serial + "' AND PartNo='" + partNo + "'" +
                           " AND ManifestNo='" + ManifestNo + "'";
                   cursor1 = db.rawQuery(strSQL,null);
                   cursor1.moveToFirst();
                   if (cursor1.getCount() != 0) {
                       Data = "ER - Double Serial No";
                   }else{
                        if (Qty_Scan_Awal == 0){
                            String strInsert = "INSERT INTO SATO_HISTORY(ID_TRANS,ManifestNo,QTY_SCAN,SERIAL,DATE_SCAN,NPK_ID,[Version],PartNo,DeviceID)VALUES" +
                                    " (" + ID_TRANS + " ,'" + ManifestNo + "'," + qty_kanban  + ",'" + serial + "','" + getCurrentTime() + "','" + E_user.UserId + "'" +
                                    ",'" + E_user.Version + "','" + partNo.trim() + "','" + E_user.DeviceID + "')";
                            db.execSQL(strInsert);

                           String strUpdate = "UPDATE [SATO_RECEIVING] SET QTY_SCAN_AWAL=" + qty_kanban + ", Qty_Part=Qty_Part+" + qty_kanban + " , " +
                                   "Date_Scan='" + getCurrentTime() + "',Status_Receiving='Partial Complete' where ManifestNo=" + ManifestNo + " AND Part_No='" + partNo.trim() +"';";
                            db.execSQL(strUpdate);
                            Cursor cursor2;
                            String strSQL2 = "SELECT Qty_Part from " +
                                    "SATO_RECEIVING WHERE ManifestNo='" + ManifestNo + "' AND Part_No='" + partNo.trim() +"';";
                            cursor2 = db.rawQuery(strSQL2,null);
                            cursor2.moveToFirst();
                            if (cursor2.getCount() != 0) {
                                int  Qty_Scan = cursor2.getInt(0);
                                E_user.QtySCNRCV = Qty_Scan;
                                E_user.QtyManifestRCV = Qty_Manifest;
                                if (Qty_Scan >= Qty_Manifest){
                                    String strUpdate1 ="UPDATE [SATO_RECEIVING] SET Status_Receiving='Complete' where ManifestNo='" + ManifestNo + "' AND Part_No='" + partNo.trim() +"';";
                                    db.execSQL(strUpdate1);

                                    Cursor cursor3;
                                    cursor3 = db.rawQuery("SELECT Status_Receiving from " +
                                            "SATO_RECEIVING WHERE ManifestNo='" + ManifestNo + "' Order by Status_Receiving desc  Limit 1 ",null);
                                    cursor3.moveToFirst();
                                    if (cursor3.getCount() != 0) {
                                        String stsReceiving = cursor3.getString(0);
                                        if (stsReceiving.equals("Complete")){
                                            Data ="Manifest Complete";
                                            db.execSQL("UPDATE [SATO_RECEIVING] SET Status_Manifest='Complete',Date_Complete_Receiving='" + getCurrentTime() + "' " +
                                                    "where ManifestNo='" + ManifestNo + "'");
                                        }else{
                                            Data ="Part Complete";
                                            db.execSQL("UPDATE [SATO_RECEIVING] SET Status_Manifest='Process' where ManifestNo='" + ManifestNo + "'");
                                        }
                                    }

                                }else{
                                    Data ="LANJUT";
                                    db.execSQL("UPDATE SATO_RECEIVING SET Status_Manifest='Process' where ManifestNo='" + ManifestNo + "'");
                                }

                            }else{
                                Data = "ER - Part No not found!";
                            }

                        }else{
                            // Jika Qty Awal Tidak Kosong / Sudah Ada
                            if (qty_kanban == Qty_Scan_Awal){
                                String strInsert = "INSERT INTO SATO_HISTORY(ID_TRANS,ManifestNo,QTY_SCAN,SERIAL,DATE_SCAN,NPK_ID,[Version],PartNo,DeviceID)VALUES" +
                                        " (" + ID_TRANS + " ,'" + ManifestNo + "'," + qty_kanban  + ",'" + serial + "','" + getCurrentTime() + "','" + E_user.UserId + "'" +
                                        ",'" + E_user.Version + "','" + partNo + "','" + E_user.DeviceID + "')";
                                db.execSQL(strInsert);
                                String strUpdate = "UPDATE [SATO_RECEIVING] SET Qty_Part=Qty_Part+ " + qty_kanban + " , " +
                                        "Date_Scan='" + getCurrentTime() + "',Status_Receiving='Partial Complete' where ManifestNo='" + ManifestNo + "' AND Part_No='" + partNo.trim() +"'";
                                db.execSQL(strUpdate);
                                Cursor cursor2;
                                String strSQL2 = "SELECT Qty_Part from " +
                                        "SATO_RECEIVING WHERE ManifestNo='" + ManifestNo + "' AND ManifestNo='" + ManifestNo + "' AND Part_No='" + partNo.trim() +"'";
                                cursor2 = db.rawQuery(strSQL2,null);
                                cursor2.moveToFirst();
                                if (cursor2.getCount() != 0) {
                                    int  Qty_Scan = cursor2.getInt(0);
                                    E_user.QtySCNRCV = Qty_Scan;
                                    E_user.QtyManifestRCV = Qty_Manifest;
                                    if (Qty_Scan >= Qty_Manifest){
                                        String strUpdate1 ="UPDATE [SATO_RECEIVING] SET Status_Receiving='Complete' where ManifestNo='" + ManifestNo + "' AND Part_No='" + partNo.trim() +"'";
                                        db.execSQL(strUpdate1);

                                        Cursor cursor3;
                                        cursor3 = db.rawQuery("SELECT Status_Receiving from " +
                                                "SATO_RECEIVING WHERE ManifestNo='" + ManifestNo + "'  Order by Status_Receiving desc Limit 1",null);
                                        cursor3.moveToFirst();
                                        if (cursor3.getCount() != 0) {
                                            String stsReceiving = cursor3.getString(0);
                                            if (stsReceiving.equals("Complete")){
                                                Data ="Manifest Complete";
                                                db.execSQL("UPDATE [SATO_RECEIVING] SET Status_Manifest='Complete',Date_Complete_Receiving='" + getCurrentTime() + "' " +
                                                        "where ManifestNo='" + ManifestNo + "'");
                                            }else{
                                                Data ="Part Complete";
                                                db.execSQL("UPDATE [SATO_RECEIVING] SET Status_Manifest='Process' where ManifestNo='" + ManifestNo + "'");
                                            }
                                        }

                                    }else{
                                        Data ="LANJUT";
                                        db.execSQL("UPDATE SATO_RECEIVING SET Status_Manifest='Process' where ManifestNo='" + ManifestNo + "'");
                                    }

                                }else{
                                    Data = "ER - Part No not found!";
                                }

                            }else{
                               Data="ER - Different Qty Scan!";
                            }
                        }
                   }
               }
            }else{
               //Data Tidak Ada
                Data = "ER - Wrong Part No";
            }
            return  Data;
        }catch (Exception ex){
            return  "ER - " + ex.toString();
        }

    }
    public String CheckKanbanQC(String ManifestNo, String supplierCode, String partNo, int qty_scan, String serial) {
        try{
            String Data = "";
            Cursor cursor;
            SQLiteDatabase db = helper.getWritableDatabase();
            String Sql = "SELECT Status_QC,ID_TRANS, IFNULL(Qty_Manifest,0), IFNULL(Qty_Part,0), IFNULL(Qty_Sampling,0)," +
                    " IFNULL(QTY_SCAN_NORMAL,0),Status_Quarantine from SATO_QC WHERE ManifestNo='" + ManifestNo +"' AND Part_No='" + partNo + "' Limit 1";
            cursor = db.rawQuery(Sql,null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0) {
                String STS = cursor.getString(0);
                int ID_TRANS = cursor.getInt(1);
                int Qty_Manifest = cursor.getInt(2);
                int Qty_Part = cursor.getInt(3);
                int Qty_Sampling = cursor.getInt(4);
                int QTY_SCAN_NORMAL = cursor.getInt(5);

                DataaccesOffline.QTY_SCAN_NORMAL = QTY_SCAN_NORMAL;
                DataaccesOffline.QCQTYPART = Qty_Manifest;
                DataaccesOffline.QTY_SCAN = Qty_Part;
                String Status_Quarantine = cursor.getString(6);
                if (Status_Quarantine.equals("Ya")){
                    if (Qty_Part >=Qty_Manifest){
                        Data = "ER - Part No Already Complete|Ya";
                    }else if ((qty_scan+Qty_Part)>Qty_Manifest){
                        Data = "ER - Qty Kanban exceed Total Qty Part|Ya";
                    }else{
                        Cursor cursor1;
                        String Sql1 = "SELECT QTY_SCAN_QC from SATO_HISTORY WHERE Serial='" + serial + "' and PartNo='" +partNo+"' AND ManifestNo='" + ManifestNo  +"'";
                        cursor1 = db.rawQuery(Sql1,null);
                        cursor1.moveToFirst();
                        if (cursor1.getCount() != 0){
                            int QTY_SCAN_QC = cursor1.getInt(0);
                            if (QTY_SCAN_QC == 0){
                                Data = "LANJUT|Ya";
                            }else if (QTY_SCAN_QC >0){
                                Data = "ER - Serial Already Scanned|Ya";
                            }
                        }else{
                            Data = "ER - Serial Not Register|Ya";
                        }
                    }
                }else{
                    if (QTY_SCAN_NORMAL >= Qty_Sampling){
                        Data = "ER - Part No Already Complete|x";
                    }else{
                        Cursor cursor1;
                        String Sql1 = "SELECT QTY_SCAN_QC from SATO_HISTORY WHERE Serial='" + serial + "' and PartNo='" +partNo+"' AND ManifestNo='" + ManifestNo  +"'";
                        cursor1 = db.rawQuery(Sql1,null);
                        cursor1.moveToFirst();
                        if (cursor1.getCount() != 0){
                            int QTY_SCAN_QC = cursor1.getInt(0);
                            if (QTY_SCAN_QC == 0){
                                Data = "LANJUT|x";
                            }else if (QTY_SCAN_QC >0){
                                Data = "ER - Serial Already Scanned|x";
                            }
                        }else{
                            Data = "ER - Serial Not Register|x";
                        }
                    }
                }
            }else{
                Data = "ER - Wrong Part No|x";
            }
            return Data;
        }catch (Exception ex){
            return "ER - " + ex.toString() + "|x" ;
        }
    }
    public String SaveKanbanQC(String ManifestNo, String supplierCode, String partNo, int qty_scan, String serial, String ok, String s, int hasil) {
        try{
            String Data = "";
            Cursor cursor;
            SQLiteDatabase db = helper.getWritableDatabase();
            String Sql = "SELECT Status_QC,ID_TRANS, IFNULL(Qty_Manifest,0), IFNULL(Qty_Part,0), IFNULL(Qty_Sampling,0)," +
                    " IFNULL(QTY_SCAN_NORMAL,0),Status_Quarantine,IFNULL(Qty_Scan_Kanban,0) from SATO_QC WHERE ManifestNo='" + ManifestNo +"' AND Part_No='" + partNo + "' Limit 1";
            cursor = db.rawQuery(Sql,null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0) {
                String STS = cursor.getString(0);
                int ID_TRANS = cursor.getInt(1);
                int Qty_Manifest = cursor.getInt(2);
                int Qty_Part = cursor.getInt(3);
                int Qty_Sampling = cursor.getInt(4);
                DataaccesOffline.QTY_SAMPLING = Qty_Sampling;
                int QTY_SCAN_NORMAL = cursor.getInt(5);
                int Qty_Scan_Kanban1 = cursor.getInt(7);
                String Status_Quarantine = cursor.getString(6);
                if (Status_Quarantine.equals("Ya")){
                    if (Qty_Part >=Qty_Manifest){
                        Data = "ER - Part No Already Complete|Ya";
                    }else if ((qty_scan+Qty_Part)>Qty_Manifest){
                        Data = "ER - Qty Kanban exceed Total Qty Part|Ya";
                    }else{
                        Cursor cursor1;
                        String Sql1 = "SELECT Serial from SATO_HISTORY WHERE Serial_QC='" + serial + "' and PartNo='" +partNo+"' AND ManifestNo='" + ManifestNo  +"'";
                        cursor1 = db.rawQuery(Sql1,null);
                        cursor1.moveToFirst();
                        if (cursor1.getCount() == 0){
                            db.execSQL("UPDATE SATO_HISTORY SET SERIAL_QC='" +serial+"',Qty_Scan_QC=" +qty_scan +",Date_Scan_QC='" +getCurrentTime() + "'," +
                                    "NPK_ID_QC='" + E_user.UserId + "' ,Status_Part='" + ok + "',Remark='" + s + "'" +
                                    " WHERE Serial='" + serial + "' AND ManifestNo='" + ManifestNo + "' AND PartNo='" + partNo + "'");

                            if (ok.equals("OK")) {
                                db.execSQL("UPDATE SATO_QC SET Qty_Part=Qty_Part+" + qty_scan + " , Date_Scan='" +getCurrentTime() +"',Status_QC='Partial Complete' ," +
                                        "Qty_Scan_Normal=Qty_Scan_Normal+" + qty_scan + ",Qty_Scan_Kanban=Qty_Scan_Kanban+1 where ManifestNo='" + ManifestNo + "' AND Part_No='" + partNo.trim() +"'");
                            }else{
                                db.execSQL("UPDATE SATO_QC SET Qty_Part=Qty_Part+" + qty_scan + " , Date_Scan='" +getCurrentTime() +"',Status_QC='Partial Complete' ," +
                                        "Qty_Scan_Normal=Qty_Scan_Normal+" + qty_scan + ",Qty_Scan_Kanban=Qty_Scan_Kanban+1,QTY_NG=QTY_NG+" + qty_scan + " where ManifestNo='" + ManifestNo +"' AND Part_No='" + partNo.trim() +"';");
                            }
                            Cursor cursor2;
                            String Sqlx = "SELECT Qty_Manifest, Qty_Part, Qty_Scan_Kanban,Qty_Scan_Normal from SATO_QC WHERE ManifestNo='" + ManifestNo + "' AND Part_No='" + partNo.trim() +"'";
                            cursor2 = db.rawQuery(Sqlx,null);
                            cursor2.moveToFirst();
                            if (cursor2.getCount() != 0) {
                                int QTY_PART_SHOW = cursor2.getInt(0);
                                DataaccesOffline.QCQTYPART = QTY_PART_SHOW;
                                int QTY_SCAN = cursor2.getInt(1);
                                DataaccesOffline.QTY_SCAN = QTY_SCAN;
                                int Qty_Scan_Kanban = cursor2.getInt(2);
                                DataaccesOffline.QTY_SCAN_KANBAN = Qty_Scan_Kanban;
                                int Qty_Scan_Normal = cursor2.getInt(3);
                                if (QTY_SCAN>= QTY_PART_SHOW){
                                    db.execSQL("UPDATE SATO_QC SET Status_QC='Complete' where  ManifestNo='" + ManifestNo + "' AND Part_No='" + partNo.trim() +"'");
                                    Cursor cursor3;
                                    cursor3 = db.rawQuery("SELECT Status_QC from SATO_QC WHERE ManifestNo='" + ManifestNo + "' Order by Status_QC desc Limit 1",null);
                                    cursor3.moveToFirst();
                                    if (cursor3.getCount() != 0) {
                                        String Status_QC = cursor3.getString(0);
                                        if (Status_QC.equals("Complete")){
                                            Data ="Manifest Complete";
                                            db.execSQL("UPDATE SATO_QC SET Status_Manifest='Complete' where ManifestNo='" +ManifestNo + "'");
                                        }else{
                                            Data ="Part Complete";
                                            db.execSQL("UPDATE SATO_QC SET Status_Manifest='Process' where ManifestNo='" +ManifestNo + "'");
                                        }
                                    }
                                }else{
                                    Data = "LANJUT";
                                    db.execSQL("UPDATE SATO_QC SET Status_Manifest='Process' where ManifestNo='" + ManifestNo + "'");
                                }
                            }else{
                                Data = "ER - Double Serial No";
                            }
                        }
                        //sudah ditutup
                    }
                    //sudah ditutup
                }else{
                    if (Qty_Scan_Kanban1 >= hasil ){
                        Data = "ER - Part No Already Complete";
                    }else{
                        Cursor cursor1;
                        String Sql1 = "SELECT Serial from SATO_HISTORY WHERE Serial_QC='" + serial + "' and PartNo='" +partNo+"' AND ManifestNo='" + ManifestNo  +"'";
                        cursor1 = db.rawQuery(Sql1,null);
                        cursor1.moveToFirst();
                        if (cursor1.getCount() == 0){
                            db.execSQL("UPDATE SATO_HISTORY SET SERIAL_QC='" +serial+"',Qty_Scan_QC=" +qty_scan +",Date_Scan_QC='" +getCurrentTime() + "'," +
                                    "NPK_ID_QC='" +E_user.UserId + "' ,Status_Part='" + ok + "',Remark='" + s + "'" +
                                    " ,Status_Scan='OK' WHERE Serial='" + serial + "' AND ManifestNo='" + ManifestNo + "' AND PartNo='" + partNo + "'");

                            if (ok.equals("OK")) {
                                db.execSQL("UPDATE SATO_QC SET Qty_Part=Qty_Part+" + qty_scan + " , Date_Scan='" +getCurrentTime() +"',Status_QC='Partial Complete' ," +
                                        "Qty_Scan_Normal=Qty_Scan_Normal+" + qty_scan + ",Qty_Scan_Kanban=Qty_Scan_Kanban+1 where ManifestNo='" + ManifestNo + "' AND Part_No='" + partNo.trim() +"'");
                            }else{
                                db.execSQL("UPDATE SATO_QC SET Qty_Part=Qty_Part+" + qty_scan + " , Date_Scan='" +getCurrentTime() +"',Status_QC='Partial Complete' ," +
                                        "Qty_Scan_Normal=Qty_Scan_Normal+" + qty_scan + ",Qty_Scan_Kanban=Qty_Scan_Kanban+1,QTY_NG=QTY_NG+" + qty_scan + " where ManifestNo='" + ManifestNo + "' AND Part_No='" + partNo.trim() +"'");
                            }
                            Cursor cursor2;
                            String Sqlx = "SELECT Qty_Manifest, Qty_Part, Qty_Scan_Kanban,Qty_Scan_Normal from SATO_QC WHERE ManifestNo='" + ManifestNo + "' AND Part_No='" + partNo.trim() +"'";
                            cursor2 = db.rawQuery(Sqlx,null);
                            cursor2.moveToFirst();
                            if (cursor2.getCount() != 0) {
                                int QTY_PART_SHOW = cursor2.getInt(0);
                                int QTY_SCAN = cursor2.getInt(1);
                                int Qty_Scan_Kanban = cursor2.getInt(2);
                                int Qty_Scan_Normal = cursor2.getInt(3);
                                DataaccesOffline.QTY_SCAN_NORMAL = Qty_Scan_Normal;

                                if (Qty_Scan_Kanban>= hasil){
                                    db.execSQL("UPDATE SATO_QC SET Status_QC='Complete' where ManifestNo='" + ManifestNo + "' AND Part_No='" + partNo.trim() +"'");
                                    Cursor cursor3;
                                    cursor3 = db.rawQuery("SELECT Status_QC from SATO_QC WHERE ManifestNo='" + ManifestNo + "' Order by Status_QC desc Limit 1",null);
                                    cursor3.moveToFirst();
                                    if (cursor3.getCount() != 0) {
                                        String Status_QC = cursor3.getString(0);
                                        if (Status_QC.equals("Complete")){
                                            Data ="Manifest Complete";
                                            db.execSQL("UPDATE SATO_QC SET Status_Manifest='Complete' where ManifestNo='" +ManifestNo + "'");
                                            db.execSQL("UPDATE SATO_QC SET Qty_Part=Qty_Manifest where ManifestNo='" + ManifestNo +"'" +
                                                    " and Part_No='" + partNo +"'");

                                            db.execSQL("Update SATO_HISTORY SET Qty_Scan_QC=Qty_Scan,Serial_QC=Serial , " +
                                                            "NPK_ID_QC='" +E_user.UserId + "',Status_Part='OK' ,Date_Scan_QC='" +getCurrentTime() + "'" +
                                                    " where ManifestNo='" + ManifestNo + "' and PartNo='" + partNo + "' AND Serial_QC IS NULL");

                                        }else{
                                            Data ="Part Complete";
                                            db.execSQL("UPDATE SATO_QC SET Status_Manifest='Process' where ManifestNo='" +ManifestNo + "'");
                                        }
                                    }
                                }else{
                                    Data = "LANJUT";
                                    db.execSQL("UPDATE SATO_QC SET Status_Manifest='Process' where ManifestNo='" + ManifestNo + "'");
                                }
                            }else{
                                Data = "ER - Double Serial No";
                            }
                        }else{
                            Data = "ER - Double Serial No";
                        }
                    }
                }
            }else{
                Data = "ER - Wrong Part No|x";
            }
            return Data;
        }catch (Exception ex){
            return "ER - " + ex.toString() + "|x" ;
        }
    }
    public String CheckKanbanQCFirst(String ManifestNo, String supplierCode, String partNo, int qty_scan, String serial) {
        try{
            String Data = "";
            Cursor cursor;
            SQLiteDatabase db = helper.getWritableDatabase();
            String Sql = "SELECT Status_QC,ID_TRANS, IFNULL(Qty_Manifest,0), IFNULL(Qty_Part,0), IFNULL(Qty_Sampling,0)," +
                    " IFNULL(QTY_SCAN_NORMAL,0),Status_Quarantine,IFNULL(Qty_Scan_Kanban,0) , [Qty_Scan_Awal] from SATO_QC WHERE ManifestNo='" + ManifestNo +"' AND Part_No='" + partNo + "' Limit 1";
            cursor = db.rawQuery(Sql,null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0) {
                String STS = cursor.getString(0);
                int ID_TRANS = cursor.getInt(1);
                int Qty_Manifest = cursor.getInt(2);
                int Qty_Part = cursor.getInt(3);
                int Qty_Sampling = cursor.getInt(4);
                int QTY_SCAN_NORMAL = cursor.getInt(5);
                String Status_Quarantine = cursor.getString(6);
                int Qty_Scan_Kanban = cursor.getInt(7);
                int Qty_Scan_Awal = cursor.getInt(8);

                DataaccesOffline.QTY_SCAN_KANBAN = Qty_Scan_Kanban;
                DataaccesOffline.QTY_SCAN_NORMAL = QTY_SCAN_NORMAL;
                DataaccesOffline.QCQTYPART = Qty_Manifest;
                DataaccesOffline.QTY_SCAN = Qty_Part;
                DataaccesOffline.QTY_SAMPLING = Qty_Sampling;
                if (Status_Quarantine.equals("Ya")){
                    if (STS.equals("Complete")){
                        Data = "ER - Part No Already Complete|Ya";
                    }else if ((qty_scan+Qty_Part)>Qty_Manifest){
                        Data = "ER - Qty Kanban exceed Total Qty Part|Ya";
                    }else{
                        if (Qty_Scan_Awal != 0) {
                            if (Qty_Scan_Awal == qty_scan) {
                                Cursor cursor1;
                                String Sql1 = "SELECT QTY_SCAN_QC from SATO_HISTORY WHERE Serial='" + serial + "' and PartNo='" + partNo + "' AND ManifestNo='" + ManifestNo + "'";
                                cursor1 = db.rawQuery(Sql1, null);
                                cursor1.moveToFirst();
                                if (cursor1.getCount() != 0) {
                                    int QTY_SCAN_QC = cursor1.getInt(0);
                                    if (QTY_SCAN_QC == 0) {
                                        Data = "LANJUT|Ya";
                                    } else if (QTY_SCAN_QC > 0) {
                                        Data = "ER - Serial Already Scanned|Ya";
                                    }
                                } else {
                                    Data = "ER - Serial Not Register|Ya";
                                }
                            }else{
                               Data ="ER - Different Qty Scan|Ya";
                            }
                        }else{
                            Cursor cursor1;
                            String Sql1 = "SELECT QTY_SCAN_QC from SATO_HISTORY WHERE Serial='" + serial + "' and PartNo='" + partNo + "' AND ManifestNo='" + ManifestNo + "'";
                            cursor1 = db.rawQuery(Sql1, null);
                            cursor1.moveToFirst();
                            if (cursor1.getCount() != 0) {
                                int QTY_SCAN_QC = cursor1.getInt(0);
                                if (QTY_SCAN_QC == 0) {
                                    Data = "LANJUT|Ya";
                                    db.execSQL("UPDATE SATO_QC SET QTY_SCAN_AWAL=" + qty_scan + " WHERE ManifestNo='" + ManifestNo + "' " +
                                            "AND Part_No='" + partNo + "'");
                                } else if (QTY_SCAN_QC > 0) {
                                    Data = "ER - Serial Already Scanned|Ya";
                                }
                            } else {
                                Data = "ER - Serial Not Register|Ya";
                            }
                        }
                    }
                }else{
                    if (STS.equals("Complete")){
                        Data = "ER - Part No Already Complete|Tidak";
                    }else{
                        if (Qty_Scan_Awal != 0) {
                            if (Qty_Scan_Awal == qty_scan) {
                                Cursor cursor1;
                                String Sql1 = "SELECT QTY_SCAN_QC from SATO_HISTORY WHERE Serial='" + serial + "' and PartNo='" + partNo + "' AND ManifestNo='" + ManifestNo + "'";
                                cursor1 = db.rawQuery(Sql1, null);
                                cursor1.moveToFirst();
                                if (cursor1.getCount() != 0) {
                                    int QTY_SCAN_QC = cursor1.getInt(0);
                                    if (QTY_SCAN_QC == 0) {
                                        Data = "LANJUT|Tidak";
                                    } else if (QTY_SCAN_QC > 0) {
                                        Data = "ER - Serial Already Scanned|Tidak";
                                    }
                                } else {
                                    Data = "ER - Serial Not Register|Tidak";
                                }
                            }else{
                                Data ="ER - Different Qty Scan|Tidak";
                            }
                        }else{
                            Cursor cursor1;
                            String Sql1 = "SELECT QTY_SCAN_QC from SATO_HISTORY WHERE Serial='" + serial + "' and PartNo='" + partNo + "' AND ManifestNo='" + ManifestNo + "'";
                            cursor1 = db.rawQuery(Sql1, null);
                            cursor1.moveToFirst();
                            if (cursor1.getCount() != 0) {
                                int QTY_SCAN_QC = cursor1.getInt(0);
                                if (QTY_SCAN_QC == 0) {
                                    Data = "LANJUT|Tidak";
                                    db.execSQL("UPDATE SATO_QC SET QTY_SCAN_AWAL=" + qty_scan + " WHERE ManifestNo='" + ManifestNo + "' " +
                                            "AND Part_No='" + partNo + "'");
                                } else if (QTY_SCAN_QC > 0) {
                                    Data = "ER - Serial Already Scanned|Tidak";
                                }
                            } else {
                                Data = "ER - Serial Not Register|Tidak";
                            }
                        }
                    }
                }
            }else{
                Data = "ER - Wrong Part No|Tidak";
            }
            return Data;
        }catch (Exception ex){
            return "ER - " + ex.toString() + "|x" ;
        }
    }
    public String ManualComplete(String ManifestNo) {
        try {
            String Check = null;
            SQLiteDatabase db = helper.getWritableDatabase();
            Cursor cursor1;
            String Sql1 = "SELECT Status_Receiving from SATO_RECEIVING WHERE ManifestNo='" +ManifestNo + "'";
            cursor1 = db.rawQuery(Sql1, null);
            cursor1.moveToFirst();
            if (cursor1.getCount() != 0) {
                Check = "OK";
                db.execSQL("UPDATE SATO_RECEIVING SET Status_Receiving='Complete',Date_Complete_Receiving='" +getCurrentTime() +"' ," +
                        "Status_Manifest='Complete',Manual_Complete='YA' WHERE ManifestNo='" + ManifestNo + "' ;");
                db.execSQL("UPDATE SATO_RECEIVING SET Date_Scan='" +getCurrentTime() +"' ,Status_Manifest='Complete' WHERE " +
                        "ManifestNo='" + ManifestNo + "' AND Date_Scan IS NULL");
            } else {
                Check = "ER - Wrong Manifest No";
            }
            return  Check;
        } catch (Exception e) {
            return "ER - " + e.toString();
        }
    }
    public String SetQuarantine(String ManifestNo, String partNo) {
        try {
            String Check = null;
            SQLiteDatabase db = helper.getWritableDatabase();
            Cursor cursor1;
            String Sql1 = "SELECT Status_QC from SATO_QC WHERE ManifestNo='" +ManifestNo + "' AND Part_No='" +partNo + "'";
            cursor1 = db.rawQuery(Sql1, null);
            cursor1.moveToFirst();
            if (cursor1.getCount() != 0) {
                Check = "OK";
                db.execSQL("UPDATE SATO_QC SET Status_Quarantine='Ya',Date_Quarantine='" +getCurrentTime() +"'  " +
                                "WHERE ManifestNo='" + ManifestNo + "' AND Part_No='" +  partNo + "'");
            } else {
                Check = "ER - Part No Not Found";
            }
            return  Check;
        } catch (Exception e) {
            return "ER - " + e.toString();
        }
    }
    public Cursor GetQuarantineData(String ManifestNo, String partNo) {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            Cursor cursor1;
            String Sql1 = "SELECT QTY_SCAN_AWAL, Status_QC , IFNULL(Qty_Manifest,0)," +
                    " IFNULL(Qty_Part,0) FROM SATO_QC  WHERE ManifestNo='" + ManifestNo + "' AND Part_No='" + partNo + "'";
            cursor1 = db.rawQuery(Sql1, null);
            cursor1.moveToFirst();
            return cursor1;
        } catch (Exception e) {
            return null;
        }
    }
    public String InsertManifestPerPartOnline(int ID_Trans,String manifestNo, String dn_no, String suplier_code,
                                              String suplier_name, String part_no, int qty_manifest, int qty_part,
                                              int qty_scan_awal, String npk_id, String date_scan, String status_receiving,
                                              String date_receiving, String date_complete_receiving, String status_manifest, String deviceID) {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            String Sql = "INSERT INTO SATO_RECEIVING(ID_Trans,ManifestNo,DN_No,Suplier_Code,Suplier_Name,Part_No," +
                    "Qty_Manifest,Qty_Part,Qty_Scan_Awal,NPK_ID,Date_Scan,Status_Receiving,Date_Receiving," +
                    "Date_Complete_Receiving,Status_Manifest,DeviceID)" +
                    "VALUES(" +ID_Trans + ",'"+ manifestNo  +"','"+ dn_no + "' ,'" +suplier_code + "'," +
                    "'" + suplier_name + "','" + part_no + "','" +qty_manifest + "','" + qty_part + "'," +
                    "'" + qty_scan_awal+"'," +npk_id +",'" +date_scan + "','" + status_receiving + "','" + date_receiving +
                    "','" + date_complete_receiving + "','" + status_manifest + "','" + deviceID + "')";

            db.execSQL(Sql);
            return "OK";
        }catch (Exception ex){
            return  "Error InsertManifestPerPartOnline : " + ex.toString();
        }
    }
    public String InsertHistoryOnline(int id_history, int id_trans, String manifestNo, String partNo, int qty_scan,
                                      int qty_scan_qc, String serial, String serial_qc, String date_scan, String date_scan_qc,
                                      String npk_id, String npk_id_qc, String status_part, String remark, String status_process,
                                      String version, String deviceID, String status_scan) {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            String strInsert = "INSERT INTO SATO_HISTORY(id_history,ID_TRANS,ManifestNo,QTY_SCAN,SERIAL,DATE_SCAN,NPK_ID,[Version],PartNo,DeviceID,status_part,status_process,status_scan)VALUES" +
                " (" +id_history + "," + id_trans + " ,'" + manifestNo + "'," + qty_scan  + ",'" + serial + "','" + date_scan + "','" + npk_id + "'" +
                ",'" + version + "','" + partNo + "','" + deviceID + "','" + status_part + "','" + status_process + "','" + status_scan + "')";
        db.execSQL(strInsert);
            return "OK";
        }catch (Exception ex){  
            return  "Error InsertManifestPerPartOnline : " + ex.toString();
        }
    }
    public String DeleteOnline(String manifestNo) {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            String strInsert = "DELETE FROM SATO_HISTORY WHERE ManifestNo='" + manifestNo+"'";
            db.execSQL(strInsert);
            db.execSQL("DELETE FROM SATO_RECEIVING WHERE ManifestNo='" + manifestNo+"'");
            return "OK";
        }catch (Exception ex){
            return  "Error InsertManifestPerPartOnline : " + ex.toString();
        }
    }

    public String ScanKanbanBackupOnline(int ID_TRANS,String ManifestNo, int qty_kanban,String serial,String partNo,String Status ){
        try {
            //Check IdHistory2
            SQLiteDatabase db = helper.getWritableDatabase();
            if (Status.equals("LANJUT")){
                String strInsert = "INSERT INTO SATO_HISTORY(ID_TRANS,ManifestNo,QTY_SCAN,SERIAL,DATE_SCAN,NPK_ID,[Version],PartNo,DeviceID)VALUES" +
                        " (" + ID_TRANS + " ,'" + ManifestNo + "'," + qty_kanban  + ",'" + serial + "','" + getCurrentTime() + "','" + E_user.UserId + "'" +
                        ",'" + E_user.Version + "','" + partNo.trim() + "','" + E_user.DeviceID + "')";
                db.execSQL(strInsert);
                String strUpdate = "UPDATE SATO_RECEIVING SET Status_Manifest='Process', QTY_SCAN_AWAL='" + qty_kanban + "'," +
                        " Qty_Part=Qty_Part+" + qty_kanban+" , Date_Scan='" + getCurrentTime() + "',Status_Receiving='Partial Complete' " +
                        " where ManifestNo='" + ManifestNo + "' AND Part_No='" + partNo + "'";
                db.execSQL(strUpdate);
            }else if (Status.equals("Part Complete")){
                String strInsert = "INSERT INTO SATO_HISTORY(ID_TRANS,ManifestNo,QTY_SCAN,SERIAL,DATE_SCAN,NPK_ID,[Version],PartNo,DeviceID)VALUES" +
                        " (" + ID_TRANS + " ,'" + ManifestNo + "'," + qty_kanban  + ",'" + serial + "','" + getCurrentTime() + "','" + E_user.UserId + "'" +
                        ",'" + E_user.Version + "','" + partNo.trim() + "','" + E_user.DeviceID + "')";
                db.execSQL(strInsert);
                String sqLUpdate = "UPDATE [SATO_RECEIVING] SET Status_Manifest='Process', QTY_SCAN_AWAL='" + qty_kanban + "'," +
                        " Qty_Part=Qty_Part+" + qty_kanban+" , Date_Scan='" + getCurrentTime() + "',Status_Receiving='Complete' " +
                        "where ManifestNo='" + ManifestNo + "' AND Part_No='" + partNo + "'";
                db.execSQL(sqLUpdate);
            }else if (Status.equals("Manifest Complete")){
                db.execSQL("DELETE FROM SATO_RECEIVING where ManifestNo='" + ManifestNo + "'");
                db.execSQL("DELETE FROM SATO_HISTORY where ManifestNo='" + ManifestNo + "'");
            }
            return  "OK";
        }catch (Exception ex){
            return  "Error Insert History Perkanban Backup Online : " + ex.toString();
        }
    }
    public String CheckManifest(String role) {
        try {
            String Check = null;
            SQLiteDatabase db = helper.getWritableDatabase();
            Cursor cursor1;
            String Sql1 = "";
            if (role.equals("Operator Receiving")){
                 Sql1 = "SELECT * from SATO_RECEIVING";
            }else{
                 Sql1 = "SELECT * from SATO_QC";
            }
            cursor1 = db.rawQuery(Sql1, null);
            cursor1.moveToFirst();
            if (cursor1.getCount() != 0) {
                Check = "ADA DATA";
            } else {
                Check = "TIDAK ADA DATA";
            }
            return  Check;
        } catch (Exception e) {
            return "ER - " + e.toString();
        }
    }
    public Cursor GetManifestUpload(String role) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            Cursor cursor1;
            String Sql1="";
            db.beginTransaction();
            if (role.equals("Operator Receiving")){
                Sql1 = "SELECT ManifestNo,Part_No FROM SATO_RECEIVING";
            }else{
                Sql1 = "SELECT ManifestNo,Part_No FROM SATO_QC";
            }
            cursor1 = db.rawQuery(Sql1, null);
            db.setTransactionSuccessful();
            cursor1.moveToFirst();
            return cursor1;
        } catch (Exception e) {
            return null;
        }finally {
            db.endTransaction();
        }
    }

    public Cursor GetDataUploadReceiving(String ManifestNo,String PartNo){
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            Cursor cursor;
            String Sql1 = "";
            db.beginTransaction();
            Sql1 = "SELECT b.ManifestNo,b.DN_No,b.Suplier_Code,b.Suplier_Name,b.Qty_Manifest,a.PartNo,IFNULL(a.Qty_Scan,0) as Qty_Scan," +
                        "a.Serial," +
                        "a.NPK_ID,b.NPK_ID,a.Version,a.DeviceID,b.DeviceID,Manual_Complete FROM  SATO_RECEIVING b " +
                        " LEFT JOIN SATO_HISTORY a  ON B.ManifestNo=a.ManifestNo AND b.Part_No=a.PartNo " +
                        " where b.ManifestNo='" + ManifestNo + "' and b.Part_No='" +PartNo +"'";
            cursor = db.rawQuery(Sql1, null);
            db.setTransactionSuccessful();
            cursor.moveToFirst();
            return cursor;
        } catch (Exception e) {
            return null;
        }finally {
            db.endTransaction();
        }
    }
    public String DeleteUploadReceivingComplete(String manifestNo, String partNo) {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            String strInsert = "DELETE FROM SATO_HISTORY WHERE ManifestNo IN(SELECT ManifestNo FROM SATO_RECEIVING WHERE " +
                    " ManifestNo='" + manifestNo + "' and PartNo='" +partNo + "' AND Status_Manifest='Complete')";
            db.execSQL(strInsert);
            String sql = "DELETE FROM SATO_RECEIVING WHERE ManifestNo='" + manifestNo + "' and Part_No='" +partNo + "' AND Status_Manifest='Complete'";
            db.execSQL(sql);
            return "OK";
        }catch (Exception ex){
            return  "Error InsertManifestPerPartOnline : " + ex.toString();
        }
    }
    public String DeleteUploadQCComplete(String manifestNo, String partNo) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            String strInsert = "DELETE FROM SATO_HISTORY WHERE ManifestNo IN(SELECT ManifestNo FROM SATO_QC WHERE " +
                    " ManifestNo='" + manifestNo + "' and PartNo='" +partNo + "' AND Status_Manifest='Complete')";
            db.execSQL(strInsert);
            String sql = "DELETE FROM SATO_QC WHERE ManifestNo='" + manifestNo + "' and Part_No='" +partNo + "' " +
                    " AND Status_Manifest='Complete'";
            db.execSQL(sql);
            db.close();
            return "OK";
        }catch (Exception ex){
            db.close();
            return  "ER-Error InsertManifestPerPartOnline : " + ex.toString();
        }
    }
    public boolean InsertManifestPerPartDownloadR(String manifestNo, String dn_no, String suplier_code,
                                               String suplier_name, String status_receiving, int qty_manifest,
                                               int qty_part, int qty_scan_awal, String part_no, int qty_scan,
                                               String serial, String npk_id, String npk_rec, String version, String deviceID, String deviceRec)
    {
        try{
            boolean Data =false;
            Cursor cursor;
            SQLiteDatabase db ;
            db = helper.getWritableDatabase();
            //db.beginTransactionNonExclusive();
            String Sql = "SELECT Status_Receiving,ID_TRANS, Qty_Manifest, Qty_Part , Qty_Scan_Awal" +
                    " from SATO_RECEIVING WHERE ManifestNo='" + manifestNo + "' AND Part_No='" + part_no.trim() + "'";
            cursor = db.rawQuery(Sql,null);
            cursor.moveToFirst();
            if (cursor.getCount() == 0){
                db.close();
                db = helper.getWritableDatabase();
                String Sql1 = "INSERT INTO SATO_RECEIVING([ManifestNo],[DN_No],[Suplier_Code],[Suplier_Name]," +
                        "[Part_No],[Qty_Manifest],[NPK_ID],[Status_Receiving],Qty_Part,Status_Manifest,DeviceID,Qty_Scan_Awal)" +
                        "VALUES('"+ manifestNo  +"','"+ dn_no + "' ,'" +suplier_code + "'," +
                        "'" + suplier_name + "','" + part_no.trim() + "','" +qty_manifest + "','" + npk_rec + "'," +
                        "'" +status_receiving +"',0,'Not Yet Scanned','" + deviceRec + "','" +qty_scan_awal + "')";
                db.execSQL(Sql1);
                db.close();
            }
            cursor.close();
            if (qty_scan != 0) {
                Cursor cursorx;
                db = helper.getWritableDatabase();
                String Sqlx = "SELECT Status_Receiving,ID_TRANS, Qty_Manifest, Qty_Part , Qty_Scan_Awal" +
                        " from SATO_RECEIVING WHERE ManifestNo='" + manifestNo + "' AND Part_No='" + part_no.trim() + "'";
                cursorx = db.rawQuery(Sqlx, null);
                cursorx.moveToFirst();
                if (cursorx.getCount() != 0) {
                    String Status_Receiving = cursorx.getString(0);
                    int ID_TRANS = cursorx.getInt(1);
                    int Qty_Manifest = cursorx.getInt(2);
                    int Qty_Part = cursorx.getInt(3);
                    int Qty_Scan_Awal = cursorx.getInt(4);
                    db.close();

                    Cursor cursor1;
                    db = helper.getWritableDatabase();
                    String strSQL = "SELECT Serial from SATO_HISTORY WHERE Serial='" + serial + "' AND PartNo='" + part_no + "'" +
                            " AND ManifestNo='" + manifestNo + "'";
                    cursor1 = db.rawQuery(strSQL, null);
                    cursor1.moveToFirst();
                    if (cursor1.getCount() != 0) {
                        Data = true;
                        db.close();

                    } else {
                        db.close();
                        if (Qty_Scan_Awal == 0) {
                            db = helper.getWritableDatabase();
                            String strInsert = "INSERT INTO SATO_HISTORY(ID_TRANS,ManifestNo,QTY_SCAN,SERIAL,DATE_SCAN,NPK_ID,[Version],PartNo,DeviceID)VALUES" +
                                    " (" + ID_TRANS + " ,'" + manifestNo + "'," + qty_scan + ",'" + serial + "','" + getCurrentTime() + "','" + npk_id + "'" +
                                    ",'" + version + "','" + part_no.trim() + "','" + deviceID + "')";
                            db.execSQL(strInsert);
                            db.close();
                            db = helper.getWritableDatabase();
                            String strUpdate = "UPDATE [SATO_RECEIVING] SET QTY_SCAN_AWAL=" + qty_scan + ", Qty_Part=Qty_Part+" + qty_scan + " , " +
                                    "Date_Scan='" + getCurrentTime() + "',Status_Receiving='Partial Complete' where ManifestNo=" + manifestNo + " AND Part_No='" + part_no.trim() + "';";
                            db.execSQL(strUpdate);
                            db.close();

                            Cursor cursor2;
                            db = helper.getWritableDatabase();
                            String strSQL2 = "SELECT Qty_Part from " +
                                    "SATO_RECEIVING WHERE ManifestNo='" + manifestNo + "' AND Part_No='" + part_no.trim() + "';";
                            cursor2 = db.rawQuery(strSQL2, null);
                            cursor2.moveToFirst();
                            if (cursor2.getCount() != 0) {
                                int Qty_Scan = cursor2.getInt(0);
                                db.close();
                                if (Qty_Scan >= Qty_Manifest) {
                                    db = helper.getWritableDatabase();
                                    String strUpdate1 = "UPDATE [SATO_RECEIVING] SET Status_Receiving='Complete' where ManifestNo='" + manifestNo + "' AND Part_No='" + part_no.trim() + "';";
                                    db.execSQL(strUpdate1);
                                    db.close();
                                    Cursor cursor3;
                                    db = helper.getWritableDatabase();
                                    cursor3 = db.rawQuery("SELECT Status_Receiving from " +
                                            "SATO_RECEIVING WHERE ManifestNo='" + manifestNo + "' Order by Status_Receiving desc  Limit 1 ", null);

                                    cursor3.moveToFirst();
                                    if (cursor3.getCount() != 0) {
                                        String stsReceiving = cursor3.getString(0);
                                        if (stsReceiving.equals("Complete")) {
                                            Data = true;
                                            db.execSQL("UPDATE [SATO_RECEIVING] SET Status_Manifest='Complete',Date_Complete_Receiving='" + getCurrentTime() + "' " +
                                                    "where ManifestNo='" + manifestNo + "'");
                                        } else {
                                            Data = true;
                                            db.execSQL("UPDATE [SATO_RECEIVING] SET Status_Manifest='Process' where ManifestNo='" + manifestNo + "'");
                                        }
                                        db.close();
                                    }
                                    cursor3.close();
                                } else {
                                    Data = true;
                                    db = helper.getWritableDatabase();
                                    db.execSQL("UPDATE SATO_RECEIVING SET Status_Manifest='Process' where ManifestNo='" + manifestNo + "'");
                                    db.close();
                                }

                            } else {
                                Data = true;

                            }
                            cursor2.close();
                        } else {
                            // Jika Qty Awal Tidak Kosong / Sudah Ada
                            if (qty_scan == Qty_Scan_Awal) {
                                db = helper.getWritableDatabase();
                                String strInsert = "INSERT INTO SATO_HISTORY(ID_TRANS,ManifestNo,QTY_SCAN,SERIAL,DATE_SCAN,NPK_ID,[Version],PartNo,DeviceID)VALUES" +
                                        " (" + ID_TRANS + " ,'" + manifestNo + "'," + qty_scan + ",'" + serial + "','" + getCurrentTime() + "','" + npk_id + "'" +
                                        ",'" + version + "','" + part_no + "','" + deviceID + "')";
                                db.execSQL(strInsert);
                                db.close();
                                db = helper.getWritableDatabase();
                                String strUpdate = "UPDATE [SATO_RECEIVING] SET Qty_Part=Qty_Part+ " + qty_scan + " , " +
                                        "Date_Scan='" + getCurrentTime() + "',Status_Receiving='Partial Complete' where ManifestNo='" + manifestNo + "' AND Part_No='" + part_no.trim() + "'";
                                db.execSQL(strUpdate);
                                db.close();
                                Cursor cursor2;
                                db = helper.getWritableDatabase();
                                String strSQL2 = "SELECT Qty_Part from " +
                                        "SATO_RECEIVING WHERE ManifestNo='" + manifestNo + "' AND Part_No='" + part_no.trim() + "'";
                                cursor2 = db.rawQuery(strSQL2, null);
                                cursor2.moveToFirst();
                                if (cursor2.getCount() != 0) {
                                    int Qty_Scan = cursor2.getInt(0);
                                    db.close();
                                    if (Qty_Scan >= Qty_Manifest) {
                                        db = helper.getWritableDatabase();
                                        String strUpdate1 = "UPDATE [SATO_RECEIVING] SET Status_Receiving='Complete' where ManifestNo='" + manifestNo + "' AND Part_No='" + part_no.trim() + "'";
                                        db.execSQL(strUpdate1);
                                        db.close();
                                        Cursor cursor3;
                                        db = helper.getWritableDatabase();
                                        cursor3 = db.rawQuery("SELECT Status_Receiving from " +
                                                "SATO_RECEIVING WHERE ManifestNo='" + manifestNo + "'  Order by Status_Receiving desc Limit 1", null);
                                        cursor3.moveToFirst();
                                        if (cursor3.getCount() != 0) {
                                            String stsReceiving = cursor3.getString(0);
                                            db.close();
                                            if (stsReceiving.equals("Complete")) {
                                                Data = true;
                                                db = helper.getWritableDatabase();
                                                db.execSQL("UPDATE [SATO_RECEIVING] SET Status_Manifest='Complete',Date_Complete_Receiving='" + getCurrentTime() + "' " +
                                                        "where ManifestNo='" + manifestNo + "'");
                                                db.close();
                                            } else {
                                                Data = true;
                                                db = helper.getWritableDatabase();
                                                db.execSQL("UPDATE [SATO_RECEIVING] SET Status_Manifest='Process' where ManifestNo='" + manifestNo + "'");
                                                db.close();
                                            }
                                        }
                                        cursor3.close();
                                    } else {
                                        Data = true;
                                        db = helper.getWritableDatabase();
                                        db.execSQL("UPDATE SATO_RECEIVING SET Status_Manifest='Process' where ManifestNo='" + manifestNo + "'");
                                        db.close();
                                    }

                                } else {
                                    Data = true;
                                    db.close();
                                }

                                cursor2.close();
                            } else {
                                Data = true;

                            }
                        }

                    }
                    cursor1.close();
                }
                cursorx.close();

            }
          /*  db.setTransactionSuccessful();
            db.endTransaction();
          //  db.close();*/
            return  Data;
        }catch (Exception ex){
            return  false;
        }
    }

    public String
    InsertManifestPerPartDownloadQCTIDAKADA
            (String manifestNo, String dn_no, String suplier_code, String suplier_name,
          int qty_part,int Qty_Scan_Awal, String PartNo,int ID_Trans,int Qty_Scan,int Qty_Scan_QC,String Serial,
          String Serial_QC,String Date_Scan,String Date_Scan_QC,String NPK_ID,String NPK_ID_QC,String Status_Part,
          String Remark,String Status_Process,String DeviceID,String Status_Scan,int QTY_SAMPLING,String Status_QC){
        try{
            String Data ="";
            Cursor cursor;
            SQLiteDatabase db ;
            db = helper.getWritableDatabase();
            String Sql = "SELECT Status_Manifest from SATO_QC WHERE ManifestNo='" + manifestNo + "' and Part_No='" + PartNo + "' Limit 1";
            cursor = db.rawQuery(Sql,null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0){
                String stsReceiving = cursor.getString(0);
                db.close();

                if (stsReceiving.equals("Complete")) {
                    Data = "ER - Manifest Already Complete";
                }else{
                    if (Status_QC.equals("Complete")){
                        db = helper.getWritableDatabase();
                        db.execSQL("UPDATE SATO_QC SET Status_QC='Complete' WHERE ManifestNo='" + manifestNo + "' AND Part_No='" + PartNo + "';");
                        db.close();
                    }
                    Data = stsReceiving;
                    Cursor cursor1;
                    db = helper.getWritableDatabase();
                    String Sqlhistory = "SELECT Serial,Serial_QC from SATO_HISTORY WHERE ManifestNo='" + manifestNo + "' " +
                            "AND PartNo='" + PartNo+"' and Serial='" + Serial + "';";
                    cursor1 = db.rawQuery(Sqlhistory,null);
                    cursor1.moveToFirst();
                    if (cursor1.getCount() == 0) {
                        db.close();
                        String strSQL2 = "INSERT INTO [SATO_HISTORY] ([ID_Trans]" +
                                ",[ManifestNo]" +
                                ",[PartNo]" +
                                ",[Qty_Scan]" +
                                ",[Qty_Scan_QC]" +
                                ",[Serial]" +
                                ",[Serial_QC]" +
                                ",[Date_Scan]" +
                                ",[Date_Scan_QC]" +
                                ",[NPK_ID]" +
                                ",[NPK_ID_QC]" +
                                ",[Status_Part]" +
                                ",[Remark]" +
                                ",[Status_Process]" +
                                ",[DeviceID]" +
                                ",[Status_Scan])" +
                                "VALUES" +
                                "('" + ID_Trans + "','" + manifestNo + "','" + PartNo + "','" + Qty_Scan +"','" + Qty_Scan_QC + "','" + Serial + "','" + Serial_QC + "'," +
                                "'" + Date_Scan +"','" + Date_Scan_QC + "','" + NPK_ID + "','" + NPK_ID_QC + "','" + Status_Part + "','" + Remark + "','" + Status_Process +"'," +
                                "'" + DeviceID +"','" +Status_Scan+"')";
                        db = helper.getWritableDatabase();
                        db.execSQL(strSQL2);
                        db.close();
                    }else{
                        String SERIAL = cursor1.getString(0);
                        String SERIAL_QC = cursor1.getString(1);
                        cursor1.close();
                        if (!SERIAL.equals(SERIAL_QC)) {
                            db = helper.getWritableDatabase();
                            db.execSQL("UPDATE SATO_HISTORY SET SERIAL_QC='" + Serial_QC + "',Qty_Scan_QC='" + Qty_Scan_QC + "'," +
                                    " Date_Scan_QC='" + Date_Scan_QC + "',NPK_ID_QC='" + NPK_ID_QC + "',Status_Part='" + Status_Part + "'," +
                                    "Remark='" + Remark + "' WHERE Serial='" + Serial + "' " +
                                    "AND ManifestNo='" + manifestNo + "' AND PartNo='" + PartNo + "'");
                            db.close();

                        }
                    }
                }
            }else{
                //Insert

                String strSQL = "INSERT INTO SATO_QC([ManifestNo],[DN_No],[Suplier_Code],[Suplier_Name],[Part_No],[Qty_Manifest],[NPK_ID],Status_QC,Qty_Part,Qty_Sampling" +
                        ",Qty_Scan_Kanban,Qty_Scan_Normal,Status_Manifest,DeviceID,Qty_Scan_Awal)" +
                        " VALUES('" + manifestNo +"','" + dn_no +"','"+suplier_code+"','" + suplier_name + "','" + PartNo.trim() + "','" +qty_part + "','" + E_user.UserId + "'," +
                        "'Not Yet Scanned',0," + QTY_SAMPLING + ",0,0,'Not Yet Scanned','" + E_user.DeviceID + "','" + Qty_Scan_Awal + "')";
                db = helper.getWritableDatabase();
                db.execSQL(strSQL);
                db.close();
                Cursor cursor1;
                String Sqlhistory = "SELECT Serial,Serial_QC from SATO_HISTORY WHERE ManifestNo='" + manifestNo + "' AND PartNo='" + PartNo+"' and Serial='" + Serial + "';";
                db = helper.getWritableDatabase();
                cursor1 = db.rawQuery(Sqlhistory,null);
                cursor1.moveToFirst();
                if (cursor1.getCount() == 0) {
                    String strSQL2 = "INSERT INTO [SATO_HISTORY]" +
                        "([ID_Trans]" +
                        ",[ManifestNo]" +
                        ",[PartNo]" +
                        ",[Qty_Scan]" +
                        ",[Qty_Scan_QC]" +
                        ",[Serial]" +
                        ",[Serial_QC]" +
                        ",[Date_Scan]" +
                        ",[Date_Scan_QC]" +
                        ",[NPK_ID]" +
                        ",[NPK_ID_QC]" +
                        ",[Status_Part]" +
                        ",[Remark]" +
                        ",[Status_Process]" +
                        ",[DeviceID]" +
                        ",[Status_Scan])" +
                        "VALUES" +
                        "('" + ID_Trans + "','" + manifestNo + "','" + PartNo + "','" + Qty_Scan +"','" + Qty_Scan_QC + "','" + Serial + "','" + Serial_QC + "'," +
                        "'" + Date_Scan +"','" + Date_Scan_QC + "','" + NPK_ID + "','" + NPK_ID_QC + "','" + Status_Part + "','" + Remark + "','" + Status_Process +"'," +
                        "'" + DeviceID +"','" +Status_Scan+"')";
                        db.execSQL(strSQL2);
                }else{
                    String SERIAL = cursor1.getString(0);
                    String SERIAL_QC = cursor1.getString(1);
                    db.close();
                    if (!SERIAL.equals(SERIAL_QC)) {
                        db = helper.getWritableDatabase();
                        db.execSQL("UPDATE SATO_HISTORY SET SERIAL_QC='" + Serial_QC + "',Qty_Scan_QC='" + Qty_Scan_QC + "'," +
                                " Date_Scan_QC='" + Date_Scan_QC + "',NPK_ID_QC='" + NPK_ID_QC + "',Status_Part='" + Status_Part + "'," +
                                "Remark='" + Remark + "' WHERE Serial='" + Serial + "' AND ManifestNo='" + manifestNo + "' AND PartNo='" + PartNo + "'");
                        db.close();
                    }
                }
// TINGGAL NERUSIN INI
                Data = "Not Yet Scanned";
            }
            return  Data;
        }catch (Exception ex){
            return  "ER - " + ex.toString();
        }
    }

    public String GetDataUploadRQC(String manifestNo, String partNo) {
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            Cursor cursor1;
            String Sql1 = "";

            db.beginTransaction();
            Sql1 ="SELECT qc.ManifestNo," +
                    " Part_No," +
                    "Status_Quarantine," +
                    "IFNULL(qc.Date_Quarantine,'1900-01-01')AS Date_Quarantine ," +
                    " his.Qty_Scan," +
                    "his.Qty_Scan_QC," +
                    "his.Serial," +
                    " IFNULL(his.Serial_QC,'')Serial_QC," +
                    "his.Date_Scan, " +
                    "IFNULL(his.Date_Scan_QC,'1900-01-01')AS Date_Scan_QC," +
                    "his.NPK_ID," +
                    " IFNULL(his.NPK_ID_QC,'')NPK_ID_QC ," +
                    "IFNULL(his.Status_Part,'')Status_Part," +
                    " IFNULL(his.Remark,'')Remark," +
                    "IFNULL(his.Status_Process,'')Status_Process," +
                    " IFNULL(his.DeviceID,'')DeviceID," +
                    "IFNULL(his.Status_Scan,'')Status_Scan," +
                    "Qty_Sampling " +
                    "FROM SATO_HISTORY his" +
                    " INNER JOIN SATO_QC qc  ON his.ManifestNo=QC.ManifestNo and his.PartNo=qc.Part_No" +
                    " WHERE his.ManifestNo='"+manifestNo+"' and his.PartNo='"+partNo+"' AND his.Qty_Scan_QC>0 ORDER BY his.Serial";
            cursor1 = db.rawQuery(Sql1, null);
            cursor1.moveToFirst();
            if (cursor1.getCount() != 0) {
                for (int ccx = 0; ccx < cursor1.getCount(); ccx++) {
                    cursor1.moveToPosition(ccx);
                    String manifestno = cursor1.getString(0);
                    String Part_No = cursor1.getString(1).trim();
                    String Status_Quarantine = cursor1.getString(2).trim();
                    String Date_Quarantine = cursor1.getString(3).trim();
                    int Qty_Scan = cursor1.getInt(4);
                    int Qty_Scan_QC = cursor1.getInt(5);
                    String Serial = cursor1.getString(6).trim();
                    String Serial_QC = cursor1.getString(7).trim();
                    String Date_Scan = cursor1.getString(8);
                    String Date_Scan_QC = cursor1.getString(9);
                    String NPK_ID = cursor1.getString(10).trim();
                    String NPK_ID_QC = cursor1.getString(11).trim();
                    String Status_Part = cursor1.getString(12).trim();
                    String Remark = cursor1.getString(13).trim();
                    String Status_Process = cursor1.getString(14).trim();
                    String DeviceID = cursor1.getString(15).trim();
                    String Status_Scan = cursor1.getString(16).trim();
                    int Qty_Sampling = cursor1.getInt(17);
                    double result = 0;
                    if (Qty_Scan_QC > Qty_Sampling) {
                        result = 1;
                    } else if (Qty_Scan_QC < Qty_Sampling) {
                        result = (double) Qty_Sampling / (double) Qty_Scan_QC;
                        result = roundAvoid(result, 0);
                    }
                    DataaccesManifest dataaccesManifest = new DataaccesManifest();
                   dataaccesManifest.UploadDataQC(manifestno,
                            Part_No, Qty_Scan, Qty_Scan_QC, Serial, Serial_QC, Date_Scan, Date_Scan_QC, NPK_ID
                            , NPK_ID_QC, Status_Part, Remark, Status_Process, DeviceID, Status_Scan, Status_Quarantine, Date_Quarantine, (int) result);
                }
                db.setTransactionSuccessful();
                return "OK";
            }else{
                return "Tidak Ada Data";
            }
        } catch (Exception e) {
            return "ER - " + e.toString();
        }finally {
            db.endTransaction();
        }
    }
    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
    public Cursor GetDataUploadRQCManifest(String manifestNo, String partNo) {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            Cursor cursor;
            String Sql1 = "";
            Sql1 = "SELECT ManifestNo, DN_No, Suplier_Code, Suplier_Name, Qty_Scan_Awal, Part_No,Status_Quarantine," +
                    "IFNULL(Date_Quarantine,'1900-01-01')AS Date_Quarantine" +
                    " ,Qty_Manifest,Qty_Sampling,DeviceID,NPK_ID FROM SATO_QC  WHERE ManifestNo='" + manifestNo + "'";
            cursor = db.rawQuery(Sql1, null);
            cursor.moveToFirst();
            return cursor;
        } catch (Exception e) {
            return null;
        }
    }
    public boolean DeleteLokal(String manifestNo, String partNo, String role) {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            if (role.equals("Operator Receiving")){
                String strInsert = "DELETE FROM SATO_HISTORY WHERE ManifestNo IN(SELECT ManifestNo FROM SATO_RECEIVING WHERE " +
                        " ManifestNo='" + manifestNo + "' and PartNo='" +partNo + "')";
                db.execSQL(strInsert);
                String sql = "DELETE FROM SATO_RECEIVING WHERE ManifestNo='" + manifestNo + "' and Part_No='" +partNo + "' ";
                db.execSQL(sql);

            }else{
                String strInsertx = "DELETE FROM SATO_HISTORY WHERE ManifestNo IN(SELECT ManifestNo FROM SATO_QC WHERE " +
                        " ManifestNo='" + manifestNo + "' and PartNo='" +partNo + "' )";
                db.execSQL(strInsertx);
                String sql = "DELETE FROM SATO_QC WHERE ManifestNo='" + manifestNo + "' and Part_No='" +partNo + "'";
                db.execSQL(sql);
            }
            return  true;
        } catch (Exception e) {
            return false;
        }
    }
    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }
}
