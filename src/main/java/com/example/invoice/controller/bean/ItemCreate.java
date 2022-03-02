package com.example.invoice.controller.bean;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Value;
import org.springframework.validation.annotation.Validated;

@Validated
@Builder
@Value
public class ItemCreate
{
	@NotNull
	@NotBlank
	@Size(min = 2, max = 50, message
			= "Product name must be between 2 and 50 characters")
	private String productName;
	
	@NotNull
	@Min(value = 0, message = "The price must be positive")
	private Double unitPrice;
	
	@NotNull
	@Min(value = 1, message = "The quantity must be positive")
	private Integer quantity;
}
