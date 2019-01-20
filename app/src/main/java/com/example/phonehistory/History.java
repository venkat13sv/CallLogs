package com.example.phonehistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;

import static android.content.ContentValues.TAG;

@SuppressLint("SimpleDateFormat") 
public class History extends ContentObserver {

	Context c;
	String simID;
	
	public History(Handler handler, Context cc) {
		// TODO Auto-generated constructor stub
		super(handler);
		c=cc;
	}
	
	@Override
    public boolean deliverSelfNotifications() {
        return true;
    }

	@Override
	public void onChange(boolean selfChange) {
		// TODO Auto-generated method stub
		super.onChange(selfChange);
		SharedPreferences sp=c.getSharedPreferences("ZnSoftech", Activity.MODE_PRIVATE);
		String number=sp.getString("number", null);
		if(number!=null)
		{
			//simID=sp.getInt("simID",0);
			getCalldetailsNow();
			sp.edit().putString("number", null).commit();
		}
	}
	
	private void getCalldetailsNow() {
		// TODO Auto-generated method stub
		
		 Cursor managedCursor=c.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC");
		
		int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER ); 
        int duration1 = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
        int type1=managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date1=managedCursor.getColumnIndex(CallLog.Calls.DATE);
		String id;

		if( managedCursor.moveToFirst() == true ) {
            String phNumber = managedCursor.getString(number);
            String callDuration = managedCursor.getString(duration1);
			id = managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID));
            String type=managedCursor.getString(type1);
            String date=managedCursor.getString(date1);
			int columnId=getSimIdColumn(managedCursor);
			if(columnId==-1) {
               simID="No dual sim";

            }
            else
                simID = managedCursor.getString(columnId);
            String dir = null;
		    int dircode = Integer.parseInt(type);
		    switch (dircode)
		    { 
		    case CallLog.Calls.OUTGOING_TYPE:
		    	dir = "OUTGOING";
		    	break;
		    case CallLog.Calls.INCOMING_TYPE:
		    	dir = "INCOMING";
		    	break;
		    case CallLog.Calls.MISSED_TYPE:
		    	dir = "MISSED";
		    	break;
		    default: 
		    	dir = "MISSED";
		    	break;
		    }
		    
		    SimpleDateFormat sdf_date = new SimpleDateFormat("dd/MM/yyyy");
		    SimpleDateFormat sdf_time = new SimpleDateFormat("h:mm a");
		   // SimpleDateFormat sdf_dur = new SimpleDateFormat("KK:mm:ss");
		    
		    String dateString = sdf_date.format(new Date(Long.parseLong(date)));
		    String timeString = sdf_time.format(new Date(Long.parseLong(date)));
		  //  String duration_new=sdf_dur.format(new Date(Long.parseLong(callDuration)));

           DBHelper db=new DBHelper(c, "ZnSoftech.db", null, 2);

           db.insertdata(phNumber, dateString, timeString, callDuration, dir, simID);
           
        }
        
        managedCursor.close();
	}
	public static int getSimIdColumn(final Cursor c) {

		for (String s : new String[] { "sim_id", "simid", "sub_id" }) {
			int id = c.getColumnIndex(s);
			if (id >=0) {
				Log.d(TAG, "sim_id column found: " + s);
				return id;
			}
		}
		Log.d(TAG, "no sim_id column found");
		return -1;
	}

}
