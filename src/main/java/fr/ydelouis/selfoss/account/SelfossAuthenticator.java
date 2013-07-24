package fr.ydelouis.selfoss.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.rest.RestService;

import fr.ydelouis.selfoss.activity.SelfossAccountActivity_;
import fr.ydelouis.selfoss.rest.SelfossRest;

@EBean
public class SelfossAuthenticator extends AbstractAccountAuthenticator {

	private Context context;
	@RestService protected SelfossRest selfossRest;

	public SelfossAuthenticator(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
		Intent intent = SelfossAccountActivity_.intent(context).get();
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
		AccountManager am = AccountManager.get(context);

		String authToken = am.peekAuthToken(account, authTokenType);
		if (!TextUtils.isEmpty(authToken)) {
			Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
			result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
			result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
			return result;
		}

		return addAccount(response, account.type, authTokenType, null, options);
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
		return null;
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
		return null;
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		return null;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
		return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
		return null;
	}
}
