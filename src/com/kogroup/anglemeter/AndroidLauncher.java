package com.kogroup.anglemeter;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import android.os.Bundle;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
	
		config.useGyroscope=true;
		config.useCompass=true;
		
		config.useAccelerometer=true;
		

		initialize(new AngleMeter(), config);
	}
}
