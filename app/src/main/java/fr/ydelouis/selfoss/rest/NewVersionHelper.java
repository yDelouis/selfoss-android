package fr.ydelouis.selfoss.rest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.model.ArticleDao;
import fr.ydelouis.selfoss.model.DatabaseHelper;

@EBean
public class NewVersionHelper {

	@RootContext
	protected Activity activity;
	@OrmLiteDao(helper = DatabaseHelper.class)
	protected ArticleDao articleDao;
	@Pref
	protected NewVersionHelper_.Prefs_ prefs;

	public void showNewVersionMessageIfNeeded() {
		if (activity != null) {
			showNewVersion2_11();
		}
	}

	private void showNewVersion2_11() {
		if (!prefs.v2_11().get() && articleDao.queryForCount() > 0 && articleDao.queryForLatestUpdateTime() == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setMessage(R.string.newVersion_2_11_message);
			builder.setTitle(R.string.newVersion_title);
			builder.setPositiveButton(R.string.remindMeLater, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					dialogInterface.dismiss();
				}
			});
			builder.setNegativeButton(R.string.gotIt, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					dialogInterface.dismiss();
					prefs.v2_11().put(true);
				}
			});
			builder.show();
		}
	}

	@SharedPref
	public interface Prefs {
		boolean v2_11();
	}
}
