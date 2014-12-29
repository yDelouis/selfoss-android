package fr.ydelouis.selfoss.config.model;

import android.content.Context;
import android.support.annotation.NonNull;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.rest.RestService;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.entity.Success;
import fr.ydelouis.selfoss.rest.SelfossApiInterceptor;
import fr.ydelouis.selfoss.rest.SelfossApiRequestFactory;
import fr.ydelouis.selfoss.rest.SelfossRest;

@EBean
public class ConfigValidator {

	public interface UrlValidationCallback {
		void onUrlValidationSuccess();
		void onUrlValidationFailed(Exception exception);
	}

	public interface AuthValidationCallback {
		void onAuthValidationSuccess();
		void onAuthValidationFailed(Exception exception);
	}

	public class CertificateException extends Exception {}
	public class InvalidUsernameException extends Exception {}
	public class IncorrectPasswordException extends Exception {}

	@RootContext protected Context context;
	@RestService protected SelfossRest selfossRest;
	@Bean protected SelfossApiInterceptor interceptor;
	@Bean protected SelfossApiRequestFactory requestFactory;

	private Config config;
	private boolean requireAuth;

	public void setConfig(Config config) {
		this.config = config;
		interceptor.setConfig(config);
		requestFactory.setConfig(config);
		RestTemplate restTemplate = selfossRest.getRestTemplate();
		restTemplate.getInterceptors().clear();
		restTemplate.getInterceptors().add(interceptor);
		restTemplate.setRequestFactory(requestFactory);
	}

// Url

	public void validateUrl(String url, @NonNull UrlValidationCallback callback) {
		if(isUrlValid(url)) {
			setUseHttps(url);
			String cleanUrl = cleanUrl(url);
			checkUrl(cleanUrl, false, callback);
		} else {
			callback.onUrlValidationFailed(new Exception(context.getString(R.string.error_urlEmpty)));
		}
	}

	public void validateUrlTrustingAllCertificates(String url, @NonNull UrlValidationCallback callback) {
		if(isUrlValid(url)) {
			String cleanUrl = cleanUrl(url);
			checkUrl(cleanUrl, true, callback);
		} else {
			callback.onUrlValidationFailed(new Exception(context.getString(R.string.error_urlEmpty)));
		}
	}

	private boolean isUrlValid(String url) {
		return !url.isEmpty();
	}

	private void setUseHttps(String url) {
		config.setUseHttps(url.startsWith("https://"));
	}

	private String cleanUrl(String url) {
		String cleanedUrl = removeScheme(url);
		cleanedUrl = removeTrailingSlash(cleanedUrl);
		return cleanedUrl;
	}

	private String removeScheme(String url) {
		String schemeMark = "://";
		int start = url.indexOf(schemeMark);
		if (start != -1) {
			return url.substring(start+schemeMark.length());
		}
		return url;
	}

	private String removeTrailingSlash(String url) {
		String trailingSlash = "/";
		if (url.endsWith(trailingSlash)) {
			return url.substring(0, url.length()-trailingSlash.length());
		}
		return url;
	}

	private void checkUrl(String url, boolean trustAllCertificates, @NonNull UrlValidationCallback callback) {
		config.setUrl(url);
		config.setTrustAllCertificates(trustAllCertificates);
		requireAuth = false;
		try {
			Success success = selfossRest.login();
			if (!success.isSuccess() && !isBecauseOfBugLogin()) {
				requireAuth = true;
				tryWithHttps();
			}
			callback.onUrlValidationSuccess();
		} catch (RestClientException e) {
			handleUrlException(e, callback);
		}
	}

	private boolean isBecauseOfBugLogin() {
		try {
			selfossRest.listTags();
			return true;
		} catch (RestClientException e) {
			return false;
		}
	}

	private void tryWithHttps() {
		config.setUseHttps(true);
		try {
			selfossRest.login();
		} catch (RestClientException e) {
			config.setUseHttps(false);
		}
	}

	private void handleUrlException(Exception exception, @NonNull UrlValidationCallback callback) {
		if (isCertificateException(exception)) {
			exception = new CertificateException();
		}
		callback.onUrlValidationFailed(exception);
	}

	private boolean isCertificateException(Exception e) {
		return e.getCause() instanceof IOException
				&& e.getMessage().contains("Hostname")
				&& e.getMessage().contains("was not verified");
	}

// Auth

	public boolean requireAuth() {
		return requireAuth;
	}

	public void validateUsernameAndPassword(String username, String password, AuthValidationCallback callback) {
		if(isUsernameValid(username)) {
			config.setUsername(username);
			config.setPassword(password);
			checkAuth(callback);
		} else {
			callback.onAuthValidationFailed(new InvalidUsernameException());
		}
	}

	private void checkAuth(AuthValidationCallback callback) {
		try {
			Success success = selfossRest.login();
			if (success.isSuccess()) {
				callback.onAuthValidationSuccess();
			} else {
				callback.onAuthValidationFailed(new IncorrectPasswordException());
			}
		} catch (RestClientException exception) {
			callback.onAuthValidationFailed(exception);
		}
	}

	private boolean isUsernameValid(String username) {
		return !username.isEmpty();
	}

}
