package fr.ydelouis.selfoss.activity;

import android.app.Activity;
import android.os.Bundle;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.UiThread;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.fragment.SelfossConfigFragment;
import fr.ydelouis.selfoss.service.Synchronizer_;

@EActivity(R.layout.activity_selfossconfig)
public class SelfossConfigActivity extends Activity implements SelfossConfigFragment.ValidationListener {

	private static final long TIME_TO_CLOSE = 2 * 1000;

	@FragmentById protected SelfossConfigFragment configFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@AfterViews
	protected void init() {
		configFragment.setValidationListener(this);
	}

	@Override
	@UiThread(delay = TIME_TO_CLOSE)
	public void onValidationSucceed() {
		Synchronizer_.intent(this).start();
		finish();
	}

	@OptionsItem(android.R.id.home)
	protected void quit() {
		finish();
	}
}
