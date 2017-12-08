package com.citytelecoin.inmatetablet.KioskMode;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.citytelecoin.inmatetablet.Login.Login;

//Boots app when device is turned on

public class BootReceiver extends BroadcastReceiver {

	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent myIntent = new Intent(context, Login.class);
		myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(myIntent);
	}
}
