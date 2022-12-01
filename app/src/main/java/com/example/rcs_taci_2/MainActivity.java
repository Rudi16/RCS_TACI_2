package com.example.rcs_taci_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.rcs_taci_2.Dataacces.DataaccesManifest;
import com.example.rcs_taci_2.Dataacces.DataaccesOffline;
import com.example.rcs_taci_2.Entity.E_user;
import com.example.rcs_taci_2.Helper.ConnectionHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {
Button btnMenu,btnLogout,BtnUpload,BtnDownload,BtnSetting;
    private Vibrator _vibrator;
    TextView tv ;
    DataaccesOffline dataaccesOffline;
    DataaccesManifest dataaccesManifest;
    Toolbar toolbar;
    LoadingDialog loadingDialog;
    private boolean YesNo;
    private Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(color);
        setSupportActionBar(toolbar);
        if (E_user.getRole().equals("Operator Receiving"))
            toolbar.setTitle("RECEIVING");
        else if(E_user.getRole().equals("Operator QC"))
            toolbar.setTitle("QC CHECK");

        btnMenu = findViewById(R.id.btnmenu);
        btnLogout = findViewById(R.id.btnlogout);
        BtnUpload = findViewById(R.id.btnupload);
        BtnDownload = findViewById(R.id.btndownload);
        BtnSetting = findViewById(R.id.btnsetting);
        _vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        loadingDialog = new LoadingDialog(MainActivity.this);
        tv = findViewById(R.id.msgbox);
        dataaccesOffline = new DataaccesOffline(this);
        dataaccesOffline.CreateTable();
        dataaccesManifest = new DataaccesManifest();
        if (E_user.getRole().equals("Operator Receiving"))
            btnMenu.setText("RECEIVING");
        else if(E_user.getRole().equals("Operator QC"))
            btnMenu.setText("QC CHECK");
        else
            btnMenu.setText("NOTHING");
        if (ConnectionHelper.connection.equals("Offline")){
            BtnDownload.setVisibility(View.VISIBLE);
            BtnUpload.setVisibility(View.VISIBLE);
        }else{
            BtnDownload.setVisibility(View.GONE);
            BtnUpload.setVisibility(View.GONE);
        }
        btnMenu.setOnClickListener(v -> {
            if (btnMenu.getText().equals("RECEIVING")) {
                Intent i = new Intent(this, receiving_manifest.class);
                startActivityForResult(i, 1);
            }else if(btnMenu.getText().equals("QC CHECK")){
                Intent i = new Intent(this, receiving_manifest.class);
                startActivityForResult(i, 1);
            }
        });
        BtnSetting.setOnClickListener(view -> {
            Intent i = new Intent(this, settings.class);
            startActivityForResult(i, 1);
        });
        BtnUpload.setOnClickListener(view -> {
            if (TestConnection().equals("OK")) {
            YesNo = false;
            String CheckTransaksi = dataaccesOffline.CheckManifest(E_user.getRole());
            if (CheckTransaksi.equals("ADA DATA")){
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialogyesno);
                dialog.setCancelable(false);
                dialog.show();
                Button BtnYes, BtnNo;
                TextView textloading = (TextView)dialog.findViewById(R.id.loadingtextview);
                textloading.setText("Are you want to Upload data?");
                BtnYes = dialog.findViewById(R.id.btn_yes);
                BtnNo = dialog.findViewById(R.id.btn_no);
                BtnYes.setOnClickListener(view12 -> {
                    startUpload();
                    dialog.dismiss();
                });
                BtnNo.setOnClickListener(view1 -> {
                    dialog.dismiss();
                });
            } else {
                startTone("NG");
                showmessage("Data Not Found", "NG");
            }
            }else{
                startTone("NG");
                showmessage("Can't Connect to Server!", "NG");
            }
        });
        BtnDownload.setOnClickListener(view -> {
            if (TestConnection().equals("OK")) {
                String Status = "";
                if (E_user.getRole().equals("Operator Receiving")){
                    Status = "";
                }else{
                    Status = "QC TIDAK ADA";
                }
                String CheckTransaksi = dataaccesManifest.CheckManifestDownload(E_user.getRole(),Status);
                if (CheckTransaksi.equals("ADA DATA")) {
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.dialogyesno);
                    dialog.setTitle("Are you want to download data?");
                    dialog.setCancelable(false);
                    dialog.show();
                    Button BtnYes, BtnNo;
                    TextView textloading = (TextView)dialog.findViewById(R.id.loadingtextview);
                    textloading.setText("Are you want to Download data?");
                    BtnYes = dialog.findViewById(R.id.btn_yes);
                    BtnNo = dialog.findViewById(R.id.btn_no);
                    BtnYes.setOnClickListener(view12 -> {
                            startDownload();
                        dialog.dismiss();

                    });
                    BtnNo.setOnClickListener(view1 -> {
                       dialog.dismiss();
                   });
                } else {
                    startTone("NG");
                    showmessage("Data Not Found", "NG");
                }
            }else{
                startTone("NG");
                showmessage("Can't Connect to Server!", "NG");
            }

        });
        btnLogout.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            // Set a title for alert dialog
            // Ask the final question
            builder.setMessage("Do you want to logout?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent loginActivity = new Intent(getApplicationContext(),Login.class);
                    startActivity(loginActivity);
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });

            AlertDialog dialog = builder.create();
            // Display the alert dialog on interface
            dialog.show();
        });
    }
    private void showmessage(String isimsg, @NonNull String ng){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (ng.equals("NG")) {
                    tv.setTextColor(Color.WHITE);
                    tv.setBackgroundColor(Color.rgb(255, 0, 0));
                }else{
                    tv.setTextColor(Color.BLACK);
                    tv.setBackgroundColor(Color.rgb(0, 255, 0));
                }
                tv.setText(isimsg);
                tv.setVisibility(View.VISIBLE);
                tv.postDelayed(() -> {
                    tv.setVisibility(View.INVISIBLE);
                }, 3000);
            }
        });

    }
    private void startTone(String ng) {
        if (ng.equals("NG")) {
            _vibrator.vibrate(500);
            play(this,R.raw.salah);

        } else if (ng.equals("OK")) {
            play(this,R.raw.beepok);
        }

    }
    private MediaPlayer mMediaPlayer;

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void play(Context c, int rid) {
        stop();

        mMediaPlayer = MediaPlayer.create(c, rid);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });

        mMediaPlayer.start();
    }
    @Override
    public void onResume(){
        super.onResume();
        setSupportActionBar(this.toolbar);
        if (ConnectionHelper.connection.equals("Offline")){
            BtnDownload.setVisibility(View.VISIBLE);
            BtnUpload.setVisibility(View.VISIBLE);
        }else{
            BtnDownload.setVisibility(View.GONE);
            BtnUpload.setVisibility(View.GONE);
        }

    }

    public String TestConnection(){
        try {
            String check = "TIDAK OK";
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Connection conn = null;
            String ConnURL = null;
            ResultSet Rs ;
            String server = null;
            String Instance = null ;

            String[] checkInstance = ConnectionHelper.server.trim().split("\\\\");
            if(checkInstance.length > 1){
                server = checkInstance[0];
                Instance = checkInstance[1];

                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                ConnURL = "jdbc:jtds:sqlserver://" + server + ";"
                        + "databaseName=DB_RCS_TACI;user=" + ConnectionHelper.user + ";password=" + ConnectionHelper.passwd + ";instance=" + Instance + ";";
            }else{
                server = ConnectionHelper.server.trim();
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                ConnURL = "jdbc:jtds:sqlserver://" + server + ";"
                        + "databaseName=DB_RCS_TACI;user=" + ConnectionHelper.user+ ";password=" + ConnectionHelper.passwd + ";";
            }

            conn = DriverManager.getConnection(ConnURL);
            String query = "SELECT * FROM sys.databases WHERE name = 'DB_RCS_TACI';";
            PreparedStatement ps = conn.prepareStatement(query);
            Log.e("query", query);
            Rs = ps.executeQuery();
            if (Rs == null) {
                check =  "TIDAK OK";
            } else if (Rs.next()) {
                check =  "OK";
            }
            return  check;
        } catch (SQLException se) {
            return  "TIDAK OK";

        } catch (ClassNotFoundException e) {
            return  "TIDAK OK";

        } catch (Exception e) {
            return  "TIDAK OK";

        }

    }
    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
    @Override
    public void onBackPressed() {
        return;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        if (ConnectionHelper.connection.equals("Offline")){
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this,R.drawable.offline));
        }else{
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this,R.drawable.online));
        }

        // set your desired icon here based on a flag if you like
        return true;
    }



    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private Button startBtn;
    private ProgressDialog mProgressDialog;

    private void startDownload() {
        String url = "";
        E_user.textLoading = "Downloading Data....";
        new DownloadFileAsync().execute(url);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
            default:
                return null;
        }
    }
    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.StartLoadingDialog();
        }

        @Override
        protected String doInBackground(String... aurl) {
            try {
                if (ConnectionHelper.connection.equals("Offline")) {
                    ResultSet Rs;
                    if (E_user.getRole().equals("Operator Receiving")) {
                        boolean check = false;
                        Rs = dataaccesManifest.GetDataDownload(E_user.getRole());
                        if (Rs != null) {
                            try {
                                int row = 0;
                                while (Rs.next()) {
                                    row++;
                                    String ManifestNo = Rs.getString("ManifestNo");
                                    String DN_No = Rs.getString("DN_No");
                                    String Suplier_Code = Rs.getString("Suplier_Code");
                                    String Suplier_Name = Rs.getString("Suplier_Name");
                                    String Status_Receiving = Rs.getString("Status_Receiving");
                                    int Qty_Manifest = Rs.getInt("Qty_Manifest");
                                    int Qty_Part = Rs.getInt("Qty_Part");
                                    int Qty_Scan_Awal = Rs.getInt("Qty_Scan_Awal");
                                    String Part_No = Rs.getString("Part_No").trim();
                                    int Qty_Scan = Rs.getInt("Qty_Scan");
                                    String Serial = Rs.getString("Serial");
                                    String NPK_ID = Rs.getString("NPK_ID");
                                    String NPK_Rec = Rs.getString("NPK_Rec");
                                    String Version = Rs.getString("Version");
                                    String DeviceID = Rs.getString("DeviceID");
                                    String DeviceRec = Rs.getString("DeviceRec");
                                    //Insert Data ke Database Lokal
                                    check = dataaccesOffline.InsertManifestPerPartDownloadR(ManifestNo, DN_No, Suplier_Code, Suplier_Name,
                                            Status_Receiving, Qty_Manifest, Qty_Part, Qty_Scan_Awal, Part_No.trim(), Qty_Scan,
                                            Serial, NPK_ID, NPK_Rec, Version, DeviceID, DeviceRec);
                                }
                                if (check) {
                                    startTone("OK");
                                    showmessage("Download Receiving Success", "OK");
                                }else{
                                    startTone("NG");
                                    showmessage("Download Receiving Failed", "NG");
                                }

                            } catch (SQLException throwable) {
                                throwable.printStackTrace();
                                loadingDialog.dismissDialog();
                            }
                        } else {
                            startTone("NG");
                            showmessage("Data Not Found", "NG");
                            loadingDialog.dismissDialog();
                        }
                    } else {
                        //QC
                        Rs = dataaccesManifest.GetDataDownload(E_user.getRole());
                        if (Rs != null) {
                            try {

                                String check = "ER";
                                //row = R
                                while (Rs.next()) {
                                    String ManifestNo = Rs.getString("ManifestNo");
                                    String DN_No = Rs.getString("DN_No");
                                    String Suplier_Code = Rs.getString("Suplier_Code");
                                    String Suplier_Name = Rs.getString("Suplier_Name");
                                    int Qty_Part = Rs.getInt("Qty_Part");
                                    int Qty_Scan_Awal = Rs.getInt("Qty_Scan_Awal");
                                    String Part_No = Rs.getString("Part_No").trim();
                                    int ID_Trans = Rs.getInt("ID_Trans");
                                    int Qty_Scan = Rs.getInt("Qty_Scan");
                                    int Qty_Scan_QC = Rs.getInt("Qty_Scan_QC");
                                    String Serial = Rs.getString("Serial").trim();
                                    String Serial_QC = Rs.getString("Serial_QC").trim();
                                    String Date_Scan = Rs.getString("Date_Scan");
                                    String Date_Scan_QC = Rs.getString("Date_Scan_QC");
                                    String NPK_ID = Rs.getString("NPK_ID").trim();
                                    String NPK_ID_QC = Rs.getString("NPK_ID_QC").trim();
                                    String Status_Part = Rs.getString("Status_Part").trim();
                                    String Remark = Rs.getString("Remark").trim();
                                    String Status_Process = Rs.getString("Status_Process").trim();
                                    String DeviceID = Rs.getString("DeviceID").trim();
                                    String Status_Scan = Rs.getString("Status_Scan").trim();
                                    int Qty_Sampling = Rs.getInt("Qty_Sampling");
                                    String Status_QC = Rs.getString("Status_QC").trim();
                                    //Insert Data ke Database Lokal
                                    check = dataaccesOffline.InsertManifestPerPartDownloadQCTIDAKADA(ManifestNo, DN_No, Suplier_Code, Suplier_Name,
                                            Qty_Part, Qty_Scan_Awal, Part_No.trim(),ID_Trans,Qty_Scan,Qty_Scan_QC,Serial,Serial_QC,Date_Scan,Date_Scan_QC,NPK_ID
                                            ,NPK_ID_QC,Status_Part,Remark,Status_Process,DeviceID,Status_Scan,Qty_Sampling,Status_QC);
                                }
                                if (check.substring(0,2).equals("ER")) {
                                    startTone("NG");
                                    showmessage(check, "NG");
                                }else{
                                    startTone("OK");
                                    showmessage("Download Data QC Success", "OK");
                                }
                            } catch (SQLException throwable) {
                                throwable.printStackTrace();
                                loadingDialog.dismissDialog();
                            }
                        } else {
                            startTone("NG");
                            showmessage("Data Not Found", "NG");
                            loadingDialog.dismissDialog();

                        }
                    }
                } else {
                    startTone("NG");
                    showmessage("Status online, please change to connection Offline", "NG");
                    loadingDialog.dismissDialog();
                }
            } catch (Exception e) {
                showmessage(e.toString(), "NG");
            }
            return null;
        }
        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }
        @Override
        protected void onPostExecute(String unused) {
            loadingDialog.dismissDialog();
        }
    }

    //upload
    private void startUpload() {
        String url = "";
        E_user.textLoading = "Uploading Data....";
        new UploadFileAsync().execute(url);
    }
    class UploadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.StartLoadingDialog();
        }

        @Override
        protected String doInBackground(String... aurl) {
            try {
                if (ConnectionHelper.connection.equals("Offline")) {
                    Cursor cursor;
                    cursor = dataaccesOffline.GetManifestUpload(E_user.getRole());
                    boolean infosuccess = false;
                    for (int cc=0; cc < cursor.getCount(); cc++){
                        cursor.moveToPosition(cc);
                        String ManifestNo = cursor.getString(0);
                        String PartNo = cursor.getString(1);
                        String CheckServer = dataaccesManifest.CheckManifestComplete(ManifestNo,PartNo,E_user.getRole());
                        if (CheckServer.equals("Complete")){
                            boolean DeleteLokal = dataaccesOffline.DeleteLokal(ManifestNo,PartNo,E_user.getRole());
                            infosuccess = true;
                        }else {
                            if (E_user.getRole().equals("Operator Receiving")) {
                                boolean check = false;
                                Cursor cursor1 = dataaccesOffline.GetDataUploadReceiving(ManifestNo, PartNo);
                                if (cursor1.getCount() != 0) {
                                    for (int ccx = 0; ccx < cursor1.getCount(); ccx++) {
                                        cursor1.moveToPosition(ccx);
                                        String DN_No = cursor1.getString(1);
                                        String supplierCode = cursor1.getString(2);
                                        String supplierName = cursor1.getString(3);
                                        int Qty_Manifest = cursor1.getInt(4);
                                        int Qty_Part = cursor1.getInt(6);
                                        String Serial = cursor1.getString(7);
                                        String NPK_ID = cursor1.getString(8);
                                        String NPK_RCV = cursor1.getString(9);
                                        String VERSION = cursor1.getString(10);
                                        String DeviceID = cursor1.getString(11);
                                        String DeviceRCV = cursor1.getString(12);
                                        String Manual_Complete = cursor1.getString(13);
                                        check = dataaccesManifest.UploadReceiving(ManifestNo, DN_No,
                                                supplierCode, supplierName, Qty_Manifest, PartNo, Qty_Part, Serial, NPK_ID, NPK_RCV, VERSION, DeviceID, DeviceRCV, Manual_Complete);
                                    }
                                }
                                if (check) {
                                    String DeleteReceivingUpload = dataaccesOffline.DeleteUploadReceivingComplete(ManifestNo, PartNo);
                                    if (!DeleteReceivingUpload.equals("OK")) {
                                        infosuccess = false;
                                        showmessage("Error Delete Data Receiving Complete", "NG");
                                        loadingDialog.dismissDialog();
                                    } else {
                                        infosuccess = true;
                                        showmessage("Upload Receiving Success", "OK");
                                        loadingDialog.dismissDialog();
                                    }
                                } else {
                                    infosuccess = false;
                                    showmessage("Error Upload Data Receiving", "NG");
                                    loadingDialog.dismissDialog();
                                }
                            } else {
                                //QC
                                String check = "  ";
                                check = dataaccesOffline.GetDataUploadRQC(ManifestNo, PartNo);
                                /*if (cursor1.getCount() != 0) {
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
                                        check = dataaccesManifest.UploadDataQC(manifestno,
                                                Part_No, Qty_Scan, Qty_Scan_QC, Serial, Serial_QC, Date_Scan, Date_Scan_QC, NPK_ID
                                                , NPK_ID_QC, Status_Part, Remark, Status_Process, DeviceID, Status_Scan, Status_Quarantine, Date_Quarantine, (int) result);
                                    }
                                }*/
                                if (!check.substring(0, 2).equals("ER")) {
                                    String DeleteReceivingUpload = dataaccesOffline.DeleteUploadQCComplete(ManifestNo, PartNo);
                                    if (!DeleteReceivingUpload.equals("OK")) {
                                        infosuccess = false;
                                    } else {
                                        infosuccess = true;
                                    }
                                } else {
                                    infosuccess = false;
                                }
                            }
                        }
                    }

                    if (E_user.getRole().equals("Operator Receiving")) {
                        if (infosuccess) {
                            showmessage("Upload Receiving Success", "OK");
                            loadingDialog.dismissDialog();
                        } else {
                            startTone("NG");
                            showmessage("Error Upload Data Receiving, Check Monitoring Receiving", "NG");
                        }
                        loadingDialog.dismissDialog();
                    }else{
                        if (infosuccess) {
                            startTone("OK");
                            showmessage("Upload QC Success", "OK");
                            loadingDialog.dismissDialog();
                        } else {
                            startTone("NG");
                            showmessage("Error Upload Data QC, Check Monitoring QC", "NG");
                        }
                        loadingDialog.dismissDialog();
                    }
                }else{
                    startTone("NG");
                    showmessage("Status online, please change to connection Offline","NG");
                    loadingDialog.dismissDialog();
                }
            } catch (Exception e) {
                showmessage(e.toString(), "NG");
                loadingDialog.dismissDialog();
            }
            return null;
        }
        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            loadingDialog.dismissDialog();
        }
    }

    //Upload
}