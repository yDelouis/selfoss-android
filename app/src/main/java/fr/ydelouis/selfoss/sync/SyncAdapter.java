package fr.ydelouis.selfoss.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.os.Bundle;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

import fr.ydelouis.selfoss.config.model.Config;
import fr.ydelouis.selfoss.config.model.ConfigManager;

@EBean
public class SyncAdapter extends AbstractThreadedSyncAdapter {

	@Bean protected ConfigManager configManager;
	@SystemService protected ConnectivityManager connectivityManager;
	@Bean protected Uploader uploader;
	@Bean protected SourceSync sourceSync;
	@Bean protected TagSync tagSync;
	@Bean protected ArticleSync articleSync;

	public SyncAdapter(Context context) {
		super(context, true);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
		Config config = configManager.get();
		if (config.syncOverWifiOnly() && !isConnectedOverWifi()) {
			return;
		}
		try {
			performSync();
		} catch (Exception e) {
			handleException(e, syncResult);
		}
	}

	private boolean isConnectedOverWifi() {
		return connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
	}

	private void performSync() {
		uploader.performSync();
		tagSync.performSync();
        sourceSync.performSync();
		articleSync.performSync();
	}

	private void handleException(Exception e, SyncResult syncResult) {
		if (e instanceof IOException
			|| e instanceof ResourceAccessException
			|| e instanceof RestClientException) {
			syncResult.stats.numIoExceptions++;
		} else {
			e.printStackTrace();
		}
	}

}
