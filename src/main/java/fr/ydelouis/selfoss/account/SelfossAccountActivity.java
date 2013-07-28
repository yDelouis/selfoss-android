package fr.ydelouis.selfoss.account;

import android.accounts.AccountAuthenticatorActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.account.SelfossAccount;
import fr.ydelouis.selfoss.entity.Success;
import fr.ydelouis.selfoss.rest.SelfossRest;
import fr.ydelouis.selfoss.service.Synchronizer_;

@EActivity(R.layout.activity_selfossaccount)
public class SelfossAccountActivity extends AccountAuthenticatorActivity {

	private static final long TIME_TO_CLOSE = 1500;

	@Bean protected SelfossAccount account;
	@RestService protected SelfossRest selfossRest;
	@SystemService protected InputMethodManager inputMethodManager;

	@ViewById protected EditText url;
	@ViewById protected CheckBox requireAuth;
	@ViewById protected View usernamePasswordContainer;
	@ViewById protected EditText username;
	@ViewById protected EditText password;
	@ViewById protected View validate;
	@ViewById protected View progress;
	@ViewById protected TextView validateText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@AfterViews
	protected void updateUi() {
		url.setText(account.getUrl());
		requireAuth.setChecked(account.requireAuth());
		username.setText(account.getUsername());
		password.setText(account.getPassword());
	}

	@CheckedChange(R.id.requireAuth)
	protected void onProtectedStateChange(CompoundButton checkBox, boolean isChecked) {
		usernamePasswordContainer.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
	}

	@Click(R.id.validate)
	@EditorAction(R.id.password)
	protected void onValidate() {
		validateUrl();
		hydrate();
		showProgress();
		tryLogin();
	}

	private void validateUrl() {
		String validatedUrl = url.getText().toString();
		validatedUrl = validatedUrl.replace("http://", "");
		validatedUrl = validatedUrl.replace("https://", "");
		url.setText(validatedUrl);
	}

	private void hydrate() {
		if (requireAuth.isChecked()) {
			account.create(url.getText().toString(),
							username.getText().toString(),
							password.getText().toString());
		} else {
			account.create(url.getText().toString());
		}
		account.setTrustAllCertificates(false);
	}

	protected void showProgress() {
		progress.setVisibility(View.VISIBLE);
		validateText.setText(R.string.checking);
		validate.setEnabled(false);
		inputMethodManager.hideSoftInputFromWindow(url.getWindowToken(), 0);
	}

	@Background
	protected void tryLogin() {
		try {
			Success success = selfossRest.login();
			handleSuccess(success);
		} catch (RestClientException e) {
			handleException(e);
		}
	}

	private void handleSuccess(Success success) {
		if (success.isSuccess()) {
			showSuccess();
			quitDelayed();
		} else {
			showUsernamePasswordError();
		}
	}

	private void handleException(RestClientException e) {
		if (isCertificateException(e)) {
			showCertificateError();
		} else {
			showUrlError();
		}
	}

	private boolean isCertificateException(RestClientException e) {
		return e.getCause() instanceof IOException
			&& e.getMessage().contains("Hostname")
			&& e.getMessage().contains("was not verified");
	}

	private void hideProgress() {
		progress.setVisibility(View.GONE);
		validate.setEnabled(true);
		validateText.setText(R.string.validate);
	}

	@UiThread
	protected void showSuccess() {
		progress.setVisibility(View.GONE);
		validate.setBackgroundResource(R.drawable.bg_button_success);
		validateText.setText(R.string.success);
	}

	@UiThread(delay = TIME_TO_CLOSE)
	public void quitDelayed() {
		Synchronizer_.intent(this).start();
		finish();
	}

	@UiThread
	protected void showUrlError() {
		showError();
		url.setError(getString(R.string.error_url));
	}

	@UiThread
	protected void showUsernamePasswordError() {
		showError();
		requireAuth.setChecked(true);
		password.setError(getString(R.string.error_usernamePassword));
	}

	@UiThread
	protected void showCertificateError() {
		showError();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.certificate_error);
		builder.setMessage(getString(R.string.certificate_error_message, account.getUrl()));
		builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				trustAllCertificates();
			}
		});
		builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				hideError();
			}
		});
		builder.show();
	}

	private void showError() {
		hideProgress();
		validate.setBackgroundResource(R.drawable.bg_button_error);
		validateText.setText(R.string.error);
	}


	@TextChange({ R.id.url, R.id.username, R.id.password })
	protected void hideError() {
		validate.setBackgroundResource(R.drawable.bg_button_default);
		validateText.setText(R.string.validate);
	}

	private void trustAllCertificates() {
		account.setTrustAllCertificates(true);
		validate.setBackgroundResource(R.drawable.bg_button_default);
		showProgress();
		tryLogin();
	}

	@OptionsItem(android.R.id.home)
	protected void quit() {
		finish();
	}
}