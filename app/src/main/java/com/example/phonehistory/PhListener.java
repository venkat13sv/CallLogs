package com.example.phonehistory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

import static android.content.ContentValues.TAG;

@SuppressLint("SimpleDateFormat") public class PhListener extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context c, Intent i) {
		// TODO Auto-generated method stub
		Bundle bundle=i.getExtras();
		
		if(bundle==null)
			return;
		int whichSIM=0;
		if (i.getExtras().containsKey("subscription")) {

			whichSIM = i.getExtras().getInt("subscription");
			Log.v("Message","ok SIMMmmmmmmmmmmmmmmmmmm found "+ whichSIM);

		}
		else
		{
			Log.v("Message 2","No simmmmmmmmmmmmmmmmmmmmmm found error");
		}
		
		SharedPreferences sp=c.getSharedPreferences("ZnSoftech", Activity.MODE_PRIVATE);
		sp.edit().putInt("simID",whichSIM).commit();
		String s=bundle.getString(TelephonyManager.EXTRA_STATE);


		if(i.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))
		{
			String number=i.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			sp.edit().putString("number", number).commit();
			sp.edit().putString("state", s).commit();
		}
		
		else if(s.equals(TelephonyManager.EXTRA_STATE_RINGING))
		{
			String number=bundle.getString("incoming_number");
			sp.edit().putString("number", number).commit();
			sp.edit().putString("state", s).commit();
		}
		
		else if(s.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
		{
			sp.edit().putString("state", s).commit();
		}
		
		else if(s.equals(TelephonyManager.EXTRA_STATE_IDLE))
		{
			String state=sp.getString("state", null);
			if(!state.equals(TelephonyManager.EXTRA_STATE_IDLE))
			{
				sp.edit().putString("state", null).commit();
				History h=new History(new Handler(),c);
				c.getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, h);
			}
			sp.edit().putString("state", s).commit();
		}
	
	}


}
