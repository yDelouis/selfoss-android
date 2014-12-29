package fr.ydelouis.selfoss.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import fr.ydelouis.selfoss.config.model.Config;
import fr.ydelouis.selfoss.config.model.ConfigManager;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.Source;
import fr.ydelouis.selfoss.rest.SelfossApiRequestFactory;

@EBean
public class SelfossImageLoader implements ImageDownloader {

	@Bean protected ConfigManager configManager;
	@RootContext protected Context context;
	@Bean protected SelfossApiRequestFactory requestFactory;
	private ImageLoader loader;

	@AfterInject
	protected void init() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.displayer(new FadeInBitmapDisplayer(500, true, false, false))
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.defaultDisplayImageOptions(options)
				.imageDownloader(this)
				.build();
		loader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
		if (!loader.isInited()) {
			loader.init(config);
		}
	}

	public String faviconUrl(String favicon) {
		Config config = configManager.get();
		String url = config != null ? config.getUrl() : "";
		return getScheme() + url + "/favicons/" + favicon;
	}

	private String getScheme() {
		Config config = configManager.get();
		boolean useHttps = config != null && config.useHttps();
		return useHttps ? "https://" : "http://";
	}

	public String faviconUrl(Article article) {
		return faviconUrl(article.getIcon());
	}

	public String faviconUrl(Source source) {
		return faviconUrl(source.getIcon());
	}

	public void displayFavicon(Article article, ImageLoadingListener listener) {
		loader.loadImage(faviconUrl(article), listener);
	}

	public void displayFavicon(Article article, ImageView imageView) {
		loader.displayImage(faviconUrl(article), imageView);
	}

	public void displayFavicon(Source source, ImageView imageView) {
		loader.displayImage(faviconUrl(source), imageView);
	}

	public void displayImage(Article article, ImageView image) {
		loader.displayImage(article.getImageUrl(), image);
	}

	public Bitmap loadImageSync(String imageUrl) {
		return loader.loadImageSync(imageUrl);
	}

	@Override
	public InputStream getStream(String imageUrl, Object extra) throws IOException {
		Config config = configManager.get();
		Log.d("Plop", imageUrl);
		URL url = new URL(imageUrl);
		URLConnection http = url.openConnection();
		if (config != null && config.requireAuth()) {
			String auth = config.getUsername() + ":" + config.getPassword();
			String authHeader = "Basic " + Base64.encodeToString(auth.getBytes(Charset.forName("US-ASCII")), Base64.DEFAULT);
			http.setRequestProperty("Authorization", authHeader);
		}
		return http.getInputStream();
	}
}
