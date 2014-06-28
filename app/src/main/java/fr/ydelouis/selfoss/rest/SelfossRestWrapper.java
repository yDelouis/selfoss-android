package fr.ydelouis.selfoss.rest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.rest.RestService;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

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
            Bitmap bitmap = loadBitmap(imageUrl);
            if (bitmap != null && !bitmapIsEmpty(bitmap)) {
                article.setImageUrl(imageUrl);
                return;
            }
        }
    }

    private Bitmap loadBitmap(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            URLConnection connection = url.openConnection();
            return BitmapFactory.decodeStream(connection.getInputStream());
        } catch (IOException e) {
            return null;
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

}
