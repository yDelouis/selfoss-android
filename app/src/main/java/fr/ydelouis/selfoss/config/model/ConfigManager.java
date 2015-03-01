package fr.ydelouis.selfoss.config.model;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import fr.ydelouis.selfoss.BuildConfig;
import fr.ydelouis.selfoss.model.DatabaseHelper;
import fr.ydelouis.selfoss.sync.SyncManager;

@EBean
public class ConfigManager {

	public static final String ACCOUNT_TYPE = BuildConfig.ACCOUNT_TYPE;

	private static final String KEY_USERNAME = "username";
	private static final String KEY_SYNC_PERIOD = "syncPeriod";
	private static final String KEY_TRUST_ALL_CERTIFICATES = "trustAllCertificates";
	private static final String KEY_USE_HTTPS = "useHttps";
	private static final String KEY_SYNC_OVER_WIFI_ONLY = "syncOverWifiOnly";

	@SystemService protected AccountManager accountManager;
	@RootContext protected Context context;
	private DatabaseHelper databaseHelper;

	@AfterInject
	protected void init() {
		databaseHelper = new DatabaseHelper(context);
	}

	public void save(Config config) {
		Account account = getAccount();
		if (account != null && !account.name.equals(config.getUrl())) {
			remove(account);
			account = null;
		}
		if (account == null) {
			account = new Account(config.getUrl(), ACCOUNT_TYPE);
			accountManager.addAccountExplicitly(account, config.getPassword(), null);
		}
		accountManager.setUserData(account, KEY_USERNAME, config.getUsername());
		accountManager.setUserData(account, KEY_USE_HTTPS, String.valueOf(config.useHttps()));
		accountManager.setUserData(account, KEY_TRUST_ALL_CERTIFICATES, String.valueOf(config.trustAllCertificates()));
		accountManager.setUserData(account, KEY_SYNC_PERIOD, String.valueOf(config.getSyncPeriod()));
		accountManager.setUserData(account, KEY_SYNC_OVER_WIFI_ONLY, String.valueOf(config.syncOverWifiOnly()));

		SyncManager.setPeriodicSync(this, config);
	}

	public Config get() {
		Account account = getAccount();
		Config config = null;
		if (account != null) {
			config = new Config();
			config.setUrl(account.name);
			config.setTrustAllCertificates(Boolean.valueOf(accountManager.getUserData(account, KEY_TRUST_ALL_CERTIFICATES)));
			config.setUsername(accountManager.getUserData(account, KEY_USERNAME));
			config.setPassword(accountManager.getPassword(account));
			config.setUseHttps(Boolean.valueOf(accountManager.getUserData(account, KEY_USE_HTTPS)));
			config.setSyncOverWifiOnly(Boolean.valueOf(accountManager.getUserData(account, KEY_SYNC_OVER_WIFI_ONLY)));
			String syncPeriod = accountManager.getUserData(account, KEY_SYNC_PERIOD);
			if (syncPeriod != null) {
				config.setSyncPeriod(Long.valueOf(syncPeriod));
			}
		}
		return config;
	}

	public Account getAccountForConfig(Config config) {
		Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
		for (Account account : accounts) {
			if (account.name.equals(config.getUrl())) {
				return account;
			}
		}
		return null;
	}

	public Account getAccount() {
		Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
		if (accounts.length > 0) {
			return accounts[0];
		}
		return null;
	}

	private void remove(Account account) {
		accountManager.removeAccount(account, null, null);
		databaseHelper.clearTables();
	}
}
