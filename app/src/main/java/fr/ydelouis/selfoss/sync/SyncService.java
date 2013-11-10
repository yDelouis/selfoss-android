package fr.ydelouis.selfoss.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;

@EService
public class SyncService extends Service {

	@Bean protected SyncAdapter syncAdapter;

	@Override
	public IBinder onBind(Intent intent) {
		return syncAdapter.getSyncAdapterBinder();
	}
}
