package com.example.invoice.service.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Quotes
{
	@JsonProperty("USDEUR")
		double USDEUR;
	
	@JsonProperty("USDHUF")
		double USDHUF;
}
