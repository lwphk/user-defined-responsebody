package com.lwp.entity;

import java.util.Arrays;
import java.util.List;

public class User {

	private Integer id;
	
	private String name;
	
	private UserInfo userInfo;
	
	private List<Card> cardList;
	
	
	
	public User() {
		super();
		this.id = 1;
		this.name = "response json";
		this.userInfo = new UserInfo();
		this.cardList = Arrays.asList(new Card(1,"1000000000",1),new Card(2,"2000000000",2),new Card(3,"3000000000",1));
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public List<Card> getCardList() {
		return cardList;
	}

	public void setCardList(List<Card> cardList) {
		this.cardList = cardList;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
