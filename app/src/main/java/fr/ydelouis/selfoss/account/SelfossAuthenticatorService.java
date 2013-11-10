package fr.ydelouis.selfoss.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;

@EService
public class SelfossAuthenticatorService extends Service {

	@Bean protected SelfossAuthenticator authenticator;

	@Override
	public IBinder onBind(Intent intent) {
		return authenticator.getIBinder();
	}
}
