package fr.ydelouis.selfoss.rest;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.RestClientErrorHandling;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.List;

import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Source;
import fr.ydelouis.selfoss.entity.Success;
import fr.ydelouis.selfoss.entity.Tag;

@Rest(converters = { StringHttpMessageConverter.class, MappingJackson2HttpMessageConverter.class },
		interceptors = { SelfossApiInterceptor.class },
		requestFactory = SelfossApiRequestFactory.class)
public interface SelfossRest extends RestClientErrorHandling {

	@Get("/login")
	Success login();

	@Get("/sources/list")
	List<Source> listSources();

	@Get("/tags")
	List<Tag> listTags();

	@Get("/items?offset={offset}&items={count}")
	List<Article> listArticles(int offset, int count);

	@Get("/items?offset={offset}&items={count}&updatedsince={updateTime}")
	List<Article> listUpdatedArticles(int offset, int count, String updateTime);

	@Get("/items?tag={tag}&offset={offset}&items={count}")
	List<Article> listArticles(Tag tag, int offset, int count);

	@Get("/items?source={sourceId}&offset={offset}&items={count}")
	List<Article> listArticles(int sourceId, int offset, int count);

	@Get("/items?type={type}&tag={tag}&offset={offset}&items={count}")
	List<Article> listArticles(ArticleType type, Tag tag, int offset, int count);

	@Get("/items?type={type}&source={sourceId}&offset={offset}&items={count}")
	List<Article> listArticles(ArticleType type, int sourceId, int offset, int count);

	@Get("/items?type={type}&offset={offset}&items={count}")
	List<Article> listArticles(ArticleType type, int offset, int count);

	@Get("/items?type=unread&offset={offset}&items={count}")
	List<Article> listUnreadArticles(int offset, int count);

	@Get("/items?type=starred&offset={offset}&items={count}")
	List<Article> listStarredArticles(int offset, int count);

	@Post("mark/{articleId}")
	Success markRead(int articleId);

	@Post("unmark/{articleId}")
	Success markUnread(int articleId);

	@Post("mark/")
	Success markRead(String articleIds);

	@Post("starr/{articleId}")
	Success markStarred(int articleId);

	@Post("unstarr/{articleId}")
	Success markUnstarred(int articleId);

}
