package fr.ydelouis.selfoss.sync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import fr.ydelouis.selfoss.BuildConfig;
import fr.ydelouis.selfoss.config.model.Config;
import fr.ydelouis.selfoss.config.model.ConfigManager;

@EBean
public class SyncManager {

	private static final String AUTHORITY = BuildConfig.AUTHORITY;

	public static void setPeriodicSync(ConfigManager configManager, Config config) {
		Account account = configManager.getAccountForConfig(config);
		if (account != null) {
			ContentResolver.setSyncAutomatically(account, AUTHORITY, config.getSyncPeriod() > 0);
			ContentResolver.addPeriodicSync(account, AUTHORITY, new Bundle(), config.getSyncPeriod());
		}
	}

	@Bean protected ConfigManager configManager;

	public void requestSync() {
		Account account = configManager.getAccount();
		if (account != null) {
			Bundle extras = new Bundle();
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
			ContentResolver.requestSync(account, AUTHORITY, extras);
		}
	}

	public boolean isActive() {
		Account account = configManager.getAccount();
		if (account != null) {
			return ContentResolver.isSyncActive(account, AUTHORITY);
		} else {
			return false;
		}
	}

}
