package com.example.invoice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UnsupportedParameterException extends IllegalArgumentException
{
	private String message;
}
