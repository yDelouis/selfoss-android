package fr.ydelouis.selfoss.rest;

import android.net.Uri;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import fr.ydelouis.selfoss.BuildConfig;
import fr.ydelouis.selfoss.account.SelfossAccount;
import fr.ydelouis.selfoss.util.Streams;

@EBean
public class SelfossApiInterceptor implements ClientHttpRequestInterceptor {

	private static final String TAG = "Selfoss API";
	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";
	private static boolean LOG_REQUEST = BuildConfig.DEBUG && true;
	private static boolean LOG_FULL_REQUEST = BuildConfig.DEBUG &&  true;
	private static boolean LOG_RESPONSE = BuildConfig.DEBUG && true;

	@Bean protected SelfossAccount account;

	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		HttpRequest apiRequest = new ApiHttpRequest(request);
		logRequest(apiRequest);
		ApiHttpResponse response = new ApiHttpResponse(execution.execute(apiRequest, body));
		logResponse(response);
		return response;
	}

	private void logResponse(ApiHttpResponse response) throws IOException {
		if(LOG_RESPONSE)
			Log.i(TAG, response.getStatusCode().value() + " : " + Streams.stringOf(response.getBody()));
	}

	private void logRequest(HttpRequest request) {
		String requestUri = request.getURI().toString();
		if(!LOG_FULL_REQUEST && LOG_REQUEST) {
			int start = requestUri.indexOf(account.getUrl());
			requestUri = requestUri.substring(start);
		}
		Log.i(TAG, request.getMethod() + " : " + requestUri);
	}

	private class ApiHttpRequest implements HttpRequest
	{
		private HttpRequest httpRequest;

		public ApiHttpRequest(HttpRequest httpRequest) {
			this.httpRequest = httpRequest;
			this.httpRequest.getHeaders().set("Content-Length", "0");
			this.httpRequest.getHeaders().remove("Content-Type");
		}

		@Override
		public HttpHeaders getHeaders() {
			return httpRequest.getHeaders();
		}

		@Override
		public HttpMethod getMethod() {
			return httpRequest.getMethod();
		}

		@Override
		public URI getURI() {
			URI uri = httpRequest.getURI();
			Uri.Builder builder = new Uri.Builder();
			builder.scheme(getScheme());
			builder.authority(account.getUrl());
			builder.path(uri.getPath());
			builder.encodedQuery(uri.getQuery());
			if (account.requireAuth()) {
				builder.appendQueryParameter(KEY_USERNAME, account.getUsername());
				builder.appendQueryParameter(KEY_PASSWORD, account.getPassword());
			}
			return URI.create(builder.build().toString());
		}

		private String getScheme() {
			return account.requireAuth() ? "https" : "http";
		}
	}

	private class ApiHttpResponse implements ClientHttpResponse
	{
		private ClientHttpResponse response;
		private byte[] byteResponse;

		public ApiHttpResponse(ClientHttpResponse response) {
			try {
				this.response = response;
				this.response.getHeaders().set("Content-Type", "application/json");
				byteResponse = Streams.byteArrayOf(response.getBody());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public InputStream getBody() throws IOException {
			return new ByteArrayInputStream(byteResponse);
		}

		@Override
		public HttpHeaders getHeaders() {
			return response.getHeaders();
		}

		@Override
		public void close() {
			response.close();
		}

		@Override
		public int getRawStatusCode() throws IOException {
			return response.getRawStatusCode();
		}

		@Override
		public HttpStatus getStatusCode() throws IOException {
			return response.getStatusCode();
		}

		@Override
		public String getStatusText() throws IOException {
			return response.getStatusText();
		}
	}
}

