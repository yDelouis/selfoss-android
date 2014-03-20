package fr.ydelouis.selfoss.account;

import android.accounts.Account;
import android.accounts.AccountManager;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import fr.ydelouis.selfoss.sync.SyncPeriod;

@EBean
public class SelfossAccount {

	public static final String ACCOUNT_TYPE = "fr.ydelouis.selfoss";
	private static final String KEY_URL = "url";
	private static final String KEY_REQUIRE_AUTH = "requireAuth";
	private static final String KEY_SYNC_PERIOD = "syncPeriod";
	private static final String KEY_TRUST_ALL_CERTIFICATES = "trustAllCertificates";
	private static final String KEY_USE_HTTPS = "useHttps";

	@SystemService protected AccountManager accountManager;

	public Account getAccount() {
		Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
		if (accounts.length > 0) {
			return accounts[0];
		}
		return null;
	}

	public void create(String url, long syncPeriod) {
		create(url, false, url, "", false, syncPeriod);
	}

	public void create(String url, String username, String password, boolean useHttps, long syncPeriod) {
		create(url, true, username, password, useHttps, syncPeriod);
	}

	private void create(String url, boolean requireAuth, String username, String password, boolean useHttps, long syncPeriod) {
		Account account = getAccount();
		if (account != null && !account.name.equals(username)) {
			accountManager.removeAccount(account, null, null);
			account = null;
		}
		if (account == null) {
			account = new Account(username, ACCOUNT_TYPE);
			accountManager.addAccountExplicitly(account, password, null);
		}
		accountManager.setPassword(account, password);
		accountManager.setUserData(account, KEY_URL, url);
		accountManager.setUserData(account, KEY_USE_HTTPS, String.valueOf(useHttps));
		accountManager.setUserData(account, KEY_SYNC_PERIOD, String.valueOf(syncPeriod));
		accountManager.setUserData(account, KEY_REQUIRE_AUTH, String.valueOf(requireAuth));
	}

	public String getUrl() {
		Account account = getAccount();
		if (account == null) {
			return "";
		} else {
			return accountManager.getUserData(account, KEY_URL);
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

	public String getUsername() {
		Account account = getAccount();
		if (account == null) {
			return "";
		} else {
			return account.name;
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

	public long getSyncPeriod() {
		Account account = getAccount();
		if (account == null) {
			return SyncPeriod.getDefault().getTime();
		} else {
			return Long.valueOf(accountManager.getUserData(account, KEY_SYNC_PERIOD));
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

	public void setUseHttps(boolean useHttps) {
		Account account = getAccount();
		if (account == null) {
			throw new IllegalStateException("Account has not been created yet");
		} else {
			accountManager.setUserData(account, KEY_USE_HTTPS, String.valueOf(useHttps));
		}
	}
}
