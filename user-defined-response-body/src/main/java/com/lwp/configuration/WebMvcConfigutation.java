package com.lwp.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.DeferredResultMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@Configuration
@EnableWebMvc
public class WebMvcConfigutation extends WebMvcConfigurerAdapter {

	@Autowired
	private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

	@Autowired
	ResponseBodyJsonHandler responseBodyJsonHandler;

	@PostConstruct
	public void init() {
		final List<HandlerMethodReturnValueHandler> originalHandlers = new ArrayList<HandlerMethodReturnValueHandler>(
				requestMappingHandlerAdapter.getReturnValueHandlers());
		final int deferredPos = obtainValueHandlerPosition(originalHandlers,
				DeferredResultMethodReturnValueHandler.class);
		originalHandlers.add(deferredPos + 1, responseBodyJsonHandler);
		// 添加自己的ReturnValueHandler
		requestMappingHandlerAdapter.setReturnValueHandlers(originalHandlers);
	}

	private int obtainValueHandlerPosition(final List<HandlerMethodReturnValueHandler> originalHandlers,
			Class<?> handlerClass) {
		for (int i = 0; i < originalHandlers.size(); i++) {
			final HandlerMethodReturnValueHandler valueHandler = originalHandlers.get(i);
			if (handlerClass.isAssignableFrom(valueHandler.getClass())) {
				return i;
			}
		}
		return -1;
	}

}
