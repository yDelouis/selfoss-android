package fr.ydelouis.selfoss.rest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.rest.RestService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import fr.ydelouis.selfoss.BuildConfig;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.model.ArticleSyncActionDao;
import fr.ydelouis.selfoss.model.DatabaseHelper;
import fr.ydelouis.selfoss.sync.ArticleSyncAction;
import fr.ydelouis.selfoss.util.ArticleContentParser;

@EBean
public class SelfossRestWrapper {

    private static final String TAG = "Selfoss Image Loading";
    private static boolean LOG_IMAGE_REQUEST = BuildConfig.DEBUG && true;

    @RestService
    protected SelfossRest rest;
    @OrmLiteDao(helper = DatabaseHelper.class)
    protected ArticleSyncActionDao articleSyncActionDao;
    @RootContext
    protected Context context;
    private AQuery aQuery;

    @AfterInject
    protected void init() {
        aQuery = new AQuery(new ImageView(context));
    }

    public List<Article> listArticles(int offset, int count) {
        return preProcess(rest.listArticles(offset, count));
    }

	public List<Article> listUpdatedArticles(int offset, int count, String updateTime) {
		String encodedUpdateTime = updateTime;
		try {
			encodedUpdateTime = URLEncoder.encode(updateTime, "UTF-8");
		} catch (UnsupportedEncodingException ignored) {}
		return preProcess(rest.listUpdatedArticles(offset, count, encodedUpdateTime));
	}

	public List<Article> listArticles(Tag tag, int offset, int count) {
        return preProcess(rest.listArticles(tag, offset, count));
    }

    public List<Article> listArticles(int sourceId, int offset, int count) {
        return preProcess(rest.listArticles(sourceId, offset, count));
    }

    public List<Article> listArticles(ArticleType type, Tag tag, int offset, int count) {
        return preProcess(rest.listArticles(type, tag, offset, count));
    }

    public List<Article> listArticles(ArticleType type, int sourceId, int offset, int count) {
        return preProcess(rest.listArticles(type, sourceId, offset, count));
    }

    public List<Article> listArticles(ArticleType type, int offset, int count) {
        return preProcess(rest.listArticles(type, offset, count));
    }

    public List<Article> listUnreadArticles(int offset, int count) {
        return preProcess(rest.listUnreadArticles(offset, count));
    }

    public List<Article> listStarredArticles(int offset, int count) {
        return preProcess(rest.listStarredArticles(offset, count));
    }

    private List<Article> preProcess(List<Article> articles) {
        if (articles != null) {
            for (Article article : articles) {
                preProcess(article);
            }
        }
        return articles;
    }

    private void preProcess(Article article) {
        applySyncAction(article);
        extractImage(article);
    }

    private void applySyncAction(Article article) {
        ArticleSyncAction syncAction = articleSyncActionDao.queryForArticle(article);
        if (syncAction != null) {
            syncAction.getAction().execute(article);
        }
    }

    private void extractImage(Article article) {
        ArticleContentParser parser = new ArticleContentParser(article);
        List<String> imageUrls = parser.getImagesUrls();
        for (String imageUrl : imageUrls) {
            if (new BitmapDownloader().isDisplayable(imageUrl)) {
                article.setImageUrl(imageUrl);
                return;
            }
        }
    }

    private class BitmapDownloader {
        private Bitmap bitmap;
        private AjaxStatus ajaxStatus;

        public Bitmap get() {
            return bitmap;
        }

        public BitmapDownloader load(String imageUrl) {
            final CountDownLatch latch = new CountDownLatch(1);
            aQuery.image(imageUrl, true, true, 200, 0, new BitmapAjaxCallback() {
                @Override
                protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                    bitmap = bm;
                    ajaxStatus = status;
                    latch.countDown();
                }
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return this;
        }

        public boolean isDisplayable(String imageUrl) {
            load(imageUrl);
            boolean isDisplayable = isDisplayable();
            log(imageUrl, isDisplayable);
            return isDisplayable;
        }

        private boolean isDisplayable() {
            if (bitmap == null) {
                return false;
            }
            if (bitmap.getWidth() < 50 || bitmap.getHeight() < 50) {
                return false;
            }
            for (int x = 0; x < bitmap.getWidth(); x++) {
                for (int y = 0; y < bitmap.getHeight(); y++) {
                    if (bitmap.getPixel(x, y) != Color.WHITE) {
                        return true;
                    }
                }
            }
            return false;
        }

        private void log(String imageUrl, boolean isDisplayable) {
            if (LOG_IMAGE_REQUEST) {
                String source = ajaxStatus.getSource() == AjaxStatus.NETWORK ? "network" : "cache";
                String valid = isDisplayable ? "Ok" : "invalid";
                Log.i(TAG, imageUrl + " from " + source + " : " + valid);
            }
        }
    }

}
