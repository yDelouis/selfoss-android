package fr.ydelouis.selfoss.activity;

import android.app.Activity;
import android.os.Bundle;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.api.SelfossConfig_;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

	@Pref protected SelfossConfig_ selfossConfig;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!isConfigFilled())
			SelfossConfigActivity_.intent(this).start();
	}

	private boolean isConfigFilled() {
		return !(selfossConfig.url().getOr("").isEmpty());
	}
}
