package fr.ydelouis.selfoss.rest;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(SharedPref.Scope.APPLICATION_DEFAULT)
public interface SelfossConfig {

	String url();
	boolean trustAllCertificates();
	boolean requireAuth();
	String username();
	String password();
}
