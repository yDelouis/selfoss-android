package fr.ydelouis.selfoss.sync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import fr.ydelouis.selfoss.account.SelfossAccount;

@EBean
public class SyncManager {

	private static final String AUTHORITY = "fr.ydelouis.selfoss";

	@Bean protected SelfossAccount selfossAccount;

	public void requestSync() {
		Account account = selfossAccount.getAccount();
		if (account != null) {
			Bundle extras = new Bundle();
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
			ContentResolver.requestSync(account, AUTHORITY, extras);
		}
	}

	public boolean isActive() {
		return ContentResolver.isSyncActive(selfossAccount.getAccount(), AUTHORITY);
	}

}
