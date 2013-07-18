package fr.ydelouis.selfoss.rest;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import fr.ydelouis.selfoss.entity.Success;

@Rest(converters = { MappingJackson2HttpMessageConverter.class },
		interceptors = { SelfossApiInterceptor.class },
		requestFactory = SelfossApiRequestFactory.class)
public interface SelfossRest {

	@Get("/login")
	Success login();

}
