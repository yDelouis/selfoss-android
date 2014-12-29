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

// OLD
/*
	public void create(String url) {
		Account account = getAccount();
		if (account != null && !account.name.equals(url)) {
			remove();
			account = null;
		}
		if (account == null) {
			account = new Account(url, ACCOUNT_TYPE);
			accountManager.addAccountExplicitly(account, null, null);
		}
	}

	public void create(String url, boolean useHttps, long syncPeriod) {
		create(url, false, url, "", useHttps, syncPeriod);
	}

	public void create(String url, String username, String password, boolean useHttps, long syncPeriod) {
		create(url, true, username, password, useHttps, syncPeriod);
	}

	private void create(String url, boolean requireAuth, String username, String password, boolean useHttps, long syncPeriod) {
		Account account = getAccount();
		if (account != null && !account.name.equals(username)) {
			accountManager.removeAccount(account, null, null);
			databaseHelper.clearTables();
			account = null;
		}
		if (account == null) {
			account = new Account(username, ACCOUNT_TYPE);
			accountManager.addAccountExplicitly(account, password, null);
		}
		accountManager.setPassword(account, password);
		accountManager.setUserData(account, KEY_USE_HTTPS, String.valueOf(useHttps));
		accountManager.setUserData(account, KEY_SYNC_PERIOD, String.valueOf(syncPeriod));
		accountManager.setUserData(account, KEY_REQUIRE_AUTH, String.valueOf(requireAuth));

		//SyncManager.setPeriodicSync(this);
	}



	public String getUrl() {
		Account account = getAccount();
		if (account == null) {
			return null;
		} else {
			return account.name;
		}
	}

	public boolean requireAuth() {
		Account account = getAccount();
		if (account == null) {
			return false;
		} else {
			return Boolean.valueOf(accountManager.getUserData(account, KEY_REQUIRE_AUTH));
		}
	}


	public void setRequireAuth(boolean requireAuth) {
		Account account = getAccount();
		if (account != null) {
			accountManager.setUserData(account, KEY_REQUIRE_AUTH, String.valueOf(requireAuth));
		}
	}

	public String getUsername() {
		Account account = getAccount();
		if (account == null) {
			return null;
		} else {
			return accountManager.getUserData(account, KEY_USERNAME);
		}
	}

	public void setUsername(String username) {
		Account account = getAccount();
		if (account != null) {
			accountManager.setUserData(account, KEY_USERNAME, username);
		}
	}

	public String getPassword() {
		Account account = getAccount();
		if (account == null) {
			return "";
		} else {
			return accountManager.getPassword(account);
		}
	}

	public void setPassword(String password) {
		Account account = getAccount();
		if (account != null) {
			accountManager.setPassword(account, password);
		}
	}

	public SyncPeriod getSyncPeriod() {
		Account account = getAccount();
		if (account == null) {
			return SyncPeriod.getDefault();
		} else {
			return SyncPeriod.fromTime(Long.valueOf(accountManager.getUserData(account, KEY_SYNC_PERIOD)));
		}
	}

	public boolean trustAllCertificates() {
		Account account = getAccount();
		if (account == null) {
			return false;
		} else {
			return Boolean.valueOf(accountManager.getUserData(account, KEY_TRUST_ALL_CERTIFICATES));
		}
	}

	public void setTrustAllCertificates(boolean trustAllCertificates) {
		Account account = getAccount();
		if (account == null) {
			throw new IllegalStateException("Account has not been created yet");
		} else {
			accountManager.setUserData(account, KEY_TRUST_ALL_CERTIFICATES, String.valueOf(trustAllCertificates));
		}
	}

	public boolean useHttps() {
		Account account = getAccount();
		if (account == null) {
			return requireAuth();
		} else {
			String useHttpsString = accountManager.getUserData(account, KEY_USE_HTTPS);
			if (useHttpsString == null) {
				return requireAuth();
			} else {
				return Boolean.valueOf(useHttpsString);
			}
		}
	}
	*/
}
