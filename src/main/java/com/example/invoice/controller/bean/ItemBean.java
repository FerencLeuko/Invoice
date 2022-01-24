package com.example.invoice.controller.bean;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ItemBean
{
	private String productName;
	
	private Double unitPrice;
	
	private Integer quantity;
	
	private Double itemTotalPrice;
	
	private Double itemTotalEuroPrice;
}
