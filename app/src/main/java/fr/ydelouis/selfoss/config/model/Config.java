package fr.ydelouis.selfoss.config.model;

public class Config {

	private String url;
	private boolean trustAllCertificates;
	private boolean useHttps;
	private String username;
	private String password;
	private long syncPeriod;


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean trustAllCertificates() {
		return trustAllCertificates;
	}

	public void setTrustAllCertificates(boolean trustAllCertificates) {
		this.trustAllCertificates = trustAllCertificates;
	}

	public boolean useHttps() {
		return useHttps;
	}

	public void setUseHttps(boolean useHttps) {
		this.useHttps = useHttps;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getSyncPeriod() {
		return syncPeriod;
	}

	public void setSyncPeriod(long syncPeriod) {
		this.syncPeriod = syncPeriod;
	}

	public boolean requireAuth() {
		return username != null;
	}
}
