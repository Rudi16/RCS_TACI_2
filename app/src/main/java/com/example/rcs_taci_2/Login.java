package com.example.rcs_taci_2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.rcs_taci_2.Dataacces.DataaccesOffline;
import com.example.rcs_taci_2.Dataacces.DataaccesUser;
import com.example.rcs_taci_2.Dataacces.DataaccesUserOffline;
import com.example.rcs_taci_2.Entity.E_user;
import com.example.rcs_taci_2.Helper.ConnectionHelper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.util.StringTokenizer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.example.rcs_taci_2.R;
import com.microsoft.signalr.HubConnection;

public class Login extends AppCompatActivity {

    EditText edtEmailAddress, edtPassword;
    Button btnLogin;
    ProgressBar progressBar;
    TextView lbllogin, txtSettings;
    E_user e_users = new E_user();
    DataaccesUser dataaccesuser;
    DataaccesUserOffline dataaccesUserOffline;
    private static final String FILE_NAME = "Config.txt";

    @SuppressLint({"WrongViewCast", "MissingPermission", "HardwareIds"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        // getSupportActionBar().hide(); // hide the title bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.login);
        dataaccesuser = new DataaccesUser();
        dataaccesUserOffline = new DataaccesUserOffline(this);
        dataaccesUserOffline.CreateTable();
        edtEmailAddress = findViewById(R.id.edtUserID);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtSettings = findViewById(R.id.txtsettings);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        E_user.Version = "1.0.2";
        Load();
        txtSettings.setOnClickListener(v -> {
            try {
                Intent i = new Intent(Login.this,settings.class);
                ConnectionHelper.setting ="Login";
                startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
    final class  HubConnectionTask extends AsyncTask<HubConnection, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(HubConnection... hubConnections) {
            try {
                HubConnection hubConnection = hubConnections[0];
                hubConnection.start().blockingAwait();
            }
            catch(Exception exc)
            {
                String msg= exc.getMessage();
                String s=msg;
            }
            return null;
        }
    }

    private class DoLoginForUser extends AsyncTask<String, Void, String> {
        String emailId, password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            emailId = edtEmailAddress.getText().toString();
            password = edtPassword.getText().toString();


        }
        @Override
        protected String doInBackground(String... params) {
            try {
                if(ConnectionHelper.connection.equals("Online")) {
                    ResultSet Rs;
                    Rs = dataaccesuser.GetDataLogin(emailId);
                    if (Rs == null) {
                        return "check internet connection, because it cannot connect to the database";
                    } else if (Rs.next()) {
                        String passcode = decrypt(Rs.getString("password"));
                        E_user.UserId = emailId;
                        E_user.Name = Rs.getString("UserName");
                        E_user.setRole(Rs.getString("Authority"));
                        Rs.close();
                        if (passcode != null && !passcode.trim().equals("") && passcode.equals(password)) {
                            return "success";
                        } else {
                            return "Wrong Password!";
                        }
                    } else {
                        return "User does not found.";
                    }
                }else{
                    try {
                        String datax = null;
                        Cursor cursor = dataaccesUserOffline.GetDataLogin(emailId);
                        if (cursor.getCount() > 0) {
                            for (int cc = 0; cc < cursor.getCount(); cc++) {
                                cursor.moveToPosition(cc);
                                String passwordx = cursor.getString(2);
                                String passcode = decrypt(passwordx);
                                E_user.UserId = emailId;
                                E_user.Name = cursor.getString(1);
                                String Role = cursor.getString(3);
                                E_user.setRole(Role);
                                if (passcode != null && !passcode.trim().equals("") && passcode.equals(password)) {
                                    datax = "success";
                                } else {
                                    datax = "Wrong Password!";
                                }
                            }
                        }else{
                            datax   = "Data User not found.";
                        }
                        return  datax;
                    } catch (Exception e) {
                        return "Error : " + e.getMessage().toString();
                    }

                }
            } catch (Exception e) {
                return "Error:" + e.getMessage().toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            //Toast.makeText(signup.this, result, Toast.LENGTH_SHORT).show();
            // Toast.makeText(Login.this ,result , Toast.LENGTH_LONG).show();
            // ShowSnackBar(result);
            progressBar.setVisibility(View.GONE);

            if (result.equals("success")) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("userdetails",0);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("email",edtEmailAddress.getText().toString());

                editor.commit();
                Intent i = new Intent(Login.this, MainActivity.class);
                startActivityForResult(i,1);
                finish();
            } else {
                Toast.makeText(Login.this ,result , Toast.LENGTH_LONG).show();
                //ShowSnackBar(result);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            finish();
            System.exit(0);
        }
    }

    public void DoLogin(View v) {
        DoLoginForUser login = new DoLoginForUser();
        login.execute("");

    }

    String SecretKey = "abcdefg_abcdefg_abcdefg_abcdefg_";
    String IV = "abcdefg_abcdefg_";
   /* public String encrypt(String message) throws NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException,
            UnsupportedEncodingException, InvalidAlgorithmParameterException {

        byte[] srcBuff = message.getBytes("UTF8");
        //here using substring because AES takes only 16 or 24 or 32 byte of key
        SecretKeySpec skeySpec = new
                SecretKeySpec(SecretKey.substring(0,32).getBytes(), "AES");
        IvParameterSpec ivSpec = new
                IvParameterSpec(IV.substring(0,16).getBytes());
        Cipher ecipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        ecipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
        byte[] dstBuff = ecipher.doFinal(srcBuff);
        String base64 = Base64.encodeToString(dstBuff, Base64.DEFAULT);
        return base64;
    }*/

    public String decrypt(String encrypted) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {

        SecretKeySpec skeySpec = new
                SecretKeySpec(SecretKey.substring(0,32).getBytes(), "AES");
        IvParameterSpec ivSpec = new
                IvParameterSpec(IV.substring(0,16).getBytes());
        Cipher ecipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        ecipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
        byte[] raw = Base64.decode(encrypted, Base64.DEFAULT);
        byte[] originalBytes = ecipher.doFinal(raw);
        String original = new String(originalBytes, "UTF8");
        return original;
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
            ConnectionHelper.server = st.nextToken();
            ConnectionHelper.user = st.nextToken();
            ConnectionHelper.passwd = st.nextToken();
            E_user.DeviceID = st.nextToken();
            ConnectionHelper.connection = st.nextToken();
            String x = "";
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


    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            finish();
            System.exit(0);
        }
        else
        {
            Toast.makeText(getBaseContext(), "Tap back button again to exit", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }

}