package com.example.invoice.controller;

import java.util.List;

import javax.validation.Valid;

import com.example.invoice.controller.bean.InvoiceResponse;
import com.example.invoice.controller.bean.InvoiceRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

public interface InvoiceApi
{
	@GetMapping( "/allInvoices" )
	@ResponseBody
	ResponseEntity<List<InvoiceResponse>> getAllInvoices( );
	
	@GetMapping( "/invoices" )
	@ResponseBody
	ResponseEntity<List<InvoiceResponse>> getInvoices(@RequestParam( value = "from" ) Integer from,
			@RequestParam( value = "to" ) Integer to );
	
	@GetMapping( "/invoice" )
	@ResponseBody
	ResponseEntity<InvoiceResponse> getInvoice(@RequestParam( value = "invoiceId" ) Integer invoiceId);
	
	@PostMapping( "/invoice" )
	@ResponseBody
	public ResponseEntity<InvoiceResponse> postInvoice(@Valid @RequestBody InvoiceRequest invoiceRequest,
			BindingResult errors ) throws RuntimeException;
}
