package fr.ydelouis.selfoss.rest;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.List;

import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.Success;
import fr.ydelouis.selfoss.entity.Tag;

@Rest(converters = { MappingJackson2HttpMessageConverter.class },
		interceptors = { SelfossApiInterceptor.class },
		requestFactory = SelfossApiRequestFactory.class)
public interface SelfossRest {

	@Get("/login")
	Success login();

	@Get("/tags")
	List<Tag> listTags();

	@Get("/items?offset={offset}&items={count}")
	List<Article> listArticles(int offset, int count);

	@Get("/items?type=unread&offset={offset}&items={count}")
	List<Article> listUnreadArticles(int offset, int count);

	@Get("/items?type=starred&offset={offset}&items={count}")
	List<Article> listFavoriteArticles(int offset, int count);
}
