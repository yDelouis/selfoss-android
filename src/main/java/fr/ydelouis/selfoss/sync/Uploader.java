package fr.ydelouis.selfoss.sync;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.rest.RestService;

import fr.ydelouis.selfoss.model.ArticleSyncActionDao;
import fr.ydelouis.selfoss.model.DatabaseHelper;
import fr.ydelouis.selfoss.rest.SelfossRest;

@EBean
public class Uploader {

	@OrmLiteDao(helper = DatabaseHelper.class, model = ArticleSyncAction.class)
	protected ArticleSyncActionDao articleSyncActionDao;
	@RestService
	protected SelfossRest selfossRest;

	public void performSync() {
		syncArticle();
	}

	private void syncArticle() {
		syncMarkRead();
		syncOtherActions();
	}

	private void syncMarkRead() {
		String ids = "";
		for (ArticleSyncAction markReadAction : articleSyncActionDao.queryForMarkRead()) {
			ids += "ids="+markReadAction.getArticleId()+"&";
		}
		if (!ids.isEmpty()) {
			ids = ids.substring(0, ids.length()-1);
			selfossRest.markRead(ids);
			articleSyncActionDao.deleteMarkRead();
		}
	}

	private void syncOtherActions() {
		for (ArticleSyncAction syncAction : articleSyncActionDao.queryForAll()) {
			syncAction.execute(selfossRest);
			articleSyncActionDao.delete(syncAction);
		}
	}
}
