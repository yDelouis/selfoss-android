package fr.ydelouis.selfoss.util;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.UrlConnectionDownloader;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;

import fr.ydelouis.selfoss.account.SelfossAccount;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.Source;

@EBean
public class FaviconUtil {

	@Bean protected SelfossAccount account;
	@RootContext protected Context context;
	private Picasso picasso;

	@AfterInject
	protected void init() {
		Picasso.Builder builder = new Picasso.Builder(context);
		builder.downloader(new AuthUrlConnectionDownloader(context));
		picasso = builder.build();
	}

	public String faviconUrl(String favicon) {
		return getScheme() + account.getUrl() + "/favicons/" + favicon;
	}

	private String getScheme() {
		return account.useHttps() ? "https://" : "http://";
	}

	public String faviconUrl(Article article) {
		return faviconUrl(article.getIcon());
	}

	public String faviconUrl(Source source) {
		return faviconUrl(source.getIcon());
	}

	public void loadFavicon(Article article, Target target) {
		picasso.load(faviconUrl(article)).into(target);
	}

	public void loadFavicon(Article article, ImageView imageView) {
		picasso.load(faviconUrl(article)).into(imageView);
	}

	public void loadFavicon(Source source, ImageView imageView) {
		picasso.load(faviconUrl(source)).into(imageView);
	}

	private class AuthUrlConnectionDownloader extends UrlConnectionDownloader {

		public AuthUrlConnectionDownloader(Context context) {
			super(context);
		}

		@Override
		protected HttpURLConnection openConnection(Uri path) throws IOException {
			HttpURLConnection connection = super.openConnection(path);
			String auth = account.getUsername() + ":" + account.getPassword();
			String authHeader = "Basic " + Base64.encodeToString(auth.getBytes(Charset.forName("US-ASCII")), Base64.DEFAULT);
			connection.setRequestProperty("Authorization", authHeader);
			return connection;
		}
	}
}
