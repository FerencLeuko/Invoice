package com.example.invoice.controller.bean;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Value;
import org.springframework.validation.annotation.Validated;

@Builder
@Value
@Validated
public class InvoiceCreate
{
	@NotNull
	@NotBlank
	@Size(min = 2, max = 50, message
			= "Customer name must be between 2 and 50 characters")
	private String customerName;
	
	@NotNull
	private LocalDate issueDate;
	
	@NotNull
	private LocalDate dueDate;
	
	private String comment;
	
	@NotNull
	@NotEmpty
	@Valid
	private List<ItemCreate> items;
}
