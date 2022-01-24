package com.example.invoice.service.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Currency {
	
	@JsonProperty("success")
	private String success;
	
	@JsonProperty("timestamp")
	private String timestamp;
	
	@JsonProperty("source")
	private String source;
	
	@JsonProperty("quotes")
	private Quotes quotes;
	
}
