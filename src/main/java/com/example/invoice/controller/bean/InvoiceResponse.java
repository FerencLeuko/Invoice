package com.example.invoice.controller.bean;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class InvoiceResponse
{
	private Integer id;
	
	private String customerName;
	
	private LocalDate issueDate;
	
	private LocalDate dueDate;
	
	private Double totalPrice;
	
	private Double totalEuroPrice;
	
	private String comment;
	
	private List<ItemResponse> items;
}
