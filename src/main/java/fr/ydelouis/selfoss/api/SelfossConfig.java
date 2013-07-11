package fr.ydelouis.selfoss.api;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(SharedPref.Scope.APPLICATION_DEFAULT)
public interface SelfossConfig {

	String url();
	boolean requireAuth();
	String username();
	String password();
}
