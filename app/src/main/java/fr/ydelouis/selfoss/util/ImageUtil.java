package fr.ydelouis.selfoss.util;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.auth.AccountHandle;
import com.androidquery.auth.BasicHandle;
import com.androidquery.callback.BitmapAjaxCallback;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import fr.ydelouis.selfoss.account.SelfossAccount;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.Source;

@EBean
public class ImageUtil {

	@Bean protected SelfossAccount account;
	@RootContext protected Context context;

	public String faviconUrl(String favicon) {
		return getScheme() + account.getUrl() + "/favicons/" + favicon;
	}

	private String getScheme() {
		return account.useHttps() ? "https" : "http";
	}

	public String faviconUrl(Article article) {
		return faviconUrl(article.getIcon());
	}

	public String faviconUrl(Source source) {
		return faviconUrl(source.getIcon());
	}

	public void loadFavicon(Article article, BitmapAjaxCallback callback) {
		new AQuery(new ImageView(context)).auth(accountHandle()).image(faviconUrl(article), true, true, 0, 0, callback);
	}

	public void loadFavicon(Article article, View view, int imageViewId) {
		new AQuery(view).id(imageViewId).auth(accountHandle()).image(faviconUrl(article));
	}

	public void loadFavicon(Source source, View view, int imageViewId) {
		new AQuery(view).id(imageViewId).auth(accountHandle()).image(faviconUrl(source));
	}

	public void loadImage(String imageUrl, BitmapAjaxCallback callback) {
		new AQuery(new ImageView(context)).auth(accountHandle()).image(imageUrl, true, true, 200, 0, callback);
	}

	public void loadImage(Article article, View view, int imageViewId) {
		if (article.hasImage()) {
			new AQuery(view).id(imageViewId).auth(accountHandle()).image(article.getImageUrl());
		}
	}

	public void loadImage(Article article, View view, int imageViewId, int imageWidth, BitmapAjaxCallback callback) {
		if (article.hasImage()) {
			new AQuery(view).id(imageViewId).auth(accountHandle()).image(article.getImageUrl(), true, true, imageWidth, 0, callback);
		}
	}

	private AccountHandle accountHandle() {
		if (account.requireAuth()) {
			return new BasicHandle(account.getUsername(), account.getPassword());
		}
		return null;
	}
}
