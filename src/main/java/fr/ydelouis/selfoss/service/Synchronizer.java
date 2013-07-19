package fr.ydelouis.selfoss.service;

import android.app.IntentService;
import android.content.Intent;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.rest.RestService;

import java.util.ArrayList;
import java.util.List;

import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.model.DatabaseHelper;
import fr.ydelouis.selfoss.rest.SelfossRest;

@EService
public class Synchronizer extends IntentService {

	public static final String ACTION_SYNC_TAGS = "fr.ydelouis.selfoss.ACTION_SYNC_TAGS";
	public static final String EXTRA_TAGS = "tags";

	@RestService protected SelfossRest selfossRest;
	@OrmLiteDao(helper = DatabaseHelper.class, model = Tag.class)
	protected RuntimeExceptionDao<Tag, String> tagDao;

	public Synchronizer() {
		super(Synchronizer.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (ACTION_SYNC_TAGS.equals(intent.getAction())) {
			syncTags();
		} else {
			syncAll();
		}
	}

	private void syncAll() {
		syncTags();
	}

	private void syncTags() {
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
		sendBroadcast(intent);
	}
}
