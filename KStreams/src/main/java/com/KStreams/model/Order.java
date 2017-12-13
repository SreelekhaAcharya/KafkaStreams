package com.KStreams.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="Order_Detail")
public class Order {
	
	@Id
	private Long orderId;
	private Long prflId;
	private String orderDesc;
	
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
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
