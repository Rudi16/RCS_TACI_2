package com.example.rcs_taci_2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.rcs_taci_2.Dataacces.DataaccesManifest;
import com.example.rcs_taci_2.Dataacces.DataaccesOffline;
import com.example.rcs_taci_2.Entity.E_Manifest;
import com.example.rcs_taci_2.Entity.E_user;
import com.example.rcs_taci_2.Helper.ConnectionHelper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class receiving_kanban extends AppCompatActivity {
    EditText manifest,edtsupplierName,edtsupplierCode,edtScanKanban,edtScanPartNo,edtQtyKanban,edtQtyPart;
    private AlertDialog.Builder Alertdialog;
    Button btnComplete,btnCancel;
    DataaccesManifest dataaccesManifest;
    DataaccesOffline dataaccesOffline;
    TextView tv ;
    private Vibrator _vibrator;
    private AlertDialog.Builder ab;
    private Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receiving_kanban);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(color);
        if (E_user.getRole().equals("Operator Receiving"))
            toolbar.setTitle("RECEIVING");
        else if(E_user.getRole().equals("Operator QC"))
            toolbar.setTitle("QC CHECK");
        _vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        manifest = findViewById(R.id.edtmanifest);
        edtsupplierName= findViewById(R.id.edtsupliername);
        edtsupplierCode= findViewById(R.id.edtsupliercode);
        edtScanKanban= findViewById(R.id.edtscankanban);
        edtScanPartNo= findViewById(R.id.edtpartnokanban);
        edtQtyKanban= findViewById(R.id.edtqtykanban);
        edtQtyPart = findViewById(R.id.edtqtypart);
        tv = findViewById(R.id.msgreturn);
        boolean checkScanMonitoring = false;
        dataaccesManifest = new DataaccesManifest();
        dataaccesOffline = new DataaccesOffline(this);
        manifest.setText(E_Manifest.getManifestNo());
        edtsupplierCode.setText(E_Manifest.getSupplierCode());
        edtsupplierName.setText(E_Manifest.getSupplierName());
        //manifest.read
        edtScanKanban.requestFocus();
        btnComplete = findViewById(R.id.btncompletereceive);
        btnCancel = findViewById(R.id.btnback);

        btnCancel.setOnClickListener(v -> finish());

       btnComplete.setOnClickListener(v -> {
           AlertDialog.Builder builder = new AlertDialog.Builder(receiving_kanban.this);

           // Set a title for alert dialog
           // Ask the final question
           builder.setMessage("Manual Complete Manifest?");
           builder.setPositiveButton("Yes", (dialog, which) -> {
               // Do something when user clicked the Yes button
               // Set the TextView visibility GONE
               if (ConnectionHelper.connection.equals("Online")) {
                   String CheckKanban = dataaccesManifest.ManualComplete(manifest.getText().toString());
                   if (CheckKanban.equals("OK")) {
                       dataaccesOffline.ScanKanbanBackupOnline(0,manifest.getText().toString(),0,"0","0","Manifest Complete");
                       String CheckKanbanOnline = dataaccesOffline.ManualComplete(manifest.getText().toString());
                       if (CheckKanbanOnline.equals("OK")) {
                           startTone("OK");
                           finish();
                       } else {
                           showmessage(CheckKanban);
                       }
                       startTone("OK");
                       finish();
                   } else {
                       showmessage(CheckKanban);
                   }
               }else{
                   String CheckKanban = dataaccesOffline.ManualComplete(manifest.getText().toString());
                   if (CheckKanban.equals("OK")) {
                       startTone("OK");
                       finish();
                   } else {
                       showmessage(CheckKanban);
                   }
               }

           });
           builder.setNegativeButton("No", (dialog, which) -> {
               // Do something when No button clicked

           });

           AlertDialog dialog = builder.create();
           // Display the alert dialog on interface
           dialog.show();
       });

        edtScanKanban.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == 1) {
                String barcode = edtScanKanban.getText().toString();
                if (barcode.equals("") || barcode == null) {
                    startTone("NG");
                    tv.setTextColor(Color.WHITE);
                    tv.setBackgroundColor(Color.rgb(255,0,0));
                    showmessage("Please scan a QRCode.");
                    return true;
                }else{
                    String SuplierCode ="";
                    String PartNo="";
                    int Qty =0;
                    String Serial="";
                    if (barcode.length() == 35){
                        SuplierCode = barcode.substring(0,6).trim();
                        PartNo = barcode.substring(6,21).trim();
                        edtScanPartNo.setText(PartNo);
                        Qty  = Integer.parseInt(barcode.substring(21,28));
                        Serial = barcode.substring(28,35).trim();

                    }else if (barcode.length() == 45){
                        SuplierCode = barcode.substring(0,6).trim();
                        PartNo = barcode.substring(6,21).trim();
                        edtScanPartNo.setText(PartNo);
                        Qty  = Integer.parseInt(barcode.substring(21,28));
                        Serial = barcode.substring(28,35).trim();
                        String ManifestNoKanban = barcode.substring(35,45).trim();
                        if (!manifest.getText().toString().equals(ManifestNoKanban)) {
                            startTone("NG");
                            tv.setTextColor(Color.WHITE);
                            tv.setBackgroundColor(Color.rgb(255,0,0));
                            showmessage("Manifest No. Different!");
                            edtScanKanban.setText("");
                            edtScanKanban.requestFocus();
                            return true;
                        }
                    }else{
                        startTone("NG");
                        tv.setTextColor(Color.WHITE);
                        tv.setBackgroundColor(Color.rgb(255,0,0));
                        showmessage("Wrong QRCode.");
                        edtScanKanban.setText("");
                        edtScanKanban.requestFocus();
                        return true;
                    }
                    if (ConnectionHelper.connection.equals("Online")) {
                        if (SuplierCode.equals(edtsupplierCode.getText().toString())) {
                            ResultSet Rs = dataaccesManifest.CheckKanban(manifest.getText().toString(),
                                    SuplierCode, PartNo, Qty, Serial);
                            if (Rs != null) {
                                try {
                                    if (Rs.next()) {
                                        String STS = Rs.getString("STS");
                                        if (STS.substring(0, 2).equals("ER")) {
                                            startTone("NG");
                                            tv.setTextColor(Color.WHITE);
                                            tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                            showmessage(STS.replace("ER - ", ""));
                                            edtScanKanban.setText("");
                                            edtScanKanban.requestFocus();
                                            return true;
                                        } else if (STS.equals("Part Complete")) {
                                            dataaccesOffline.ScanKanbanBackupOnline(100,manifest.getText().toString(),Qty,Serial,PartNo,"Part Complete");
                                            edtQtyKanban.setText("");
                                            edtQtyPart.setText("");
                                            edtScanPartNo.setText("");
                                            startTone("OK");
                                            tv.setTextColor(Color.BLACK);
                                            tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                            showmessage("Part Complete");
                                            edtScanKanban.setText("");
                                            edtScanKanban.requestFocus();

                                            return true;
                                        } else if (STS.equals("LANJUT")) {
                                            dataaccesOffline.ScanKanbanBackupOnline(100,manifest.getText().toString(),Qty,Serial,PartNo,"LANJUT");
                                            startTone("OK");
                                            edtQtyKanban.setText(Rs.getString("QTY_SCAN"));
                                            edtQtyPart.setText(Rs.getString("QTY_PART"));
                                            edtScanKanban.setText("");
                                            edtScanKanban.requestFocus();

                                            return true;
                                        } else if (STS.equals("Manifest Complete")) {
                                            dataaccesOffline.ScanKanbanBackupOnline(100,manifest.getText().toString(),Qty,Serial,PartNo,"Manifest Complete");
                                            startTone("OK");
                                            tv.setTextColor(Color.BLACK);
                                            tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                            //showmessage("Manifest Complete");
                                            edtScanKanban.setText("");
                                            ab = new AlertDialog.Builder(receiving_kanban.this);
                                            ab.setMessage("Manifest Complete");
                                            ab.setCancelable(false);
                                            ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    finish();
                                                }
                                            });
                                            AlertDialog dg = ab.create();
                                            dg.setIcon(R.drawable.dlg_oke);
                                            dg.setTitle("OK!");
                                            dg.show();
                                            /*edtScanKanban.requestFocus();
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                public void run() {
                                                    finish();
                                                }
                                            }, 3000);*/
                                            return true;
                                        }

                                    }
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                            } else {
                                startTone("NG");
                                Alertdialog = new AlertDialog.Builder(receiving_kanban.this);
                                Alertdialog.setMessage("Error Scan Kanban, Check Connection Server");
                                Alertdialog.setCancelable(false);
                                Alertdialog.setPositiveButton("OK", null);
                                AlertDialog dg = Alertdialog.create();
                                dg.setIcon(R.drawable.dlg_alrt);
                                dg.setTitle("Error!");
                                dg.show();
                                edtScanKanban.setText("");
                                edtScanKanban.requestFocus();

                                return true;
                            }
                        } else {

                            startTone("NG");
                            tv.setTextColor(Color.WHITE);
                            tv.setBackgroundColor(Color.rgb(255, 0, 0));
                            showmessage("Wrong Suplier Code");
                            edtScanKanban.setText("");
                            edtScanKanban.requestFocus();
                            return true;
                        }
                    }else{
                        //Offline
                        if (SuplierCode.equals(edtsupplierCode.getText().toString())) {
                            String STS = dataaccesOffline.CheckKanbanReceiving(manifest.getText().toString(),
                                    SuplierCode, PartNo.trim(), Qty, Serial);
                            try {
                                if (STS.substring(0, 2).equals("ER")) {
                                    startTone("NG");
                                    tv.setTextColor(Color.WHITE);
                                    tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                    showmessage(STS.replace("ER - ", ""));
                                    edtScanKanban.setText("");
                                    edtScanKanban.requestFocus();
                                    return true;
                                } else if (STS.equals("Part Complete")) {
                                    edtQtyKanban.setText("");
                                    edtQtyPart.setText("");
                                    edtScanPartNo.setText("");
                                    startTone("OK");
                                    tv.setTextColor(Color.BLACK);
                                    tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                    showmessage("Part Complete");
                                    edtScanKanban.setText("");
                                    edtScanKanban.requestFocus();
                                    return true;
                                } else if (STS.equals("LANJUT")) {
                                    startTone("OK");

                                    edtQtyKanban.setText(String.valueOf(E_user.QtySCNRCV));
                                    edtQtyPart.setText(String.valueOf(E_user.QtyManifestRCV));
                                    edtScanKanban.setText("");
                                    edtScanKanban.requestFocus();
                                    return true;
                                } else if (STS.equals("Manifest Complete")) {
                                    startTone("OK");
                                    tv.setTextColor(Color.BLACK);
                                    tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                    //showmessage("Manifest Complete");
                                    edtScanKanban.setText("");
                                    ab = new AlertDialog.Builder(receiving_kanban.this);
                                    ab.setMessage("Manifest Complete");
                                    ab.setCancelable(false);
                                    ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    });
                                    AlertDialog dg = ab.create();
                                    dg.setIcon(R.drawable.dlg_oke);
                                    dg.setTitle("OK!");
                                    dg.show();
                                    /*edtScanKanban.requestFocus();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            finish();
                                        }
                                    }, 3000);*/
                                    return true;
                                }
                            } catch (Exception e) {
                                startTone("NG");
                                tv.setTextColor(Color.WHITE);
                                tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                showmessage(e.toString());
                                edtScanKanban.setText("");
                                edtScanKanban.requestFocus();
                                return true;
                            }
                        } else {

                            startTone("NG");
                            tv.setTextColor(Color.WHITE);
                            tv.setBackgroundColor(Color.rgb(255, 0, 0));
                            showmessage("Wrong Suplier Code");
                            edtScanKanban.setText("");
                            edtScanKanban.requestFocus();
                            return true;
                        }
                    }

                }
            }
            return false;
        });


    }
    private void showmessage(String isimsg){
        tv.setText(isimsg);
        tv.setVisibility(View.VISIBLE);
        tv.postDelayed(() -> {
            tv.setVisibility(View.INVISIBLE);
        }, 3000);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        if (ConnectionHelper.connection.equals("Offline")){
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this,R.drawable.offline));
        }else{
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this,R.drawable.online));
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        return;
    }
}