package com.example.rcs_taci_2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.ImageButton;
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
import java.util.StringTokenizer;

public class qc_kanban extends AppCompatActivity {
    EditText manifest,edtsupplierName,edtsupplierCode,edtScanKanban,edtScanPartNo,edtQtyKanban,edtQtyPart,EdtQtySampling;
    TextView txtQtyKanban,txtQtyPart,txtQtySampling;
    private AlertDialog.Builder Alertdialog;
    Button btnQuarantine,btnCancel;
    DataaccesManifest dataaccesManifest;
    DataaccesOffline dataaccesOffline;
    TextView tv ;
    private boolean ScanFirst = false;
    private String PartNoTemp = "";
    private int QtyTemp =0;
    private double Hasil = 0;
    private Vibrator _vibrator;
    private AlertDialog.Builder ab;
    private String SupplierCode = "";
    private String PartNo ="";
    private  String Serial ="";
    private Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qc_kanban);
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
        manifest = findViewById(R.id.edtmanifestqc);
        edtsupplierName= findViewById(R.id.edtsupliernameqc);
        edtsupplierCode= findViewById(R.id.edtsupliercodeqc);
        edtScanKanban= findViewById(R.id.edtscankanbanqc);
        edtScanPartNo= findViewById(R.id.edtpartnokanbanqc);
        edtQtyKanban= findViewById(R.id.edtqtykanbanqc);
        edtQtyPart = findViewById(R.id.edtqtypartqc);
        EdtQtySampling = findViewById(R.id.edtqtysampling);
        txtQtySampling = findViewById(R.id.txtqtysampling);
        txtQtyKanban = findViewById(R.id.txtqtyscan);
        txtQtyPart = findViewById(R.id.txttotalqtypart);
        tv = findViewById(R.id.msgqc);
        dataaccesManifest = new DataaccesManifest();
        dataaccesOffline = new DataaccesOffline(this);
        manifest.setText(E_Manifest.getManifestNo());
        edtsupplierCode.setText(E_Manifest.getSupplierCode());
        edtsupplierName.setText(E_Manifest.getSupplierName());
        //manifest.read
        edtScanKanban.requestFocus();
        btnQuarantine = findViewById(R.id.btnquarantine);
        btnCancel = findViewById(R.id.btnbackqc);

        btnCancel.setOnClickListener(v -> finish());

        btnQuarantine.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(qc_kanban.this);
            // Set a title for alert dialog
            // Ask the final question
            builder.setMessage("Are you sure?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                // Do something when user clicked the Yes button
                // Set the TextView visibility GONE
                if (edtScanPartNo.getText().toString().equals("")){
                    startTone("NG");
                    tv.setTextColor(Color.WHITE);
                    tv.setBackgroundColor(Color.rgb(255, 0, 0));
                    showmessage("Please Scan Kanban");
                    edtScanPartNo.requestFocus();
                    return;
                }
                if (ConnectionHelper.connection.equals("Online")) {
                    String CheckKanban = dataaccesManifest.SetQuarantine(manifest.getText().toString(), edtScanPartNo.getText().toString());
                    if (CheckKanban.equals("OK")) {
                        ResultSet Rs = dataaccesManifest.GetQuarantineData(manifest.getText().toString(),
                                edtScanPartNo.getText().toString());
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
                                        return;
                                    } else if (!STS.equals("Complete")) {
                                        // showmessage("Please Scan Again");
                                        //Quarantine
                                        toolbar.setBackgroundColor(Color.YELLOW);
                                        toolbar.setTitleTextColor(Color.BLACK);
                                        btnQuarantine.setVisibility(View.INVISIBLE);
                                        txtQtyPart.setText("Total Qty Part");
                                        txtQtyKanban.setText("Qty Scan");
                                        edtQtyKanban.setText(Rs.getString("Qty_Part"));
                                        edtQtyPart.setText(Rs.getString("Qty_Manifest"));
                                        QtyTemp = Rs.getInt("QTY_SCAN");
                                        PartNoTemp = edtScanPartNo.getText().toString();
                                        EdtQtySampling.setVisibility(View.INVISIBLE);
                                        txtQtySampling.setVisibility(View.INVISIBLE);
                                        txtQtyKanban.setVisibility(View.VISIBLE);
                                        txtQtyPart.setVisibility(View.VISIBLE);
                                        edtQtyKanban.setVisibility(View.VISIBLE);
                                        edtQtyPart.setVisibility(View.VISIBLE);
                                        edtScanKanban.setText("");
                                        edtScanKanban.requestFocus();
                                        startTone("OK");
                                        return;
                                    } else if (STS.equals("Complete")) {
                                        startTone("OK");
                                        tv.setTextColor(Color.BLACK);
                                        tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                        showmessage("Part Complete");
                                        edtScanKanban.setText("");
                                        edtScanKanban.requestFocus();
                                        ScanFirst = false;
                                        edtScanPartNo.setText("");
                                        edtQtyKanban.setText("");
                                        edtQtyPart.setText("");
                                        EdtQtySampling.setText("");
                                        return;
                                    }
                                }
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        } else {
                            startTone("NG");
                            Alertdialog = new AlertDialog.Builder(qc_kanban.this);
                            Alertdialog.setMessage("Error Set Quarantine");
                            Alertdialog.setCancelable(false);
                            Alertdialog.setPositiveButton("OK", null);
                            AlertDialog dg = Alertdialog.create();
                            dg.setIcon(R.drawable.dlg_alrt);
                            dg.setTitle("Error!");
                            dg.show();
                            edtScanKanban.setText("");
                            edtScanKanban.requestFocus();

                            return;
                        }
                    } else {
                        showmessage(CheckKanban);
                    }
                }else{
                    String CheckKanban = dataaccesOffline.SetQuarantine(manifest.getText().toString(), edtScanPartNo.getText().toString());
                    if (CheckKanban.equals("OK")) {
                        Cursor cursor = dataaccesOffline.GetQuarantineData(manifest.getText().toString(),
                                edtScanPartNo.getText().toString());
                        if (cursor != null) {
                            try {
                                cursor.moveToFirst();
                                if (cursor.getCount() != 0) {
                                    String STS =  cursor.getString(1);
                                     if (!STS.equals("Complete")) {
                                        // showmessage("Please Scan Again");
                                        //Quarantine
                                         toolbar.setBackgroundColor(Color.YELLOW);
                                         toolbar.setTitleTextColor(Color.BLACK);
                                         btnQuarantine.setVisibility(View.INVISIBLE);
                                        txtQtyPart.setText("Total Qty Part");
                                        txtQtyKanban.setText("Qty Scan");
                                        edtQtyKanban.setText(cursor.getString(3));
                                        edtQtyPart.setText(cursor.getString(2));
                                        QtyTemp = cursor.getInt(0);
                                        PartNoTemp = edtScanPartNo.getText().toString();
                                        EdtQtySampling.setVisibility(View.INVISIBLE);
                                        txtQtySampling.setVisibility(View.INVISIBLE);
                                        txtQtyKanban.setVisibility(View.VISIBLE);
                                        txtQtyPart.setVisibility(View.VISIBLE);
                                        edtQtyKanban.setVisibility(View.VISIBLE);
                                        edtQtyPart.setVisibility(View.VISIBLE);
                                        edtScanKanban.setText("");
                                        edtScanKanban.requestFocus();
                                        startTone("OK");
                                        return;
                                    } else if (STS.equals("Complete")) {
                                        startTone("OK");
                                        tv.setTextColor(Color.BLACK);
                                        tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                        showmessage("Part Complete");
                                        edtScanKanban.setText("");
                                        edtScanKanban.requestFocus();
                                        ScanFirst = false;
                                        edtScanPartNo.setText("");
                                        edtQtyKanban.setText("");
                                        edtQtyPart.setText("");
                                        EdtQtySampling.setText("");
                                        return;
                                    }
                                }else{
                                    startTone("NG");
                                    Alertdialog = new AlertDialog.Builder(qc_kanban.this);
                                    Alertdialog.setMessage("Wrong Part No");
                                    Alertdialog.setCancelable(false);
                                    Alertdialog.setPositiveButton("OK", null);
                                    AlertDialog dg = Alertdialog.create();
                                    dg.setIcon(R.drawable.dlg_alrt);
                                    dg.setTitle("Error!");
                                    dg.show();
                                    edtScanKanban.setText("");
                                    edtScanKanban.requestFocus();

                                    return;
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            startTone("NG");
                            Alertdialog = new AlertDialog.Builder(qc_kanban.this);
                            Alertdialog.setMessage("Error Set Quarantine");
                            Alertdialog.setCancelable(false);
                            Alertdialog.setPositiveButton("OK", null);
                            AlertDialog dg = Alertdialog.create();
                            dg.setIcon(R.drawable.dlg_alrt);
                            dg.setTitle("Error!");
                            dg.show();
                            edtScanKanban.setText("");
                            edtScanKanban.requestFocus();

                            return;
                        }
                    } else {
                        showmessage(CheckKanban);
                    }
                }

            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do something when No button clicked

                }
            });

            AlertDialog dialog = builder.create();
            // Display the alert dialog on interface
            dialog.show();
        });
        btnQuarantine.setVisibility(View.INVISIBLE);
        edtQtyKanban.setVisibility(View.INVISIBLE);
        edtQtyPart.setVisibility(View.INVISIBLE);
        txtQtyPart.setVisibility(View.INVISIBLE);
        txtQtyKanban.setVisibility(View.INVISIBLE);
        EdtQtySampling.setVisibility(View.INVISIBLE);
        txtQtySampling.setVisibility(View.INVISIBLE);

        edtScanKanban.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == 1) {
                String barcode = edtScanKanban.getText().toString();

                int Qty =0;
                if (barcode.equals("") || barcode == null) {
                    startTone("NG");
                    Alertdialog = new AlertDialog.Builder(qc_kanban.this);
                    Alertdialog.setMessage("Please scan a QRCode.");
                    Alertdialog.setCancelable(false);
                    Alertdialog.setPositiveButton("OK", null);
                    AlertDialog dg = Alertdialog.create();
                    dg.setIcon(R.drawable.dlg_alrt);
                    dg.setTitle("Error!");
                    dg.show();
                    return true;
                }else{
                    if (barcode.length() == 35) {
                        SupplierCode = barcode.substring(0, 6).trim();
                        PartNo = barcode.substring(6, 21).trim();
                        edtScanPartNo.setText(PartNo);
                        Qty = Integer.parseInt(barcode.substring(21, 28));
                        if (PartNoTemp.equals(PartNo)) {
                            ScanFirst = true;
                        } else {
                            ScanFirst = false;
                        }
                        PartNoTemp = PartNo;
                        if (ScanFirst) {
                            if (QtyTemp != Qty) {
                                startTone("NG");
                                tv.setTextColor(Color.WHITE);
                                tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                showmessage("Different Qty Part");
                                edtScanKanban.setText("");
                                edtScanKanban.requestFocus();
                                return true;
                            }
                        }
                        Serial = barcode.substring(28, 35).trim();
                        if (!SupplierCode.equals(edtsupplierCode.getText().toString())) {
                            startTone("NG");
                            tv.setTextColor(Color.WHITE);
                            tv.setBackgroundColor(Color.rgb(255, 0, 0));
                            showmessage("Wrong Suplier Code");
                            PartNoTemp = "";
                            edtScanKanban.setText("");
                            edtScanKanban.requestFocus();
                            return true;
                        }
                    }else if (barcode.length() == 45){
                        SupplierCode = barcode.substring(0,6).trim();
                        PartNo = barcode.substring(6,21).trim();
                        edtScanPartNo.setText(PartNo);
                        Qty  = Integer.parseInt(barcode.substring(21,28));
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
                        if (PartNoTemp.equals(PartNo)) {
                            ScanFirst = true;
                        } else {
                            ScanFirst = false;
                        }
                        PartNoTemp = PartNo;
                        if (ScanFirst) {
                            if (QtyTemp != Qty) {
                                startTone("NG");
                                tv.setTextColor(Color.WHITE);
                                tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                showmessage("Different Qty Part");
                                edtScanKanban.setText("");
                                edtScanKanban.requestFocus();
                                return true;
                            }
                        }
                        Serial = barcode.substring(28, 35).trim();
                        if (!SupplierCode.equals(edtsupplierCode.getText().toString())) {
                            startTone("NG");
                            tv.setTextColor(Color.WHITE);
                            tv.setBackgroundColor(Color.rgb(255, 0, 0));
                            showmessage("Wrong Suplier Code");
                            PartNoTemp = "";
                            edtScanKanban.setText("");
                            edtScanKanban.requestFocus();
                            return true;
                        }


                    }else{
                        startTone("NG");
                        tv.setTextColor(Color.WHITE);
                        tv.setBackgroundColor(Color.rgb(255,0,0));
                        showmessage("Wrong QRCode.");
                        PartNoTemp="";
                        edtScanKanban.setText("");
                        edtScanKanban.requestFocus();
                        return true;
                    }

                    //ONLINE
                    if (ConnectionHelper.connection.equals("Online")) {
                        if (ScanFirst) {
                            ResultSet Rs = dataaccesManifest.CheckKanbanQC(manifest.getText().toString(),
                                    SupplierCode, PartNo, Qty, Serial);
                            if (Rs != null) {
                                try {
                                    if (Rs.next()) {
                                        String STS = Rs.getString("STS");
                                        String Status_Quarantine = Rs.getString("Status_Quarantine");
                                        if (STS.substring(0, 2).equals("ER")) {
                                            startTone("NG");
                                            tv.setTextColor(Color.WHITE);
                                            tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                            showmessage(STS.replace("ER - ", ""));
                                            edtScanKanban.setText("");
                                            edtScanKanban.requestFocus();
                                            return true;
                                        } else if (STS.equals("Part Already Complete")) {
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
                                            edtScanKanban.setText("");
                                            edtScanKanban.requestFocus();

                                            //Show Dialog NG OK
                                            final Dialog dialog = new Dialog(qc_kanban.this);
                                            dialog.setContentView(R.layout.dialog);
                                            dialog.setCancelable(false);
                                            dialog.show();

                                            Button btn_OK, btn_NG;
                                            ImageButton btn_close;
                                            btn_OK = dialog.findViewById(R.id.btn_yes);
                                            btn_NG = dialog.findViewById(R.id.btn_no);
                                            btn_close = dialog.findViewById(R.id.button_close);
                                            btn_close.setOnClickListener(v13 -> {
                                                dialog.dismiss();
                                            });
                                            int finalQty = Qty;
                                            btn_OK.setOnClickListener(view ->
                                            {
                                                ResultSet RsQC = dataaccesManifest.SaveKanbanQC(manifest.getText().toString(),
                                                        SupplierCode, PartNo, finalQty, Serial, "OK", "", (int) Hasil);
                                                if (RsQC != null) {
                                                    try {
                                                        if (RsQC.next()) {
                                                            String Status_Scan = RsQC.getString("STS");
                                                            if (Status_Quarantine.equals("Ya")) {
                                                                //Quarantine
                                                                toolbar.setBackgroundColor(Color.YELLOW);
                                                                toolbar.setTitleTextColor(Color.BLACK);
                                                                btnQuarantine.setVisibility(View.INVISIBLE);
                                                                txtQtyPart.setText("Total Qty Part");
                                                                txtQtyKanban.setText("Qty Scan");
                                                                String Qty_Kanban = RsQC.getString("QTY_SCAN");
                                                                edtQtyKanban.setText(Qty_Kanban);
                                                                String Qty_Part = RsQC.getString("QTY_PART");
                                                                edtQtyPart.setText(Qty_Part);
                                                                EdtQtySampling.setVisibility(View.INVISIBLE);
                                                                txtQtySampling.setVisibility(View.INVISIBLE);

                                                                if (Status_Scan.equals("Part Complete")) {
                                                                    startTone("OK");
                                                                    tv.setTextColor(Color.BLACK);
                                                                    tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                                                    showmessage("Part Complete");
                                                                    edtScanKanban.setText("");
                                                                    edtScanKanban.requestFocus();
                                                                    ScanFirst = false;
                                                                    edtScanPartNo.setText("");
                                                                    edtQtyKanban.setText("");
                                                                    edtQtyPart.setText("");
                                                                    EdtQtySampling.setText("");
                                                                } else if (Status_Scan.equals("Manifest Complete")) {
                                                                    startTone("OK");
                                                                       /* tv.setTextColor(Color.BLACK);
                                                                        tv.setBackgroundColor(Color.rgb(0, 255, 0));*/
                                                                    //showmessage("Manifest Complete");
                                                                    edtScanKanban.setText("");
                                                                    ab = new AlertDialog.Builder(qc_kanban.this);
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
                                                                    //edtScanKanban.requestFocus();
                                                                        /*Handler handler = new Handler();
                                                                        handler.postDelayed(new Runnable() {
                                                                            public void run() {
                                                                                finish();
                                                                            }
                                                                        }, 4000);*/
                                                                }
                                                            } else {
                                                                //Normal
                                                                toolbar.setTitleTextColor(Color.WHITE);
                                                                toolbar.setBackgroundColor(color);
                                                                btnQuarantine.setVisibility(View.VISIBLE);
                                                                txtQtyPart.setText("Qty Part");
                                                                txtQtyKanban.setText("Qty Sampling");
                                                                String Qty_Normal_Kanban = RsQC.getString("QTY_SCAN_NORMAL");
                                                                String Qty_Sampling = RsQC.getString("QTY_SAMPLING");
                                                                edtQtyKanban.setText(Qty_Normal_Kanban + " / " + Qty_Sampling);
                                                                edtQtyPart.setText(String.valueOf(finalQty));
                                                                EdtQtySampling.setVisibility(View.VISIBLE);
                                                                txtQtySampling.setVisibility(View.VISIBLE);

                                                                int QtySampling = RsQC.getInt("QTY_SAMPLING");
                                                                if (finalQty > QtySampling) {
                                                                    Hasil = 1;
                                                                } else if (finalQty < QtySampling) {
                                                                    Hasil = (double) QtySampling / (double) finalQty;
                                                                    Hasil = roundAvoid(Hasil, 0);
                                                                }
                                                                int ScanKanban = RsQC.getInt("QTY_SCAN_KANBAN");
                                                                EdtQtySampling.setText(ScanKanban + " / " + (int) Hasil);
                                                                if (Status_Scan.equals("Part Complete")) {
                                                                    startTone("OK");
                                                                    tv.setTextColor(Color.BLACK);
                                                                    tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                                                    showmessage("Part Complete");
                                                                    edtScanKanban.setText("");
                                                                    edtScanKanban.requestFocus();
                                                                    ScanFirst = false;
                                                                    edtScanPartNo.setText("");
                                                                    edtQtyKanban.setText("");
                                                                    edtQtyPart.setText("");
                                                                    EdtQtySampling.setText("");
                                                                } else if (Status_Scan.equals("Manifest Complete")) {
                                                                    startTone("OK");
                                                                        /*tv.setTextColor(Color.BLACK);
                                                                        tv.setBackgroundColor(Color.rgb(0, 255, 0));*/
                                                                    //showmessage("Manifest Complete");
                                                                    edtScanKanban.setText("");
                                                                    //edtScanKanban.requestFocus();
                                                                    ab = new AlertDialog.Builder(qc_kanban.this);
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
                                                                    dg.show();
                                                                        /*Handler handler = new Handler();
                                                                        handler.postDelayed(new Runnable() {
                                                                            public void run() {
                                                                                finish();
                                                                            }
                                                                        }, 4000);*/
                                                                }
                                                            }
                                                        }
                                                    } catch (Exception ex) {
                                                        startTone("NG");
                                                        Alertdialog = new AlertDialog.Builder(qc_kanban.this);
                                                        Alertdialog.setMessage("Error Save Data OK");
                                                        Alertdialog.setCancelable(false);
                                                        Alertdialog.setPositiveButton("OK", null);
                                                        AlertDialog dg = Alertdialog.create();
                                                        dg.setIcon(R.drawable.dlg_alrt);
                                                        dg.setTitle("Error!");
                                                        dg.show();
                                                        edtScanKanban.setText("");
                                                        edtScanKanban.requestFocus();
                                                    }

                                                } else {
                                                    startTone("NG");
                                                    Alertdialog = new AlertDialog.Builder(qc_kanban.this);
                                                    Alertdialog.setMessage("Error Save Data OK");
                                                    Alertdialog.setCancelable(false);
                                                    Alertdialog.setPositiveButton("OK", null);
                                                    AlertDialog dg = Alertdialog.create();
                                                    dg.setIcon(R.drawable.dlg_alrt);
                                                    dg.setTitle("Error!");
                                                    dg.show();
                                                    edtScanKanban.setText("");
                                                    edtScanKanban.requestFocus();
                                                }
                                                //Toast.makeText(qc_kanban.this, "You Clicked OK Button", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            });

                                            int finalQty1 = Qty;
                                            btn_NG.setOnClickListener(view -> {
                                                //Toast.makeText(qc_kanban.this, "You Clicked NG Button", Toast.LENGTH_SHORT).show();
                                                final Dialog dialogng = new Dialog(qc_kanban.this);
                                                dialogng.setContentView(R.layout.dialogng);
                                                dialogng.setCancelable(false);
                                                dialogng.show();

                                                Button btn_save, btn_cancel;
                                                EditText edtremark;
                                                btn_save = dialogng.findViewById(R.id.btnsave);
                                                btn_cancel = dialogng.findViewById(R.id.btncancel);
                                                edtremark = dialogng.findViewById(R.id.edtremarkng);
                                                btn_save.setOnClickListener(v12 ->
                                                {
                                                    ResultSet RsQC = dataaccesManifest.SaveKanbanQC(manifest.getText().toString(),
                                                            SupplierCode, PartNo, finalQty1, Serial, "NG", edtremark.getText().toString(), (int) Hasil);
                                                    if (RsQC != null) {
                                                        try {
                                                            if (RsQC.next()) {
                                                                String Status_Scan = RsQC.getString("STS");
                                                                if (Status_Quarantine.equals("Ya")) {
                                                                    //Quarantine
                                                                    toolbar.setBackgroundColor(Color.YELLOW);
                                                                    toolbar.setTitleTextColor(Color.BLACK);
                                                                    btnQuarantine.setVisibility(View.INVISIBLE);
                                                                    txtQtyPart.setText("Total Qty Part");
                                                                    txtQtyKanban.setText("Qty Scan");
                                                                    edtQtyKanban.setText(RsQC.getString("QTY_SCAN"));
                                                                    edtQtyPart.setText(RsQC.getString("QTY_PART"));
                                                                    EdtQtySampling.setVisibility(View.INVISIBLE);
                                                                    txtQtySampling.setVisibility(View.INVISIBLE);
                                                                    int QtySampling = RsQC.getInt("QTY_SAMPLING");
                                                                    if (finalQty1 > QtySampling) {
                                                                        Hasil = 1;
                                                                    } else if (finalQty1 < QtySampling) {
                                                                        Hasil = (double) QtySampling / (double) finalQty1;
                                                                        Hasil = roundAvoid(Hasil, 0);
                                                                    }
                                                                    int ScanKanban = RsQC.getInt("QTY_SCAN_KANBAN");
                                                                    EdtQtySampling.setText(ScanKanban + " / " + (int) Hasil);
                                                                    if (Status_Scan.equals("Part Complete")) {
                                                                        startTone("OK");
                                                                        tv.setTextColor(Color.BLACK);
                                                                        tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                                                        showmessage("Part Complete");
                                                                        edtScanKanban.setText("");
                                                                        edtScanKanban.requestFocus();
                                                                        ScanFirst = false;
                                                                        edtScanPartNo.setText("");
                                                                        edtQtyKanban.setText("");
                                                                        edtQtyPart.setText("");
                                                                        EdtQtySampling.setText("");
                                                                    } else if (Status_Scan.equals("Manifest Complete")) {
                                                                        startTone("OK");
                                                                             /* tv.setTextColor(Color.BLACK);
                                                                              tv.setBackgroundColor(Color.rgb(0, 255, 0));*/
                                                                        //showmessage("Manifest Complete");
                                                                        edtScanKanban.setText("");
                                                                        ab = new AlertDialog.Builder(qc_kanban.this);
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
                                                                             /* edtScanKanban.requestFocus();
                                                                              Handler handler = new Handler();
                                                                              handler.postDelayed(new Runnable() {
                                                                                  public void run() {
                                                                                      finish();
                                                                                  }
                                                                              }, 4000);*/
                                                                    }
                                                                } else {
                                                                    //Normal
                                                                    toolbar.setTitleTextColor(Color.WHITE);
                                                                    toolbar.setBackgroundColor(color);
                                                                    btnQuarantine.setVisibility(View.VISIBLE);
                                                                    txtQtyPart.setText("Qty Part");
                                                                    txtQtyKanban.setText("Qty Sampling");
                                                                    String Qty_Normal_Kanban = RsQC.getString("QTY_SCAN_NORMAL");
                                                                    String Qty_Sampling = RsQC.getString("QTY_SAMPLING");
                                                                    edtQtyKanban.setText(Qty_Normal_Kanban + " / " + Qty_Sampling);
                                                                    edtQtyPart.setText(String.valueOf(finalQty1));
                                                                    EdtQtySampling.setVisibility(View.VISIBLE);
                                                                    EdtQtySampling.setVisibility(View.VISIBLE);
                                                                    txtQtySampling.setVisibility(View.VISIBLE);

                                                                    int QtySampling = RsQC.getInt("QTY_SAMPLING");
                                                                    if (finalQty1 > QtySampling) {
                                                                        Hasil = 1;
                                                                    } else if (finalQty1 < QtySampling) {
                                                                        Hasil = (double) QtySampling / (double) finalQty1;
                                                                        Hasil = roundAvoid(Hasil, 0);
                                                                    }
                                                                    int ScanKanban = RsQC.getInt("QTY_SCAN_KANBAN");
                                                                    EdtQtySampling.setText(ScanKanban + " / " + (int) Hasil);
                                                                    if (Status_Scan.equals("Part Complete")) {
                                                                        startTone("OK");
                                                                        tv.setTextColor(Color.BLACK);
                                                                        tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                                                        showmessage("Part Complete");
                                                                        edtScanKanban.setText("");
                                                                        edtScanKanban.requestFocus();
                                                                        ScanFirst = false;
                                                                        edtScanPartNo.setText("");
                                                                        edtQtyKanban.setText("");
                                                                        edtQtyPart.setText("");
                                                                        EdtQtySampling.setText("");
                                                                    } else if (Status_Scan.equals("Manifest Complete")) {
                                                                        startTone("OK");
                                                                        // tv.setTextColor(Color.BLACK);
                                                                        // tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                                                        // showmessage("Manifest Complete");
                                                                        edtScanKanban.setText("");
                                                                        ab = new AlertDialog.Builder(qc_kanban.this);
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
                                                                             /* edtScanKanban.requestFocus();
                                                                              Handler handler = new Handler();
                                                                              handler.postDelayed(new Runnable() {
                                                                                  public void run() {
                                                                                      finish();
                                                                                  }
                                                                              }, 4000);*/
                                                                    }
                                                                }
                                                            }
                                                        } catch (Exception ex) {
                                                            startTone("NG");
                                                            Alertdialog = new AlertDialog.Builder(qc_kanban.this);
                                                            Alertdialog.setMessage("Error Save Data OK");
                                                            Alertdialog.setCancelable(false);
                                                            Alertdialog.setPositiveButton("OK", null);
                                                            AlertDialog dg = Alertdialog.create();
                                                            dg.setIcon(R.drawable.dlg_alrt);
                                                            dg.setTitle("Error!");
                                                            dg.show();
                                                            edtScanKanban.setText("");
                                                            edtScanKanban.requestFocus();
                                                        }

                                                    } else {
                                                        startTone("NG");
                                                        Alertdialog = new AlertDialog.Builder(qc_kanban.this);
                                                        Alertdialog.setMessage("Error Save Data OK");
                                                        Alertdialog.setCancelable(false);
                                                        Alertdialog.setPositiveButton("OK", null);
                                                        AlertDialog dg = Alertdialog.create();
                                                        dg.setIcon(R.drawable.dlg_alrt);
                                                        dg.setTitle("Error!");
                                                        dg.show();

                                                        startTone("NG");
                                                        tv.setTextColor(Color.WHITE);
                                                        tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                                        showmessage("Error Save Data OK");

                                                        edtScanKanban.setText("");
                                                        edtScanKanban.requestFocus();
                                                    }
                                                    dialogng.dismiss();
                                                    dialog.dismiss();
                                                });
                                                btn_cancel.setOnClickListener(v1 -> {
                                                    dialogng.dismiss();
                                                });
                                            });

                                            //Show Dialog NG OK

                                            return true;
                                        } else if (STS.equals("Manifest Complete")) {
                                            startTone("OK");
                                                  /*tv.setTextColor(Color.BLACK);
                                                  tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                                  showmessage("Manifest Complete");*/
                                            edtScanKanban.setText("");

                                            ab = new AlertDialog.Builder(qc_kanban.this);
                                            ab.setMessage("Manifest Complete");
                                            ab.setCancelable(false);
                                            ab.setPositiveButton("OK", null);
                                            AlertDialog dg = ab.create();
                                            dg.setIcon(R.drawable.dlg_oke);
                                            dg.setTitle("OK!");
                                            dg.show();
                                            finish();
                                                 /* edtScanKanban.requestFocus();
                                                  Handler handler = new Handler();
                                                  handler.postDelayed(new Runnable() {
                                                      public void run() {
                                                          finish();
                                                      }
                                                  }, 3000);*/
                                            return true;
                                        }

                                    }
                                } catch (SQLException throwable) {
                                    throwable.printStackTrace();
                                }
                            } else {
                                startTone("NG");
                                tv.setTextColor(Color.WHITE);
                                tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                showmessage("Error Scan Kanban");
                                edtScanKanban.setText("");
                                edtScanKanban.requestFocus();
                                return true;
                            }
                        } else {
                            // Scan Pertama Untuk manggil SOP
                            ResultSet Rs = dataaccesManifest.CheckKanbanQCFirst(manifest.getText().toString(),
                                    SupplierCode, PartNo, Qty, Serial);
                            if (Rs != null) {
                                try {
                                    if (Rs.next()) {
                                        String STS = Rs.getString("STS");
                                        String Status_Quarantine = Rs.getString("Status_Quarantine");

                                        if (STS.substring(0, 2).equals("ER")) {
                                            startTone("NG");
                                            tv.setTextColor(Color.WHITE);
                                            tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                            showmessage(STS.replace("ER - ", ""));
                                            edtScanKanban.setText("");
                                            edtScanKanban.requestFocus();
                                            PartNoTemp = "";
                                            return true;
                                        } else if (STS.equals("Part Complete")) {
                                            edtQtyKanban.setText("");
                                            edtQtyPart.setText("");
                                            edtScanPartNo.setText("");
                                            startTone("NG");
                                            tv.setTextColor(Color.BLACK);
                                            tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                            showmessage("Part Complete");
                                            edtScanKanban.setText("");
                                            edtScanKanban.requestFocus();
                                            PartNoTemp = "";
                                            return true;
                                        } else if (STS.equals("LANJUT")) {
                                            ScanFirst = true;
                                            QtyTemp = Qty;
                                            // showmessage("Please Scan Again");
                                            if (Status_Quarantine.equals("Ya")) {
                                                //Quarantine
                                                toolbar.setBackgroundColor(Color.YELLOW);
                                                toolbar.setTitleTextColor(Color.BLACK);
                                                btnQuarantine.setVisibility(View.INVISIBLE);
                                                txtQtyPart.setText("Total Qty Part");
                                                txtQtyKanban.setText("Qty Scan");
                                                edtQtyKanban.setText(Rs.getString("QTY_SCAN"));
                                                edtQtyPart.setText(Rs.getString("QTY_PART"));
                                                EdtQtySampling.setVisibility(View.INVISIBLE);
                                                txtQtySampling.setVisibility(View.INVISIBLE);
                                            } else {
                                                toolbar.setTitleTextColor(Color.WHITE);
                                                toolbar.setBackgroundColor(color);
                                                //Normal
                                                btnQuarantine.setVisibility(View.VISIBLE);
                                                txtQtyPart.setText("Qty Part");
                                                txtQtyKanban.setText("Qty Sampling");
                                                edtQtyKanban.setText(Rs.getString("QTY_SCAN_NORMAL") + " / " + Rs.getString("QTY_SAMPLING"));
                                                edtQtyPart.setText(String.valueOf(Qty));
                                                EdtQtySampling.setVisibility(View.VISIBLE);
                                                txtQtySampling.setVisibility(View.VISIBLE);

                                                int QtySampling = Rs.getInt("QTY_SAMPLING");
                                                if (Qty > QtySampling) {
                                                    Hasil = 1;
                                                } else if (Qty < QtySampling) {
                                                    Hasil = (double) QtySampling / (double) Qty;
                                                    Hasil = roundAvoid(Hasil, 0);
                                                }
                                                int ScanKanban = Rs.getInt("QTY_SCAN_KANBAN");

                                                EdtQtySampling.setText(ScanKanban + " / " + (int) Hasil);

                                            }
                                            txtQtyKanban.setVisibility(View.VISIBLE);
                                            txtQtyPart.setVisibility(View.VISIBLE);
                                            edtQtyKanban.setVisibility(View.VISIBLE);
                                            edtQtyPart.setVisibility(View.VISIBLE);
                                            edtScanKanban.setText("");
                                            edtScanKanban.requestFocus();
                                            startTone("OK");
                                            return true;
                                        } else if (STS.equals("Manifest Complete")) {
                                            startTone("OK");
                                            /*  tv.setTextColor(Color.BLACK);
                                              tv.setBackgroundColor(Color.rgb(0,255,0));
                                              showmessage("Manifest Complete");*/
                                            edtScanKanban.setText("");
                                            PartNoTemp = "";
                                            // edtScanKanban.requestFocus();
                                            ab = new AlertDialog.Builder(qc_kanban.this);
                                            ab.setMessage("Manifest Complete");
                                            ab.setCancelable(false);
                                            ab.setPositiveButton("OK", null);
                                            AlertDialog dg = ab.create();
                                            dg.setIcon(R.drawable.dlg_oke);
                                            dg.setTitle("OK!");
                                            dg.show();
                                            finish();
                                              /*Handler handler = new Handler();
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
                                tv.setTextColor(Color.WHITE);
                                tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                showmessage("Error Scan Kanban");
                                PartNoTemp = "";
                                edtScanKanban.setText("");
                                edtScanKanban.requestFocus();

                                return true;
                            }
                        }
                        //END ONLINE

                    }else{
                        //OFFLINE
                        if (ScanFirst) {
                            String sts = dataaccesOffline.CheckKanbanQC(manifest.getText().toString(),
                                    SupplierCode, PartNo, Qty, Serial);
                            String[] cek = split("|", sts);
                            String STS = cek[0];
                            String Status_Quarantine = cek[1];
                                try {
                                    if (STS.substring(0, 2).equals("ER")) {
                                        startTone("NG");
                                        tv.setTextColor(Color.WHITE);
                                        tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                        showmessage(STS.replace("ER - ", ""));
                                        edtScanKanban.setText("");
                                        edtScanKanban.requestFocus();
                                        return true;
                                    } else if (STS.equals("Part Already Complete")) {
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
                                        edtScanKanban.setText("");
                                        edtScanKanban.requestFocus();

                                        //Show Dialog NG OK
                                        final Dialog dialog = new Dialog(qc_kanban.this);
                                        dialog.setContentView(R.layout.dialog);
                                        dialog.setCancelable(false);
                                        dialog.show();

                                        Button btn_OK, btn_NG;
                                        ImageButton btn_close;
                                        btn_OK = dialog.findViewById(R.id.btn_yes);
                                        btn_NG = dialog.findViewById(R.id.btn_no);
                                        btn_close = dialog.findViewById(R.id.button_close);
                                        btn_close.setOnClickListener(v13 -> {
                                            dialog.dismiss();
                                        });
                                        int finalQty = Qty;
                                        btn_OK.setOnClickListener(view ->
                                        {
                                            String GetDataSave = dataaccesOffline.SaveKanbanQC(manifest.getText().toString(),
                                                    SupplierCode, PartNo, finalQty, Serial, "OK", "", (int) Hasil);
                                                try {
                                                    String Status_Scan = GetDataSave;
                                                    if (Status_Quarantine.equals("Ya")) {
                                                        //Quarantine
                                                        toolbar.setBackgroundColor(Color.YELLOW);
                                                        toolbar.setTitleTextColor(Color.BLACK);
                                                        btnQuarantine.setVisibility(View.INVISIBLE);
                                                        txtQtyPart.setText("Total Qty Part");
                                                        txtQtyKanban.setText("Qty Scan");
                                                        String Qty_Kanban = String.valueOf(DataaccesOffline.QTY_SCAN);
                                                        edtQtyKanban.setText(Qty_Kanban);
                                                        String Qty_Part = String.valueOf(DataaccesOffline.QCQTYPART);
                                                        edtQtyPart.setText(Qty_Part);
                                                        EdtQtySampling.setVisibility(View.INVISIBLE);
                                                        txtQtySampling.setVisibility(View.INVISIBLE);

                                                        if (Status_Scan.equals("Part Complete")) {
                                                            startTone("OK");
                                                            tv.setTextColor(Color.BLACK);
                                                            tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                                            showmessage("Part Complete");
                                                            edtScanKanban.setText("");
                                                            edtScanKanban.requestFocus();
                                                            ScanFirst = false;
                                                            edtScanPartNo.setText("");
                                                            edtQtyKanban.setText("");
                                                            edtQtyPart.setText("");
                                                            EdtQtySampling.setText("");
                                                        } else if (Status_Scan.equals("Manifest Complete")) {
                                                            startTone("OK");
                                                                   /* tv.setTextColor(Color.BLACK);
                                                                    tv.setBackgroundColor(Color.rgb(0, 255, 0));*/
                                                            //showmessage("Manifest Complete");
                                                            edtScanKanban.setText("");
                                                            ab = new AlertDialog.Builder(qc_kanban.this);
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
                                                            //edtScanKanban.requestFocus();
                                                                    /*Handler handler = new Handler();
                                                                    handler.postDelayed(new Runnable() {
                                                                        public void run() {
                                                                            finish();
                                                                        }
                                                                    }, 4000);*/
                                                        }
                                                    } else {
                                                        toolbar.setTitleTextColor(Color.WHITE);
                                                        toolbar.setBackgroundColor(color);
                                                        //Normal
                                                        btnQuarantine.setVisibility(View.VISIBLE);
                                                        txtQtyPart.setText("Qty Part");
                                                        txtQtyKanban.setText("Qty Sampling");
                                                        String Qty_Normal_Kanban = String.valueOf(DataaccesOffline.QTY_SCAN_NORMAL);
                                                        String Qty_Sampling = String.valueOf(DataaccesOffline.QTY_SAMPLING);
                                                        edtQtyKanban.setText(Qty_Normal_Kanban + " / " + Qty_Sampling);
                                                        edtQtyPart.setText(String.valueOf(finalQty));
                                                        EdtQtySampling.setVisibility(View.VISIBLE);
                                                        txtQtySampling.setVisibility(View.VISIBLE);

                                                        int QtySampling =DataaccesOffline.QTY_SAMPLING;
                                                        if (finalQty > QtySampling) {
                                                            Hasil = 1;
                                                        } else if (finalQty < QtySampling) {
                                                            Hasil = (double) QtySampling / (double) finalQty;
                                                            Hasil = roundAvoid(Hasil, 0);
                                                        }
                                                        int ScanKanban = DataaccesOffline.QTY_SCAN_KANBAN;
                                                        EdtQtySampling.setText(ScanKanban + " / " + (int) Hasil);
                                                        if (Status_Scan.equals("Part Complete")) {
                                                            startTone("OK");
                                                            tv.setTextColor(Color.BLACK);
                                                            tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                                            showmessage("Part Complete");
                                                            edtScanKanban.setText("");
                                                            edtScanKanban.requestFocus();
                                                            ScanFirst = false;
                                                            edtScanPartNo.setText("");
                                                            edtQtyKanban.setText("");
                                                            edtQtyPart.setText("");
                                                            EdtQtySampling.setText("");
                                                        } else if (Status_Scan.equals("Manifest Complete")) {
                                                            startTone("OK");
                                                                    /*tv.setTextColor(Color.BLACK);
                                                                    tv.setBackgroundColor(Color.rgb(0, 255, 0));*/
                                                            //showmessage("Manifest Complete");
                                                            edtScanKanban.setText("");
                                                            //edtScanKanban.requestFocus();
                                                            ab = new AlertDialog.Builder(qc_kanban.this);
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
                                                            dg.show();
                                                                    /*Handler handler = new Handler();
                                                                    handler.postDelayed(new Runnable() {
                                                                        public void run() {
                                                                            finish();
                                                                        }
                                                                    }, 4000);*/
                                                        }
                                                    }

                                                } catch (Exception ex) {
                                                    startTone("NG");
                                                    Alertdialog = new AlertDialog.Builder(qc_kanban.this);
                                                    Alertdialog.setMessage("Error Save Data OK");
                                                    Alertdialog.setCancelable(false);
                                                    Alertdialog.setPositiveButton("OK", null);
                                                    AlertDialog dg = Alertdialog.create();
                                                    dg.setIcon(R.drawable.dlg_alrt);
                                                    dg.setTitle("Error!");
                                                    dg.show();
                                                    edtScanKanban.setText("");
                                                    edtScanKanban.requestFocus();
                                                }
                                            //Toast.makeText(qc_kanban.this, "You Clicked OK Button", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        });

                                        int finalQty1 = Qty;
                                        btn_NG.setOnClickListener(view -> {
                                            //Toast.makeText(qc_kanban.this, "You Clicked NG Button", Toast.LENGTH_SHORT).show();
                                            final Dialog dialogng = new Dialog(qc_kanban.this);
                                            dialogng.setContentView(R.layout.dialogng);
                                            dialogng.setCancelable(false);
                                            dialogng.show();

                                            Button btn_save, btn_cancel;
                                            EditText edtremark;
                                            btn_save = dialogng.findViewById(R.id.btnsave);
                                            btn_cancel = dialogng.findViewById(R.id.btncancel);
                                            edtremark = dialogng.findViewById(R.id.edtremarkng);
                                            btn_save.setOnClickListener(v12 ->
                                            {
                                                /*ResultSet RsQC = dataaccesManifest.SaveKanbanQC(manifest.getText().toString(),
                                                        SupplierCode, PartNo, finalQty1, Serial, "NG", edtremark.getText().toString(), (int) Hasil);*/
                                                String GetDataSave = dataaccesOffline.SaveKanbanQC(manifest.getText().toString(),
                                                        SupplierCode, PartNo, finalQty, Serial, "NG", edtremark.getText().toString().trim() , (int) Hasil);
                                                try {
                                                    String Status_Scan = GetDataSave;
                                                    if (Status_Quarantine.equals("Ya")) {
                                                        //Quarantine
                                                        btnQuarantine.setVisibility(View.INVISIBLE);
                                                        txtQtyPart.setText("Total Qty Part");
                                                        txtQtyKanban.setText("Qty Scan");
                                                        String Qty_Kanban = String.valueOf(DataaccesOffline.QTY_SCAN);
                                                        edtQtyKanban.setText(Qty_Kanban);
                                                        String Qty_Part = String.valueOf(DataaccesOffline.QCQTYPART);
                                                        edtQtyPart.setText(Qty_Part);
                                                        EdtQtySampling.setVisibility(View.INVISIBLE);
                                                        txtQtySampling.setVisibility(View.INVISIBLE);

                                                        if (Status_Scan.equals("Part Complete")) {
                                                            startTone("OK");
                                                            tv.setTextColor(Color.BLACK);
                                                            tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                                            showmessage("Part Complete");
                                                            edtScanKanban.setText("");
                                                            edtScanKanban.requestFocus();
                                                            ScanFirst = false;
                                                            edtScanPartNo.setText("");
                                                            edtQtyKanban.setText("");
                                                            edtQtyPart.setText("");
                                                            EdtQtySampling.setText("");
                                                        } else if (Status_Scan.equals("Manifest Complete")) {
                                                            startTone("OK");
                                                                   /* tv.setTextColor(Color.BLACK);
                                                                    tv.setBackgroundColor(Color.rgb(0, 255, 0));*/
                                                            //showmessage("Manifest Complete");
                                                            edtScanKanban.setText("");
                                                            ab = new AlertDialog.Builder(qc_kanban.this);
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
                                                            //edtScanKanban.requestFocus();
                                                                    /*Handler handler = new Handler();
                                                                    handler.postDelayed(new Runnable() {
                                                                        public void run() {
                                                                            finish();
                                                                        }
                                                                    }, 4000);*/
                                                        }
                                                    } else {
                                                        //Normal
                                                        btnQuarantine.setVisibility(View.VISIBLE);
                                                        txtQtyPart.setText("Qty Part");
                                                        txtQtyKanban.setText("Qty Sampling");
                                                        String Qty_Normal_Kanban = String.valueOf(DataaccesOffline.QTY_SCAN_NORMAL);
                                                        String Qty_Sampling = String.valueOf(DataaccesOffline.QTY_SAMPLING);
                                                        edtQtyKanban.setText(Qty_Normal_Kanban + " / " + Qty_Sampling);
                                                        edtQtyPart.setText(String.valueOf(finalQty));
                                                        EdtQtySampling.setVisibility(View.VISIBLE);
                                                        txtQtySampling.setVisibility(View.VISIBLE);

                                                        int QtySampling =DataaccesOffline.QTY_SAMPLING;
                                                        if (finalQty > QtySampling) {
                                                            Hasil = 1;
                                                        } else if (finalQty < QtySampling) {
                                                            Hasil = (double) QtySampling / (double) finalQty;
                                                            Hasil = roundAvoid(Hasil, 0);
                                                        }
                                                        int ScanKanban = DataaccesOffline.QTY_SCAN_KANBAN;
                                                        EdtQtySampling.setText(ScanKanban + " / " + (int) Hasil);
                                                        if (Status_Scan.equals("Part Complete")) {
                                                            startTone("OK");
                                                            tv.setTextColor(Color.BLACK);
                                                            tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                                            showmessage("Part Complete");
                                                            edtScanKanban.setText("");
                                                            edtScanKanban.requestFocus();
                                                            ScanFirst = false;
                                                            edtScanPartNo.setText("");
                                                            edtQtyKanban.setText("");
                                                            edtQtyPart.setText("");
                                                            EdtQtySampling.setText("");
                                                        } else if (Status_Scan.equals("Manifest Complete")) {
                                                            startTone("OK");
                                                                    /*tv.setTextColor(Color.BLACK);
                                                                    tv.setBackgroundColor(Color.rgb(0, 255, 0));*/
                                                            //showmessage("Manifest Complete");
                                                            edtScanKanban.setText("");
                                                            //edtScanKanban.requestFocus();
                                                            ab = new AlertDialog.Builder(qc_kanban.this);
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
                                                            dg.show();
                                                                    /*Handler handler = new Handler();
                                                                    handler.postDelayed(new Runnable() {
                                                                        public void run() {
                                                                            finish();
                                                                        }
                                                                    }, 4000);*/
                                                        }
                                                    }

                                                } catch (Exception ex) {
                                                    startTone("NG");
                                                    Alertdialog = new AlertDialog.Builder(qc_kanban.this);
                                                    Alertdialog.setMessage("Error Save Data NG");
                                                    Alertdialog.setCancelable(false);
                                                    Alertdialog.setPositiveButton("OK", null);
                                                    AlertDialog dg = Alertdialog.create();
                                                    dg.setIcon(R.drawable.dlg_alrt);
                                                    dg.setTitle("Error!");
                                                    dg.show();
                                                    edtScanKanban.setText("");
                                                    edtScanKanban.requestFocus();
                                                }
                                                dialogng.dismiss();
                                                dialog.dismiss();
                                            });
                                            btn_cancel.setOnClickListener(v1 -> {
                                                dialogng.dismiss();
                                            });
                                        });

                                        //Show Dialog NG OK

                                        return true;
                                    } else if (STS.equals("Manifest Complete")) {
                                        startTone("OK");
                                                  /*tv.setTextColor(Color.BLACK);
                                                  tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                                  showmessage("Manifest Complete");*/
                                        edtScanKanban.setText("");

                                        ab = new AlertDialog.Builder(qc_kanban.this);
                                        ab.setMessage("Manifest Complete");
                                        ab.setCancelable(false);
                                        ab.setPositiveButton("OK", null);
                                        AlertDialog dg = ab.create();
                                        dg.setIcon(R.drawable.dlg_oke);
                                        dg.setTitle("OK!");
                                        dg.show();
                                        finish();
                                                 /* edtScanKanban.requestFocus();
                                                  Handler handler = new Handler();
                                                  handler.postDelayed(new Runnable() {
                                                      public void run() {
                                                          finish();
                                                      }
                                                  }, 3000);*/
                                        return true;
                                    }
                                } catch (Exception ex) {
                                    startTone("NG");
                                    tv.setTextColor(Color.WHITE);
                                    tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                    showmessage(ex.toString());
                                }
                        } else {
                            // Scan Pertama Untuk manggil SOP
                            //OFFLINE
                            String sts = dataaccesOffline.CheckKanbanQCFirst(manifest.getText().toString(),
                                    SupplierCode, PartNo, Qty, Serial);
                            String[] cek = split("|", sts);
                            String STS = cek[0];
                            String Status_Quarantine = cek[1];
                                try {
                                        if (STS.substring(0, 2).equals("ER")) {
                                            startTone("NG");
                                            tv.setTextColor(Color.WHITE);
                                            tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                            showmessage(STS.replace("ER - ", ""));
                                            edtScanKanban.setText("");
                                            edtScanKanban.requestFocus();
                                            PartNoTemp = "";
                                            return true;
                                        } else if (STS.equals("Part Complete")) {
                                            edtQtyKanban.setText("");
                                            edtQtyPart.setText("");
                                            edtScanPartNo.setText("");
                                            startTone("NG");
                                            tv.setTextColor(Color.BLACK);
                                            tv.setBackgroundColor(Color.rgb(0, 255, 0));
                                            showmessage("Part Complete");
                                            edtScanKanban.setText("");
                                            edtScanKanban.requestFocus();
                                            PartNoTemp = "";
                                            return true;
                                        } else if (STS.equals("LANJUT")) {
                                            ScanFirst = true;
                                            QtyTemp = Qty;
                                            // showmessage("Please Scan Again");
                                            if (Status_Quarantine.equals("Ya")) {
                                                //Quarantine
                                                toolbar.setTitleTextColor(Color.BLACK);
                                                toolbar.setBackgroundColor(Color.YELLOW);
                                                btnQuarantine.setVisibility(View.INVISIBLE);
                                                txtQtyPart.setText("Total Qty Part");
                                                txtQtyKanban.setText("Qty Scan");
                                                edtQtyKanban.setText( String.valueOf(DataaccesOffline.QTY_SCAN));
                                                edtQtyPart.setText(String.valueOf(DataaccesOffline.QCQTYPART));
                                                EdtQtySampling.setVisibility(View.INVISIBLE);
                                                txtQtySampling.setVisibility(View.INVISIBLE);
                                            } else {
                                                //Normal
                                                toolbar.setTitleTextColor(Color.WHITE);
                                                toolbar.setBackgroundColor(color);
                                                btnQuarantine.setVisibility(View.VISIBLE);
                                                txtQtyPart.setText("Qty Part");
                                                txtQtyKanban.setText("Qty Sampling");
                                                edtQtyKanban.setText(String.valueOf(DataaccesOffline.QTY_SCAN_NORMAL) + " / " + String.valueOf(DataaccesOffline.QTY_SAMPLING));
                                                edtQtyPart.setText(String.valueOf(Qty));
                                                EdtQtySampling.setVisibility(View.VISIBLE);
                                                txtQtySampling.setVisibility(View.VISIBLE);

                                                int QtySampling = DataaccesOffline.QTY_SAMPLING;
                                                if (Qty > QtySampling) {
                                                    Hasil = 1;
                                                } else if (Qty < QtySampling) {
                                                    Hasil = (double) QtySampling / (double) Qty;
                                                    Hasil = roundAvoid(Hasil, 0);
                                                }
                                                int ScanKanban = DataaccesOffline.QTY_SCAN_KANBAN;

                                                EdtQtySampling.setText(ScanKanban + " / " + (int) Hasil);

                                            }
                                            txtQtyKanban.setVisibility(View.VISIBLE);
                                            txtQtyPart.setVisibility(View.VISIBLE);
                                            edtQtyKanban.setVisibility(View.VISIBLE);
                                            edtQtyPart.setVisibility(View.VISIBLE);
                                            edtScanKanban.setText("");
                                            edtScanKanban.requestFocus();
                                            startTone("OK");
                                            return true;
                                        } else if (STS.equals("Manifest Complete")) {
                                            startTone("OK");
                                        /*  tv.setTextColor(Color.BLACK);
                                          tv.setBackgroundColor(Color.rgb(0,255,0));
                                          showmessage("Manifest Complete");*/
                                            edtScanKanban.setText("");
                                            PartNoTemp = "";
                                            // edtScanKanban.requestFocus();
                                            ab = new AlertDialog.Builder(qc_kanban.this);
                                            ab.setMessage("Manifest Complete");
                                            ab.setCancelable(false);
                                            ab.setPositiveButton("OK", null);
                                            AlertDialog dg = ab.create();
                                            dg.setIcon(R.drawable.dlg_oke);
                                            dg.setTitle("OK!");
                                            dg.show();
                                            finish();
                                          /*Handler handler = new Handler();
                                          handler.postDelayed(new Runnable() {
                                              public void run() {
                                                  finish();
                                              }
                                          }, 3000);*/
                                            return true;
                                        }

                                } catch (Exception throwables) {
                                    throwables.printStackTrace();
                                }

                        }

                    }
                }
            }
            return false;
        });
    }

    public void BackQCKanban(View v){
        finish();
    }

    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
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
    public void onBackPressed() {
            return;
    }

    public static String[] split(String separators, String list) {
        StringTokenizer tokens = new StringTokenizer( list, separators );
        String[] result = new String[tokens.countTokens()];
        int i = 0;
        while ( tokens.hasMoreTokens() ) {
            result[i++] = tokens.nextToken();
        }
        return result;

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
}