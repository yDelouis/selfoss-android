package fr.ydelouis.selfoss.fragment;

import android.app.Fragment;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.activity.SelfossConfigActivity_;
import fr.ydelouis.selfoss.rest.SelfossConfig_;

@EFragment(R.layout.fragment_menu)
public class MenuFragment extends Fragment {

	@Pref protected SelfossConfig_ selfossConfig;

	@ViewById protected TextView url;

	@AfterViews
	protected void updateViews() {
		url.setText(selfossConfig.url().getOr(""));
	}

	@Override
	public void onResume() {
		super.onResume();
		updateViews();
	}

	@Click(R.id.url)
	protected void openSelfossConfig() {
		SelfossConfigActivity_.intent(getActivity()).start();
	}
}
