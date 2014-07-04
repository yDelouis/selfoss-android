package fr.ydelouis.selfoss.rest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.rest.RestService;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.model.ArticleSyncActionDao;
import fr.ydelouis.selfoss.model.DatabaseHelper;
import fr.ydelouis.selfoss.sync.ArticleSyncAction;
import fr.ydelouis.selfoss.util.ArticleContentParser;

@EBean
public class SelfossRestWrapper {

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
        List<String> imageUrls = parser.extractImageUrls();
        for (String imageUrl : imageUrls) {
            Bitmap bitmap = new BitmapDownloader().load(imageUrl);
            if (bitmap != null && !bitmapIsEmpty(bitmap)) {
                article.setImageUrl(imageUrl);
                return;
            }
        }
    }

    private boolean bitmapIsEmpty(Bitmap bitmap) {
        if (bitmap.getWidth() < 50 || bitmap.getHeight() < 50) {
            return true;
        }
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                if (bitmap.getPixel(x, y) != Color.WHITE) {
                    return false;
                }
            }
        }
        return true;
    }

    private class BitmapDownloader {
        private Bitmap bitmap;

        public Bitmap load(String imageUrl) {
            final CountDownLatch latch = new CountDownLatch(1);
            aQuery.image(imageUrl, true, true, 200, 0, new BitmapAjaxCallback() {
                @Override
                protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                    bitmap = bm;
                    latch.countDown();
                }
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }

}
