package com.example.phonehistory;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import static android.os.Build.VERSION_CODES.M;


public class MainActivity extends Activity {

    DBHelper db;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);

        String imeiSIM1 = telephonyInfo.getImsiSIM1();
        String imeiSIM2 = telephonyInfo.getImsiSIM2();

        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();

        boolean isDualSIM = telephonyInfo.isDualSIM();
        Toast toast = Toast.makeText(getApplicationContext(),   " IME1 : " + imeiSIM1 + "\n" +
                " IME2 : " + imeiSIM2 + "\n" +
                " IS DUAL SIM : " + isDualSIM + "\n" +
                " IS SIM1 READY : " + isSIM1Ready + "\n" +
                " IS SIM2 READY : " + isSIM2Ready + "\n", Toast.LENGTH_SHORT); toast.show();
        Log.v("Msg",  " IME1 : " + imeiSIM1 + "\n" +
                " IME2 : " + imeiSIM2 + "\n" +
                " IS DUAL SIM : " + isDualSIM + "\n" +
                " IS SIM1 READY : " + isSIM1Ready + "\n" +
                " IS SIM2 READY : " + isSIM2Ready + "\n");
    
        Log.v("My Message 0","My activity 0");
        tv=(TextView)findViewById(R.id.text);
        tv.setText("");
        if (Build.VERSION.SDK_INT >=23) {

            if (checkSelfPermission(Manifest.permission.READ_CALL_LOG)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, 1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant

                return;
            }
        }

        db=new DBHelper(this, "ZnSoftech.db", null, 2);

        Cursor c=db.getData();
        try {
            Log.v("My Message ","My activity 1");
            if (c.getCount() > 0) {
                Log.v("My Message ","My activity 2");
                c.moveToLast();
                do {
                    String number = c.getString(0);
                    String date = c.getString(1);
                    String time = c.getString(2);
                    String duration = c.getString(3);
                    String type = c.getString(4);
                    String simID = c.getString(5);

                    tv.append("Number:" + number + "\nDate:" + date + "\nTime:" + time + "\nDuration:" + duration + "\nCall Type:" + type + "\nSIM slot:" + simID + "\n\n");
                } while (c.moveToPrevious());
            } else {
                tv.setText("No Incoming and Outgoing call history exists!!!");
            }

        }
        finally {
            c.close();
            db.close();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
       /* if (id == R.id.action_settings) {
            db.deleteTable();
            tv.setText("No Incoming and Outgoing call history exists!!!");
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.
                    return;

                } else {

                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Please allow the required permission",
                            Toast.LENGTH_SHORT);

                    toast.show();
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }
}

