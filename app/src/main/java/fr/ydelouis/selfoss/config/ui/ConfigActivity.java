package fr.ydelouis.selfoss.config.ui;

import android.accounts.AccountAuthenticatorActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.config.model.Config;
import fr.ydelouis.selfoss.config.model.ConfigManager;
import fr.ydelouis.selfoss.config.model.ConfigValidator;
import fr.ydelouis.selfoss.sync.SyncManager;

@EActivity(R.layout.activity_config)
public class ConfigActivity extends AccountAuthenticatorActivity implements ConfigValidator.UrlValidationCallback, ConfigValidator.AuthValidationCallback {

	@Bean protected ConfigManager configManager;
	@Bean protected SyncManager syncManager;
	@Bean protected ConfigValidator configValidator;
	@SystemService protected InputMethodManager inputMethodManager;
	private Config config;

	@ViewById(R.id.toolbar) protected Toolbar toolbar;
	@ViewById(R.id.title) protected TextView title;
	@ViewById(R.id.progress) protected ProgressBar progress;
	@ViewById(R.id.urlView) protected ConfigUrlView urlView;
	@ViewById(R.id.authView) protected ConfigAuthView authView;
	@ViewById(R.id.syncPeriodView) protected ConfigSyncView syncPeriodView;
	@ViewById(R.id.next) protected TextView next;

	@AfterInject
	protected void init() {
		config = configManager.get();
		if (config == null) {
			config = new Config();
		}
		configValidator.setConfig(config);
	}

	@AfterViews
	protected void initToolbar() {
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_48dp);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	@AfterViews
	protected void initViews() {
		urlView.setUrl(config.getUrl());
		authView.setUsername(config.getUsername());
		authView.setPassword(config.getPassword());
		syncPeriodView.setSyncPeriod(config.getSyncPeriod());
		syncPeriodView.setSyncOverWifiOnly(config.syncOverWifiOnly());
	}

	@Click(R.id.next)
	protected void next() {
		if (urlView.getVisibility() == View.VISIBLE) {
			nextUrl();
		} else if (authView.getVisibility() == View.VISIBLE) {
			nextAuth();
		} else {
			nextSyncPeriod();
		}
	}

	private void back() {
		if (urlView.getVisibility() == View.VISIBLE) {
			quit();
		} else if (authView.getVisibility() == View.VISIBLE) {
			showUrlView();
		} else {
			if (configValidator.requireAuth()) {
				showAuthView();
			} else {
				showUrlView();
			}
		}
	}

// URL

	private void nextUrl() {
		inputMethodManager.hideSoftInputFromWindow(toolbar.getWindowToken(), 0);
		setChecking(true);
		validateUrl();
	}

	@Background
	protected void validateUrl() {
		configValidator.validateUrl(urlView.getUrl(), this);
	}

	@Override
	@UiThread
	public void onUrlValidationSuccess() {
		setChecking(false);
		if (configValidator.requireAuth()) {
			showAuthView();
		} else {
			showSyncPeriodView();
		}
	}

	@Override
	@UiThread
	public void onUrlValidationFailed(Exception exception) {
		setChecking(false);
		if (exception instanceof ConfigValidator.CertificateException) {
			showCertificateError();
		} else {
			urlView.showError(exception);
		}
	}

	protected void showCertificateError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.certificate_error);
		builder.setMessage(getString(R.string.certificate_error_message, config.getUrl()));
		builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				trustAllCertificates();
			}
		});
		builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		builder.show();
	}

	private void trustAllCertificates() {
		setChecking(true);
		configValidator.validateUrlTrustingAllCertificates(urlView.getUrl(), this);
	}

// Auth

	private void nextAuth() {
		inputMethodManager.hideSoftInputFromWindow(toolbar.getWindowToken(), 0);
		setChecking(true);
		validateAuth();
	}

	@Background
	protected void validateAuth() {
		configValidator.validateUsernameAndPassword(authView.getUsername(), authView.getPassword(), this);
	}

	@Override
	@UiThread
	public void onAuthValidationSuccess() {
		setChecking(false);
		showSyncPeriodView();
	}

	@Override
	@UiThread
	public void onAuthValidationFailed(Exception exception) {
		setChecking(false);
		authView.showError(exception);
	}

// SyncPeriod

	private void nextSyncPeriod() {
		config.setSyncPeriod(syncPeriodView.getSyncPeriod());
		config.setSyncOverWifiOnly(syncPeriodView.getSyncOverWifiOnly());
		configManager.save(config);
		setResult(RESULT_OK);
		finish();
	}

// Views

	private void setChecking(boolean checking){
		progress.setVisibility(checking ? View.VISIBLE : View.INVISIBLE);
		next.setEnabled(!checking);
	}

	private void showUrlView() {
		title.setText(R.string.account_url_title);
		urlView.setVisibility(View.VISIBLE);
		authView.setVisibility(View.GONE);
		syncPeriodView.setVisibility(View.GONE);
		next.setText(R.string.next);
	}

	private void showAuthView() {
		title.setText(R.string.account_auth_title);
		urlView.setVisibility(View.GONE);
		authView.setVisibility(View.VISIBLE);
		syncPeriodView.setVisibility(View.GONE);
		next.setText(R.string.next);
	}

	private void showSyncPeriodView() {
		title.setText(R.string.account_syncPeriod_title);
		urlView.setVisibility(View.GONE);
		authView.setVisibility(View.GONE);
		syncPeriodView.setVisibility(View.VISIBLE);
		next.setText(R.string.finish);
	}

// Back

	@OptionsItem(android.R.id.home)
	protected void quit() {
		setResult(RESULT_CANCELED);
		finish();
	}

	@Override
	public void onBackPressed() {
		back();
	}
}
