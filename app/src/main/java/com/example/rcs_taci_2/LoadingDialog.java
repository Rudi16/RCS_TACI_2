package com.example.rcs_taci_2;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.rcs_taci_2.Entity.E_user;
import com.example.rcs_taci_2.R;

import org.w3c.dom.Text;

public class LoadingDialog {
   private Activity activity;
   private AlertDialog alertDialog;
    private TextView txtMessage;
    LoadingDialog(Activity myActivity){
        activity = myActivity;

    }
    private class ViewHolder {
        TextView textloading;
    }
    void StartLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        View content =  inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(content);
        TextView textView = (TextView) content.findViewById(R.id.textloading);
        textView.setText(E_user.textLoading);
        builder.setView(content);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        alertDialog.show();
    }
    void dismissDialog(){
        alertDialog.dismiss();
    }
}
