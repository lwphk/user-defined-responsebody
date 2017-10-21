package com.lwp.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBodyJson {
	
	/**
	 * 过滤开始的根类型
	 * @return
	 */
	Class<?> type();
	
	String[] includes() default {};
	
	String[] excludes() default {};
	
}
