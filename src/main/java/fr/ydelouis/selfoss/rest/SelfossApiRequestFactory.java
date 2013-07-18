package fr.ydelouis.selfoss.rest;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@EBean
public class SelfossApiRequestFactory extends SimpleClientHttpRequestFactory implements HostnameVerifier, X509TrustManager {

	@Pref protected SelfossConfig_ selfossConfig;

	public SelfossApiRequestFactory() {
		trustAllHosts();
	}

	@Override
	protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
		if (selfossConfig.trustAllCertificates().get() && connection instanceof HttpsURLConnection) {
			((HttpsURLConnection) connection).setHostnameVerifier(this);
		}
		super.prepareConnection(connection, httpMethod);
	}

	@Override
	public boolean verify(String s, SSLSession sslSession) {
		return true;
	}

	@Override
	public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain,String authType) throws CertificateException {

	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[]{};
	}

	private void trustAllHosts() {
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[]{this}, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
