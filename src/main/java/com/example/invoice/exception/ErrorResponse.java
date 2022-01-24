package com.example.invoice.exception;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonRootName("error")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorResponse
{
	private String message;
}
