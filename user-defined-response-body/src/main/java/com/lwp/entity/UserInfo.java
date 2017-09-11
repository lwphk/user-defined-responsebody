package com.lwp.entity;

public class UserInfo {

	private Integer userId;
	
	
	private Integer age;
	
	private String realName;
	
	private String number;
	
	

	public UserInfo() {
		super();
		this.age = 10;
		this.userId = 1;
		this.realName = "json";
		this.number ="100254123199306151";
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	
}
