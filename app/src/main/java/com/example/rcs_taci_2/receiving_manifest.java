package com.example.rcs_taci_2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.rcs_taci_2.Dataacces.DataaccesManifest;
import com.example.rcs_taci_2.Dataacces.DataaccesOffline;
import com.example.rcs_taci_2.Dataacces.DataaccesUser;
import com.example.rcs_taci_2.Entity.E_Manifest;
import com.example.rcs_taci_2.Entity.E_user;
import com.example.rcs_taci_2.Helper.ConnectionHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class receiving_manifest extends AppCompatActivity {
    EditText manifest;
    E_Manifest e_manifest;
    DataaccesManifest dataaccesManifest;
    DataaccesOffline dataaccesOffline;
    TextView tv ;
    private AlertDialog.Builder Alertdialog;
    private Vibrator _vibrator;
    private Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        setContentView(R.layout.receiving_manifest);
        manifest = findViewById(R.id.edtscanmanifest);
        manifest.requestFocus();
        e_manifest = new E_Manifest();
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(color);
        if (E_user.getRole().equals("Operator Receiving"))
            toolbar.setTitle("RECEIVING");
        else if(E_user.getRole().equals("Operator QC"))
            toolbar.setTitle("QC CHECK");
        setSupportActionBar(toolbar);
        tv = findViewById(R.id.msgreturn);
      /*  if (E_user.getRole().equals("Operator Receiving"))
            setTitle("RECEIVING");
        else if(E_user.getRole().equals("Operator QC"))
            setTitle("QC CHECK");
*/


        dataaccesManifest = new DataaccesManifest();
        dataaccesOffline = new DataaccesOffline(this);
        dataaccesOffline.CreateTable();
        manifest.setOnKeyListener((v, keyCode, event)  ->{
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == 1) {
                String barcode = manifest.getText().toString().trim();
                boolean checkisiData = false;
                //Check Kosong atau tidak
                if (barcode.equals("") || barcode == null) {
                    startTone("NG");
                    tv.setTextColor(Color.WHITE);
                    tv.setBackgroundColor(Color.rgb(255,0,0));
                    showmessage("Please Scan QRCode");
                    manifest.setText("");
                    manifest.requestFocus();
                    return true;
                }else{
                    String respond = manifest.getText().toString();
                  if (isJSONValid(respond)) {
                      try {
                          JSONObject jo = new JSONObject(respond);
                          int checklength = jo.length();
                          if (checklength== 5){
                              boolean checkmanifest = false;
                              e_manifest.setSupplierCode( jo.getString("supplierCode"));
                              e_manifest.setSupplierName(jo.getString("supplierName"));
                              e_manifest.setManifestNo(jo.getString("transactionCd"));
                              e_manifest.setDnNo(jo.getString("dnNo"));

                              JSONArray Jarray = jo.getJSONArray("data");
                              int x = Jarray.length();
                              for (int i = 0; i < x; i++) {
                                  JSONArray innerArray = Jarray.optJSONArray(i);
                                  int CountArray = innerArray.length();
                                  if (CountArray % 3 == 0){
                                      int partno = 1;
                                      int qty = 2;
                                      CountArray = CountArray / 3;
                                      if (E_user.getRole().equals("Operator Receiving")) {
                                          for (int xx = 0 ; xx <CountArray;xx++){
                                              if (xx == 0){
                                                  e_manifest.setPartNo(innerArray.getString(partno));
                                                  e_manifest.setQtyManifest (innerArray.getInt(qty));
                                              }else{
                                                  partno= partno+3;
                                                  qty = qty+3;
                                                  e_manifest.setPartNo(innerArray.getString(partno));
                                                  e_manifest.setQtyManifest (innerArray.getInt(qty));
                                              }
                                              if (ConnectionHelper.connection.equals("Online")) {
                                                  String status_checkmanifest = dataaccesManifest.CheckManifest(e_manifest);
                                                  if (status_checkmanifest.equals("Not Yet Scanned") || status_checkmanifest.equals("Partial Complete")) {
                                                      dataaccesOffline.InsertManifestPerPart(e_manifest);
                                                      checkmanifest = true;
                                                  } else if (status_checkmanifest.equals("Complete")) {
                                                      startTone("NG");
                                                      tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                                      showmessage("Status Manifest Already Complete!");
                                                      manifest.setText("");
                                                      manifest.requestFocus();
                                                      return true;
                                                  } else {
                                                      startTone("NG");
                                                      tv.setTextColor(Color.WHITE);
                                                      tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                                      showmessage(status_checkmanifest);
                                                      manifest.setText("");
                                                      manifest.requestFocus();
                                                      return true;
                                                  }
                                              }else{
                                                  String Check_ManifestOffline = dataaccesOffline.CheckManifestNo(e_manifest);
                                                  if (Check_ManifestOffline.equals("INSERT"))
                                                  {
                                                      dataaccesOffline.InsertManifestPerPart(e_manifest);
                                                      checkmanifest = true;
                                                  }else if (Check_ManifestOffline.equals("Complete")){
                                                      startTone("NG");
                                                      tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                                      showmessage("Status Manifest Already Complete!");
                                                      manifest.setText("");
                                                      manifest.requestFocus();
                                                      return true;
                                                  }else if (Check_ManifestOffline.equals("DATA SUDAH ADA")) {
                                                      checkmanifest = true;
                                                  }else if (Check_ManifestOffline.equals("Not Yet Scanned")){
                                                      checkmanifest = true;
                                                  }else if (Check_ManifestOffline.equals("Partial Finish")){
                                                      checkmanifest = true;
                                                  }else if (Check_ManifestOffline.equals("Process")){
                                                      checkmanifest = true;
                                                  }else{
                                                      startTone("NG");
                                                      tv.setTextColor(Color.WHITE);

                                                      tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                                      showmessage(Check_ManifestOffline.replace("ER - ",""));
                                                      manifest.setText("");
                                                      manifest.requestFocus();
                                                      return true;
                                                  }
                                              }
                                          }

                                      }
                                      else if(E_user.getRole().equals("Operator QC")){
                                          if (ConnectionHelper.connection.equals("Online")) {
                                              String status_checkmanifest1 = dataaccesManifest.CheckManifestQc1(e_manifest);
                                              if (status_checkmanifest1.equals("Lanjut")) {
                                                  String status_checkmanifest = dataaccesManifest.CheckManifestQc(e_manifest);
                                                  if (status_checkmanifest.equals("Not Yet Scanned") || status_checkmanifest.equals("Partial Complete") || status_checkmanifest.equals("Process")) {
                                                      checkmanifest = true;
                                                  }
                                              } else {
                                                  startTone("NG");
                                                  tv.setTextColor(Color.WHITE);
                                                  tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                                  showmessage(status_checkmanifest1.replace("ER - ", ""));
                                                  manifest.setText("");
                                                  manifest.requestFocus();
                                                  return true;
                                              }
                                          }else{
                                              String status_checkmanifest1 = dataaccesOffline.CheckManifestNoQC1(e_manifest);
                                              if (status_checkmanifest1.equals("Lanjut")) {
                                                 // String status_checkmanifest = dataaccesOffline.CheckManifestQc(e_manifest);
                                                  //if (status_checkmanifest.equals("Not Yet Scanned") || status_checkmanifest.equals("Partial Complete") || status_checkmanifest.equals("Process")) {
                                                      checkmanifest = true;
                                                 // }
                                              } else {
                                                  startTone("NG");
                                                  tv.setTextColor(Color.WHITE);
                                                  tv.setBackgroundColor(Color.rgb(255, 0, 0));
                                                  showmessage(status_checkmanifest1.replace("ER - ", ""));
                                                  manifest.setText("");
                                                  manifest.requestFocus();
                                                  return true;
                                              }
                                          }
                                      }
                                  }
                              }
                              if (E_user.getRole().equals("Operator Receiving")) {
                                  if (checkmanifest == true) {
                                      if (ConnectionHelper.connection.equals("Online")) {
                                          dataaccesOffline.DeleteOnline(e_manifest.getManifestNo());
                                          ResultSet Rs = dataaccesManifest.GetManifestOnline(e_manifest.getManifestNo());
                                          if (Rs != null) {
                                              try {
                                                  while (Rs.next()) {
                                                      int ID_Trans=Rs.getInt("ID_Trans");
                                                      String ManifestNo=Rs.getString("ManifestNo");
                                                      String DN_No=Rs.getString("DN_No");
                                                      String Suplier_Code=Rs.getString("Suplier_Code");
                                                      String Suplier_Name=Rs.getString("Suplier_Name");
                                                      String Part_No=Rs.getString("Part_No").trim();
                                                      int Qty_Manifest=Rs.getInt("Qty_Manifest");
                                                      int Qty_Part=Rs.getInt("Qty_Part");
                                                      int Qty_Scan_Awal=Rs.getInt("Qty_Scan_Awal");
                                                      String NPK_ID=Rs.getString("NPK_ID");
                                                      String Date_Scan=Rs.getString("Date_Scan");
                                                      String Status_Receiving=Rs.getString("Status_Receiving");
                                                      String Date_Receiving=Rs.getString("Date_Receiving");
                                                      String Date_Complete_Receiving=Rs.getString("Date_Complete_Receiving");
                                                      String Status_Manifest=Rs.getString("Status_Manifest");
                                                      String DeviceID=Rs.getString("DeviceID");

                                                      String checkInsert = dataaccesOffline.InsertManifestPerPartOnline(ID_Trans,ManifestNo,
                                                              DN_No,Suplier_Code,Suplier_Name,Part_No,Qty_Manifest,Qty_Part,Qty_Scan_Awal,
                                                              NPK_ID,Date_Scan,Status_Receiving,Date_Receiving,Date_Complete_Receiving,Status_Manifest,DeviceID);
                                                      if (checkInsert.equals("OK")){
                                                      }else{
                                                          Toast.makeText(this ,checkInsert , Toast.LENGTH_LONG).show();
                                                      }
                                                  }
                                              } catch (SQLException throwables) {
                                                  throwables.printStackTrace();
                                              }
                                          }

                                          ResultSet History = dataaccesManifest.GetHistoryOnline(e_manifest.getManifestNo());
                                          if (History != null) {
                                              try {
                                                  while (History.next()) {
                                                      int ID_History = History.getInt("ID_History");
                                                      int ID_Trans = History.getInt("ID_Trans");
                                                      String ManifestNo = History.getString("ManifestNo");
                                                      String PartNo = History.getString("PartNo").trim();
                                                      int Qty_Scan = History.getInt("Qty_Scan");
                                                      int Qty_Scan_QC = History.getInt("Qty_Scan_QC");
                                                      String Serial = History.getString("Serial");
                                                      String Serial_QC = History.getString("Serial_QC");
                                                      String Date_Scan = History.getString("Date_Scan");
                                                      String Date_Scan_QC = History.getString("Date_Scan_QC");
                                                      String NPK_ID = History.getString("NPK_ID");
                                                      String NPK_ID_QC = History.getString("NPK_ID_QC");
                                                      String Status_Part = History.getString("Status_Part");
                                                      String Remark = History.getString("Remark");
                                                      String Status_Process = History.getString("Status_Process");
                                                      String Version = History.getString("Version");
                                                      String DeviceID = History.getString("DeviceID");
                                                      String Status_Scan = History.getString("Status_Scan");

                                                     String CheckHistory = dataaccesOffline.InsertHistoryOnline(ID_History,ID_Trans,ManifestNo,PartNo,Qty_Scan,
                                                             Qty_Scan_QC,Serial,Serial_QC,Date_Scan,Date_Scan_QC,NPK_ID,NPK_ID_QC,Status_Part,Remark,Status_Process,Version,DeviceID,Status_Scan);
                                                      if (CheckHistory.equals("OK")){
                                                      }else{
                                                          Toast.makeText(this ,CheckHistory , Toast.LENGTH_LONG).show();
                                                      }
                                                  }
                                              } catch (SQLException throwables) {
                                                  throwables.printStackTrace();
                                              }
                                          }

                                      }
                                      startTone("OK");
                                      Intent intent = new Intent(this, receiving_kanban.class);
                                      startActivityForResult(intent, 1);
                                      manifest.setText("");
                                      manifest.requestFocus();
                                  }
                              }else if(E_user.getRole().equals("Operator QC")){
                                  if (checkmanifest == true) {
                                      Intent intent = new Intent(this, qc_kanban.class);
                                      startActivityForResult(intent, 1);

                                      startTone("OK");
                                      manifest.setText("");
                                      manifest.requestFocus();
                                  }
                              }
                          }else{
                              startTone("NG");
                              tv.setTextColor(Color.WHITE);
                              tv.setBackgroundColor(Color.rgb(255,0,0));
                              showmessage("Please scan the correct QRCode.");
                              manifest.setText("");
                              manifest.requestFocus();
                              return true;
                          }

                      } catch (JSONException e) {
                          startTone("NG");
                          tv.setTextColor(Color.WHITE);

                          tv.setBackgroundColor(Color.rgb(255,0,0));
                          showmessage("Please scan the correct QRCode.");
                          manifest.setText("");
                          manifest.requestFocus();
                          return true;
                      }
                      return true;
                  }else{
                      startTone("NG");
                      tv.setTextColor(Color.WHITE);

                      tv.setBackgroundColor(Color.rgb(255,0,0));
                      showmessage("Please scan the correct QRCode.");
                      manifest.setText("");
                      manifest.requestFocus();
                      return true;
                  }
                }
            }
            return false;
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
    public void CancelScanManifest(View view){
        finish();
    }
    private boolean isJSONValid(String respond) {
        try {
            new JSONObject(respond);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(respond);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
    private void showmessage(String isimsg){
        tv.setText(isimsg);
        tv.setVisibility(View.VISIBLE);
        tv.postDelayed(() -> {
            tv.setVisibility(View.INVISIBLE);
        }, 3000);
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
        return true;
    }
}