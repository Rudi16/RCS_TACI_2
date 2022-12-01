package com.example.rcs_taci_2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.StrictMode;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rcs_taci_2.Dataacces.DataaccesManifest;
import com.example.rcs_taci_2.Dataacces.DataaccesOffline;
import com.example.rcs_taci_2.Dataacces.DataaccesUser;
import com.example.rcs_taci_2.Dataacces.DataaccesUserOffline;
import com.example.rcs_taci_2.Entity.E_user;
import com.example.rcs_taci_2.Helper.ConnectionHelper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
public class settings extends AppCompatActivity {

    EditText edtserver,edtusersql,edtpasswordsql,edtBHTID;
    private Vibrator _vibrator;
    Button btnset,btnTestConnection;
    private AlertDialog.Builder ab;
    DataaccesUser dataaccesuserWifi ;
    DataaccesUserOffline dataaccesUserOffline;
    private static final String FILE_NAME = "Config.txt";
    private RadioButton RbWifi,RbBatch;
    ImageView btnback;
    E_user e_users = new E_user();
    boolean testcon = false;
    DataaccesOffline dataaccesOffline;
    DataaccesManifest dataaccesManifest;
    boolean hasilupload = false;
    @SuppressLint({"WrongViewCast", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        //getSupportActionBar().hide(); // hide the title bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.setting);
        edtserver = findViewById(R.id.edtserver);
        edtusersql = findViewById(R.id.edtusersql);
        edtpasswordsql = findViewById(R.id.edtpasswordsql);
        edtBHTID = findViewById(R.id.edtbhtid);
        RbWifi = findViewById(R.id.rbwifi);
        RbBatch = findViewById(R.id.rbbatch);
        btnset = findViewById(R.id.btnsavesettings);
        btnback = findViewById(R.id.imageView);
        btnTestConnection = findViewById(R.id.btntestconnect);
        dataaccesUserOffline = new DataaccesUserOffline(this);
        dataaccesOffline = new DataaccesOffline(this);
        dataaccesOffline.CreateTable();
        dataaccesManifest = new DataaccesManifest();
        loadingDialog = new LoadingDialog(settings.this);
        Load();
        btnback.setOnClickListener(v -> finish());
        RbWifi.setOnClickListener(v -> {

        });
        RbBatch.setOnClickListener(v -> {

        });
        tv = findViewById(R.id.msgbox);
        _vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        loadingDialog = new LoadingDialog(settings.this);
    }

    public void TestConnection (View v){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        ResultSet Rs ;
        try {
            String server = null;
            String Instance = null ;

            String[] checkInstance = edtserver.getText().toString().split("\\\\");
            if(checkInstance.length > 1){
                server = checkInstance[0];
                Instance = checkInstance[1];
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                ConnURL = "jdbc:jtds:sqlserver://" + server + ";"
                        + "databaseName=DB_RCS_TACI;user=" + edtusersql.getText()+ ";password=" + edtpasswordsql.getText() + ";instance=" + Instance + ";";
            }else{
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                ConnURL = "jdbc:jtds:sqlserver://" + edtserver.getText() + ";"
                        + "databaseName=DB_RCS_TACI;user=" + edtusersql.getText()+ ";password=" + edtpasswordsql.getText() + ";";
            }

            conn = DriverManager.getConnection(ConnURL);
            String query = "SELECT * FROM sys.databases WHERE name = 'DB_RCS_TACI';";
            PreparedStatement ps = conn.prepareStatement(query);
            Log.e("query", query);
            Rs = ps.executeQuery();
            if (Rs == null) {
                testcon = false;
                startTone("NG",settings.this);
                ab = new AlertDialog.Builder(settings.this);
                ab.setMessage("Database Failed...Please check database!");
                ab.setCancelable(false);
                ab.setPositiveButton("Error", null);
                AlertDialog dg = ab.create();
                dg.setIcon(R.drawable.dlg_oke);
                dg.setTitle("OK!");
                dg.show();
                return;
            } else if (Rs.next()) {
                testcon = true;
                if (RbWifi.isChecked()) {
                    boolean xcheck = false;
                    String CheckTransaksir = dataaccesOffline.CheckManifest("Operator Receiving");
                    if (CheckTransaksir.equals("ADA DATA")) {
                        xcheck = true;
                    }
                    String CheckTransaksiq = dataaccesOffline.CheckManifest("Operator QC");
                    if (CheckTransaksiq.equals("ADA DATA")) {
                       xcheck = true;
                    }
                    if (xcheck == true) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(settings.this);
                        builder.setMessage("Are you sure to upload data?");
                        builder.setPositiveButton("Yes", (dialog, which) -> {

                                startUpload();


                        });
                        builder.setNegativeButton("No", (dialog, which) -> {
                            // Do something when No button clicked
                            testcon = false;
                            RbBatch.setChecked(true);
                        });

                        AlertDialog dialog = builder.create();
                        // Display the alert dialog on interface
                        dialog.show();
                    }else{
                        testcon = true;
                        btnset.setEnabled(true);
                        startTone("OK",settings.this);
                        ab = new AlertDialog.Builder(settings.this);
                        ab.setMessage("Test Connection Success");
                        ab.setCancelable(false);
                        ab.setPositiveButton("OK", null);
                        AlertDialog dg = ab.create();
                        dg.setIcon(R.drawable.dlg_oke);
                        dg.setTitle("OK!");
                        dg.show();
                        return;
                    }
                }else{
                    testcon = true;
                    btnset.setEnabled(true);
                    startTone("OK",settings.this);
                    ab = new AlertDialog.Builder(settings.this);
                    ab.setMessage("Test Connection Success");
                    ab.setCancelable(false);
                    ab.setPositiveButton("OK", null);
                    AlertDialog dg = ab.create();
                    dg.setIcon(R.drawable.dlg_oke);
                    dg.setTitle("OK!");
                    dg.show();
                    return;
                }
            }
        } catch (SQLException se) {
            startTone("NG",settings.this);
            ab = new AlertDialog.Builder(settings.this);
            ab.setMessage("Connection Failed...Please check username and password!");
            ab.setCancelable(false);
            ab.setPositiveButton("Error", null);
            AlertDialog dg = ab.create();
            dg.setIcon(R.drawable.dlg_oke);
            dg.setTitle("Error!");
            dg.show();
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
            startTone("NG",settings.this);
            ab = new AlertDialog.Builder(settings.this);
            ab.setMessage("Connection Failed...Please check username and password!");
            ab.setCancelable(false);
            ab.setPositiveButton("Error", null);
            AlertDialog dg = ab.create();
            dg.setIcon(R.drawable.dlg_oke);
            dg.setTitle("Error!");
            dg.show();
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
            startTone("NG",settings.this);
            ab = new AlertDialog.Builder(settings.this);
            ab.setMessage("Connection Failed...Please check username and password!");
            ab.setCancelable(false);
            ab.setPositiveButton("Error", null);
            AlertDialog dg = ab.create();
            dg.setIcon(R.drawable.dlg_oke);
            dg.setTitle("Error!");
            dg.show();
        }

    }
    public void save (View v ){
        String Connection = "";
        if (RbBatch.isChecked()){
            Connection ="Offline";
        }else if (RbWifi.isChecked()){
            Connection = "Online";
        }
        String server = edtserver.getText().toString();
        String usersql = edtusersql.getText().toString();
        String passwordsql = edtpasswordsql.getText().toString();
        String DeviceID = edtBHTID.getText().toString();
        ConnectionHelper.server = server;
        ConnectionHelper.user = usersql;
        ConnectionHelper.passwd = passwordsql;
        ConnectionHelper.connection = Connection;
        E_user.DeviceID = DeviceID;
        String text = server + "|" + usersql + "|" + passwordsql + "|" + edtBHTID.getText().toString() + "|" + Connection ;
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
            if (Connection.equals("Offline")){
                try {
                    ResultSet Rs ;
                    Connection connect = ConnectionHelper.CONN();
                    if (connect == null){
                        startTone("NG",settings.this);
                        ab = new AlertDialog.Builder(settings.this);
                        ab.setMessage("Connection Failed...Please check username and password!");
                        ab.setCancelable(false);
                        ab.setPositiveButton("Error", null);
                        AlertDialog dg = ab.create();
                        dg.setIcon(R.drawable.dlg_oke);
                        dg.setTitle("Error!");
                        dg.show();
                    }else {
                        String query = "";
                        query = "SELECT * FROM SATO_USER";
                        PreparedStatement ps = connect.prepareStatement(query);
                        Log.e("query", query);
                        Rs = ps.executeQuery();
                        if (Rs != null) {
                            try {
                                String Delete = dataaccesUserOffline.DeleteDataUser();
                                while (Rs.next()) {
                                    String UserID = Rs.getString("UserID");
                                    String UserName = Rs.getString("UserName");
                                    String Password = Rs.getString("Password");
                                    String Authority = Rs.getString("Authority");
                                    String Blokir = Rs.getString("Blokir");
                                        dataaccesUserOffline.InsertDataUser(UserID,UserName,Authority,Blokir,Password);
                                }
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        }else{
                            startTone("NG",settings.this);
                            ab = new AlertDialog.Builder(settings.this);
                            ab.setMessage("Error Get Data User!, Data User Not Found");
                            ab.setCancelable(false);
                            ab.setPositiveButton("Error", null);
                            AlertDialog dg = ab.create();
                            dg.setIcon(R.drawable.dlg_oke);
                            dg.setTitle("Error!");
                            dg.show();
                        }
                    }
                } catch (Exception e) {
                    startTone("NG",settings.this);
                    ab = new AlertDialog.Builder(settings.this);
                    ab.setMessage("Error : " + e.toString());
                    ab.setCancelable(false);
                    ab.setPositiveButton("Error", null);
                    AlertDialog dg = ab.create();
                    dg.setIcon(R.drawable.dlg_oke);
                    dg.setTitle("Error!");
                    dg.show();
                }
            }
            startTone("OK",settings.this);
            ab = new AlertDialog.Builder(settings.this);
            ab.setMessage("Save Connection Success");
            ab.setCancelable(false);
            ab.setPositiveButton("OK", null);
            AlertDialog dg = ab.create();
            dg.setIcon(R.drawable.dlg_oke);
            dg.setTitle("OK!");
            dg.show();
            return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void Load(){
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text);
            }
            StringTokenizer st = new StringTokenizer(sb.toString(), "|");
            String server = st.nextToken();
            String user = st.nextToken();
            String pasword = st.nextToken();
            String bhtid = st.nextToken();
            String connection = st.nextToken();
            edtserver.setText(server);
            edtusersql.setText(user);
            edtpasswordsql.setText(pasword);
            edtBHTID.setText(bhtid);
            if (connection.equals("Online")){
                RbWifi.setChecked(true);
            }else{
                RbBatch.setChecked(true);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    //upload
    private void startUpload() {
        String url = "";
        E_user.textLoading = "Uploading Data....";
        new settings.UploadFileAsync().execute(url);
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
                    int x = 2;
                    String OperatorAwal = E_user.getRole();
                    for (int i = 0; i < x; i++) {
                        if (i == 0) {
                            E_user.setRole("Operator Receiving");
                        } else {
                            E_user.setRole("Operator QC");
                        }
                        Cursor cursor;
                        String xxx = E_user.getRole();
                        boolean infosuccess = false;
                        if (!TextUtils.isEmpty(xxx)) {
                            cursor = dataaccesOffline.GetManifestUpload(E_user.getRole());
                            for (int cc = 0; cc < cursor.getCount(); cc++) {
                                cursor.moveToPosition(cc);
                                String ManifestNo = cursor.getString(0);
                                String PartNo = cursor.getString(1);
                                String CheckServer = dataaccesManifest.CheckManifestComplete(ManifestNo, PartNo, E_user.getRole());
                                if (CheckServer.equals("Complete")) {
                                    boolean DeleteLokal = dataaccesOffline.DeleteLokal(ManifestNo, PartNo, E_user.getRole());
                                    infosuccess = true;
                                } else {
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
                                            } else {
                                                infosuccess = true;
                                            }
                                        } else {
                                            infosuccess = true;
                                        }
                                    } else {
                                        //QC
                                        String check = "  ";
                                        check = dataaccesOffline.GetDataUploadRQC(ManifestNo, PartNo);
                                        if (!check.substring(0, 2).equals("ER")) {
                                            String DeleteReceivingUpload = dataaccesOffline.DeleteUploadQCComplete(ManifestNo, PartNo);
                                            if (!DeleteReceivingUpload.equals("OK")) {
                                                infosuccess = false;
                                            } else {
                                                infosuccess = true;
                                            }
                                        } else {
                                            infosuccess = true;
                                        }
                                    }
                                }
                            }


                            if (E_user.getRole().equals("Operator Receiving")) {
                                if (infosuccess) {
                                    hasilupload = true;
                                } else {
                                    hasilupload = true;
                                }
                            } else {
                                if (infosuccess) {
                                    hasilupload = true;
                                } else {
                                    hasilupload = true;
                                }
                            }
                        }
                    }
                    E_user.setRole(OperatorAwal);
                }else{
                    startTone("NG",settings.this);
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
            if (hasilupload){
                try {
                    loadingDialog.dismissDialog();
                    testcon = true;
                    btnset.setEnabled(true);
                    startTone("OK",settings.this);
                    ab = new AlertDialog.Builder(settings.this);
                    ab.setMessage("Test Connection Success");
                    ab.setCancelable(false);
                    ab.setPositiveButton("OK", null);
                    AlertDialog dg = ab.create();
                    dg.setIcon(R.drawable.dlg_oke);
                    dg.setTitle("OK!");
                    dg.show();
                }catch (Exception e){
                    Log.e(e.toString(),"Error Test Connection Success");
                }

            }else{
                loadingDialog.dismissDialog();
                startTone("NG",settings.this);
                ab = new AlertDialog.Builder(settings.this);
                ab.setMessage("Upload Failed!");
                ab.setCancelable(false);
                ab.setPositiveButton("OK", null);
                AlertDialog dg = ab.create();
                dg.setIcon(R.drawable.dlg_oke);
                dg.setTitle("NG!");
                dg.show();
            }

        }
    }

    //Upload
    LoadingDialog loadingDialog;
    private ProgressDialog mProgressDialog;

    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
    TextView tv ;
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
    protected void startTone(String name, Context context) {

        if(name.equals("NG")) {
            final MediaPlayer
                    mediaPlayer = MediaPlayer.create(context, R.raw.ng_efect);
            mediaPlayer.start();

        }else if(name.equals("OK")){
            final MediaPlayer
                    mediaPlayer = MediaPlayer.create(context,R.raw.tone_ng);
            mediaPlayer.start();

        }
    }
}