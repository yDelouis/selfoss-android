package fr.ydelouis.selfoss.fragment;

import android.app.Fragment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.springframework.web.client.RestClientException;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.api.SelfossConfig_;
import fr.ydelouis.selfoss.api.entity.Success;
import fr.ydelouis.selfoss.api.rest.LoginRest;

@EFragment(R.layout.fragment_selfossconfig)
public class SelfossConfigFragment extends Fragment {

	@Pref protected SelfossConfig_ selfossConfig;
	@RestService protected LoginRest loginRest;
	private ValidationListener validatIonListener;

    @ViewById protected EditText url;
    @ViewById protected CheckBox requireAuth;
    @ViewById protected View usernamePasswordContainer;
    @ViewById protected EditText username;
    @ViewById protected EditText password;
	@ViewById protected View validate;
	@ViewById protected View progress;
	@ViewById protected TextView validateText;

	public void setValidatIonListener(ValidationListener listener) {
		this.validatIonListener = listener;
	}

	@AfterViews
	protected void updateUi() {
		url.setText(selfossConfig.url().getOr(""));
		requireAuth.setChecked(selfossConfig.requireAuth().getOr(false));
		username.setText(selfossConfig.username().getOr(""));
		password.setText(selfossConfig.password().getOr(""));
	}

    @CheckedChange(R.id.requireAuth)
    protected void onProtectedStateChange(CompoundButton checkBox, boolean isChecked) {
        usernamePasswordContainer.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
    }

	@Click(R.id.validate)
	@EditorAction(R.id.password)
	protected void onValidate() {
		hydrate();
		showProgress();
		tryLogin();
	}

	private void hydrate() {
		selfossConfig.edit()
				.url().put(url.getText().toString())
				.requireAuth().put(requireAuth.isChecked())
				.username().put(username.getText().toString())
				.password().put(password.getText().toString())
				.apply();
	}

	protected void showProgress() {
		progress.setVisibility(View.VISIBLE);
		validateText.setText(R.string.checking);
		validate.setEnabled(false);
	}

	@Background
	protected void tryLogin() {
		try {
			Success success = loginRest.login();
			if (success.isSuccess()) {
				showSuccessAndQuit();
			} else {
				showUsernamePasswordError();
			}
		} catch (RestClientException e) {
			showUrlError();
		}
	}

	private void hideProgress() {
		progress.setVisibility(View.GONE);
		validate.setEnabled(true);
		validateText.setText(R.string.validate);
	}

	@UiThread
	protected void showSuccessAndQuit() {
		progress.setVisibility(View.GONE);
		validate.setBackgroundResource(R.drawable.bg_button_success);
		validateText.setText(R.string.success);
		if (validatIonListener != null)
			validatIonListener.onValidationSucceed();
	}

	@UiThread
	protected void showUrlError() {
		hideProgress();
		url.setError(getString(R.string.error_url));
	}

	@UiThread
	protected void showUsernamePasswordError() {
		hideProgress();
		requireAuth.setChecked(true);
		password.setError(getString(R.string.error_usernamePassword));
	}

	public interface ValidationListener {
		void onValidationSucceed();
	}

}
