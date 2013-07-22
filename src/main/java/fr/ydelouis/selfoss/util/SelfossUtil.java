package fr.ydelouis.selfoss.util;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import fr.ydelouis.selfoss.rest.SelfossConfig_;

@EBean
public class SelfossUtil {

	@Pref protected SelfossConfig_ selfossConfig;

	public String faviconUrl(String favicon) {
		return "http://" + selfossConfig.url().get() + "/favicons/" + favicon;
	}
}
