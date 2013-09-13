package fr.ydelouis.selfoss.util;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import fr.ydelouis.selfoss.account.SelfossAccount;
import fr.ydelouis.selfoss.entity.Article;

@EBean
public class SelfossUtil {

	@Bean protected SelfossAccount account;

	public String faviconUrl(String favicon) {
		return "http://" + account.getUrl() + "/favicons/" + favicon;
	}

	public String faviconUrl(Article article) {
		return faviconUrl(article.getIcon());
	}
}
