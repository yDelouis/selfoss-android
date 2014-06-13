package fr.ydelouis.selfoss.sync;

import android.content.Context;
import android.content.Intent;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.rest.RestService;

import java.util.ArrayList;
import java.util.List;

import fr.ydelouis.selfoss.entity.Source;
import fr.ydelouis.selfoss.model.DatabaseHelper;
import fr.ydelouis.selfoss.rest.SelfossRest;

@EBean
public class SourceSync {

	public static final String ACTION_SYNC_SOURCES = "fr.ydelouis.selfoss.ACTION_SYNC_SOURCES";
	public static final String EXTRA_SOURCES = "sources";

	@RootContext protected Context context;
	@RestService protected SelfossRest selfossRest;
	@OrmLiteDao(helper = DatabaseHelper.class)
	protected RuntimeExceptionDao<Source, String> sourceDao;

	public void performSync() {
		List<Source> serverSources = selfossRest.listSources();
		List<Source> databaseSources = sourceDao.queryForAll();
		for (Source source : serverSources) {
			sourceDao.createOrUpdate(source);
			databaseSources.remove(source);
		}
		sourceDao.delete(databaseSources);
		sendSourceBroadcast(serverSources);
	}

	private void sendSourceBroadcast(List<Source> sources) {
		Intent intent = new Intent(ACTION_SYNC_SOURCES);
		intent.putExtra(EXTRA_SOURCES, new ArrayList<Source>(sources));
		context.sendBroadcast(intent);
	}

}
