package com.KStreams.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="Order_History_Detail")
public class OrderHistory {
	
	
	private Long prflId;
	private String orderDesc;
	
	public Long getPrflId() {
		return prflId;
	}
	public void setPrflId(Long prflId) {
		this.prflId = prflId;
	}
	public String getOrderDesc() {
		return orderDesc;
	}
	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}
	
	
	

}
