package com.lwp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lwp.annotation.ResponseBodyJson;
import com.lwp.entity.User;

@RestController
public class TestController {
	
	/**
	 * 过滤User id属性 和 UserInfo对象的number属性
	 * @return
	 */
	@ResponseBodyJson(type=User.class,excludes= {"id","userInfo.number"})
	@RequestMapping("/filter")
	public User filter() {
		return new User();
	}
	
	
	@RequestMapping("/test")
	public User test() {
		return new User();
	}
}
