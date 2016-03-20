package com.kogroup.anglemeter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/*
 * This class is reserved for saving data, such as
 * which unit is selected or is help on/off and 
 * which measurements were saved by user
 */


public class SavedData {
	
	
	public static Preferences prefs;
	
	
	public static void setPrefs() {
		prefs=Gdx.app.getPreferences("AngleMeter");
		
		
		if (!prefs.contains("noHelp")) {
			prefs.putBoolean("noHelp", false);

		}
		
		if (!prefs.contains("unitCm")) {
			prefs.putBoolean("unitCm", false);
		}
		
		if (!prefs.contains("unitRad")) {
			prefs.putBoolean("unitRad", false);
		}
		
		if (!prefs.contains("results")) {
			prefs.putString("results", "");
		}
		
		if (!prefs.contains("results")) {
			prefs.putString("results", "");
		}
		
		if (!prefs.contains("g")) {
			prefs.putFloat("g", 9.81f);
		}
	}
	
	
	public static boolean getNoHelp() {
		return prefs.getBoolean("noHelp");
	}
	
	public static void setHelp(boolean val) {
		prefs.putBoolean("noHelp", val);
		prefs.flush();
	}
	
	public static boolean getUnitCm() {
		return prefs.getBoolean("unitCm");
	}
	
	public static void setUnitCm(boolean val) {
		prefs.putBoolean("unitCm", val);
		prefs.flush();
	}
	
	public static boolean getUnitRad() {
		return prefs.getBoolean("unitRad");
	}
	
	public static void setUnitRad(boolean val) {
		prefs.putBoolean("unitRad", val);
		prefs.flush();
	}
	
	public static String getResults() {
		return prefs.getString("results");
	}
	
	public static void setResults(String val) {
		prefs.putString("results", val);
		prefs.flush();
	}
	
	public static float getG() {
		return prefs.getFloat("g");
	}
	
	public static void setG(float val) {
		prefs.putFloat("g", val);
		prefs.flush();
	}

}
