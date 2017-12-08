package com.citytelecoin.inmatetablet.KioskMode;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


class PrefUtils {

	private static final String PREF_KIOSK_MODE = "pref_kiosk_mode";


	static boolean isKioskModeActive(final Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getBoolean(PREF_KIOSK_MODE, false);
	}
}
