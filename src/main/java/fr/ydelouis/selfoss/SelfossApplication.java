package fr.ydelouis.selfoss;

import android.app.Application;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.sharedpreferences.Pref;

import fr.ydelouis.selfoss.api.SelfossConfig_;

@EApplication
public class SelfossApplication extends Application {

	@Pref public static SelfossConfig_ selfossConfig;

}
