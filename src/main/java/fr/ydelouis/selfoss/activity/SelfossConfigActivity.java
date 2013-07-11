package fr.ydelouis.selfoss.activity;

import android.app.Activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.UiThread;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.fragment.SelfossConfigFragment;

@EActivity(R.layout.activity_selfossconfig)
public class SelfossConfigActivity extends Activity implements SelfossConfigFragment.ValidationListener {

	@FragmentById protected SelfossConfigFragment configFragment;

	@AfterViews
	protected void init() {
		configFragment.setValidatIonListener(this);
	}

	@Override
	@UiThread(delay = 1000)
	public void onValidationSucceed() {
		finish();
	}
}
