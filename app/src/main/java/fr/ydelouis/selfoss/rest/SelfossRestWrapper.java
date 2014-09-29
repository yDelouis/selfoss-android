package fr.ydelouis.selfoss.rest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.rest.RestService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.model.ArticleSyncActionDao;
import fr.ydelouis.selfoss.model.DatabaseHelper;
import fr.ydelouis.selfoss.sync.ArticleSyncAction;
import fr.ydelouis.selfoss.util.ArticleContentParser;
import fr.ydelouis.selfoss.util.SelfossImageLoader;

@EBean
public class SelfossRestWrapper {

    @RestService
    protected SelfossRest rest;
    @OrmLiteDao(helper = DatabaseHelper.class)
    protected ArticleSyncActionDao articleSyncActionDao;
    @Bean protected SelfossImageLoader imageLoader;

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
            if (isImageDisplayable(imageUrl)) {
                article.setImageUrl(imageUrl);
                return;
            }
        }
    }

	private boolean isImageDisplayable(String imageUrl) {
		Bitmap bitmap = imageLoader.loadImageSync(imageUrl);
		return isDisplayable(bitmap);
	}

	private boolean isDisplayable(Bitmap bitmap) {
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
}
