package com.lwp.entity;

public class Card {

	private Integer id;
	
	private String cardNo;
	
	private Integer type;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Card(Integer id, String cardNo, Integer type) {
		super();
		this.id = id;
		this.cardNo = cardNo;
		this.type = type;
	}

	public Card() {
		super();
	}
	
	
}
