package fr.ydelouis.selfoss.api.rest;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import fr.ydelouis.selfoss.api.entity.Success;

@Rest(converters = { MappingJackson2HttpMessageConverter.class },
		interceptors = { SelfossApiInterceptor.class },
		requestFactory = SelfossApiRequestFactory.class)
public interface LoginRest {

	@Get("/login")
	Success login();

}
