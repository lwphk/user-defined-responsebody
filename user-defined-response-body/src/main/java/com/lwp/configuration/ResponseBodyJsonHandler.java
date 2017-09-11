package com.lwp.configuration;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.lwp.annotation.ResponseBodyJson;

@Component
public class ResponseBodyJsonHandler implements HandlerMethodReturnValueHandler ,InitializingBean{
	
	/**
	 * 实体类泛型字段真实类的参数下标
	 * Map<C1,C2> map;   
	 * real C1 ==> genericityRealClassIndex.put(Map.class.getName(), 0);  
	 * real C2 ==> genericityRealClassIndex.put(Map.class.getName(), 1);
	 */
	private Map<String, Integer> genericityRealClassIndex;   
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if(genericityRealClassIndex == null) {
			genericityRealClassIndex = new HashMap<>();
			genericityRealClassIndex.put(Set.class.getName(), 0);
			genericityRealClassIndex.put(List.class.getName(), 0);
			genericityRealClassIndex.put(Map.class.getName(), 1);
		}
	}

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		ResponseBodyJson annotation = returnType.getMethodAnnotation(ResponseBodyJson.class);
		if(annotation == null) {
			return false;
		}
		if(annotation.excludes().length > 0 || annotation.includes().length > 0) {
			return true;
		}
		return false;
	}

	@Override
	public void handleReturnValue(Object returnValue,
			MethodParameter returnType, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest) throws Exception {
		mavContainer.setRequestHandled(true);
		ResponseBodyJson annotation = returnType.getMethodAnnotation(ResponseBodyJson.class);
		String[] excludes = annotation.excludes();
		String[] includes = annotation.includes();
		Map<String, Class<?>> clzzMap = new HashMap<>();
		SimplePropertyPreFilter[] propertyFilterArr = null;
		clzzMap.put(annotation.type().getName(), annotation.type());
		
		if(excludes.length > 0) {
			propertyFilterArr = getPropertyFilters(clzzMap, excludes, annotation.type(),true);
			
		}else {
			propertyFilterArr = getPropertyFilters(clzzMap, includes, annotation.type(),false);
		}
		
		responseWrite(returnValue, webRequest,propertyFilterArr);
		
	}
	
	/**
	 * 获取Json过滤器数组
	 * @param clzzMap	过滤对象中包含的类
	 * @param propertys   属性字段数组
	 * @param annotationType  注解的Type属性,根对象
	 * @param isExclude   属性字段数组  是否是exclude
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	private SimplePropertyPreFilter[] getPropertyFilters(Map<String, Class<?>> clzzMap,String[] propertys,Class<?> annotationType,boolean isExclude) throws NoSuchFieldException, SecurityException {
		Map<String, Set<String>> propertysMap = new HashMap<>();
		propertysMap.put(annotationType.getName(), new HashSet<>());
		for (String property : propertys) {
			if(property.contains(".")) {
				int lastIndexOf = property.lastIndexOf(".");
				String className = property.substring(0, lastIndexOf);
				String attribute = property.substring(lastIndexOf+1,property.length());
		
				if(clzzMap.containsKey(className)) {
					propertysMap.get(className).add(attribute);
				}else {
					Class<?> clz = getRealClass(annotationType, className);
					clzzMap.put(className, clz);
					propertysMap.put(className, new HashSet<>(Arrays.asList(attribute)));
				}
			}else {
				propertysMap.get(annotationType.getName()).add(property);
				
			}
		}
		SimplePropertyPreFilter[] arr = new SimplePropertyPreFilter[clzzMap.size()];
		int i = 0;
		for (Entry<String, Class<?>> clzz : clzzMap.entrySet()) {
			SimplePropertyPreFilter s = new SimplePropertyPreFilter(clzz.getValue());
			if(isExclude){
				s.getExcludes().addAll(propertysMap.get(clzz.getKey()));
			}else {
				s.getIncludes().addAll(propertysMap.get(clzz.getKey()));
			}
			arr[i++] = s;
		}
		return arr;
	}
	/**
	 * 根据.语法获取真实全类名
	 * @param clz
	 * @param className
	 * @return
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	private Class<?> getRealClass(Class<?> clz,String className) throws NoSuchFieldException, SecurityException{
		String[] arr = className.split("\\.");
		for (String str : arr) {
			Field field = clz.getDeclaredField(str);
			clz = field.getType();
			//如果属性是泛型集合
			if(genericityRealClassIndex.containsKey(clz.getName())) {
				clz = getGenericityRealClass(field);
			}
		}
		return clz;
	}
	
	/**
	 * 获取泛型真正的class 类型
	 * @param field
	 * @return
	 */
	public Class<?> getGenericityRealClass(Field field){
		Type type = field.getGenericType();
		ParameterizedType pt = null;
		//如果是泛型参数
		while(type instanceof ParameterizedType) {
			pt = (ParameterizedType) type;
			int index = getTypeIndex(pt);
			type = pt.getActualTypeArguments()[index];
		}
		if(pt == null) {
			return Empty.class;
		}
		return (Class<?>)pt.getActualTypeArguments()[getTypeIndex(pt)];
	}
	 
	public  int getTypeIndex(ParameterizedType pt) {
		Class<?> clz = (Class<?>)pt.getRawType();
		return genericityRealClassIndex.get(clz.getName());
	}
	
	private void responseWrite(Object returnValue,NativeWebRequest webRequest,SerializeFilter[] serializeFilter) throws IOException {
		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
		response.setCharacterEncoding("UTF-8");  
	    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		if(serializeFilter == null) {
			response.getWriter().write(JSON.toJSONString(returnValue,SerializerFeature.WriteNullStringAsEmpty,SerializerFeature.WriteNullListAsEmpty));
		}else {
			
			response.getWriter().write(JSON.toJSONString(returnValue,serializeFilter,SerializerFeature.WriteNullStringAsEmpty,SerializerFeature.WriteNullListAsEmpty));
		}
	}

	class Empty{
		
	}
	
}
