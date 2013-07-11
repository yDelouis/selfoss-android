package fr.ydelouis.selfoss.api.rest;

import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

@Rest(converters = { MappingJacksonHttpMessageConverter.class }, interceptors = { SelfossApiInterceptor.class })
public interface ArticleRest {

}
