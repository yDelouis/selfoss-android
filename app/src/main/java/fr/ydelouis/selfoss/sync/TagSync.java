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

import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.model.DatabaseHelper;
import fr.ydelouis.selfoss.rest.SelfossRest;

@EBean
public class TagSync {

	public static final String ACTION_SYNC_TAGS = "fr.ydelouis.selfoss.ACTION_SYNC_TAGS";
	public static final String EXTRA_TAGS = "tags";

	@RootContext protected Context context;
	@RestService protected SelfossRest selfossRest;
	@OrmLiteDao(helper = DatabaseHelper.class)
	protected RuntimeExceptionDao<Tag, String> tagDao;

	public void performSync() {
		List<Tag> serverTags = selfossRest.listTags();
		List<Tag> databaseTags = tagDao.queryForAll();
		for (Tag tag : serverTags) {
			tagDao.createOrUpdate(tag);
			databaseTags.remove(tag);
		}
		tagDao.delete(databaseTags);
		sendTagBroadcast(serverTags);
	}

	private void sendTagBroadcast(List<Tag> tags) {
		Intent intent = new Intent(ACTION_SYNC_TAGS);
		intent.putExtra(EXTRA_TAGS, new ArrayList<Tag>(tags));
		context.sendBroadcast(intent);
	}

}
