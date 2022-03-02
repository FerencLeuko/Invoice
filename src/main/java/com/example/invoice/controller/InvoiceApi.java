package com.example.invoice.controller;

import java.util.List;

import javax.validation.Valid;

import com.example.invoice.controller.bean.InvoiceBean;
import com.example.invoice.controller.bean.InvoiceCreate;
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
	List<InvoiceBean> getAllInvoices( );
	
	@GetMapping( "/invoices" )
	@ResponseBody
	public List<InvoiceBean> getInvoices(@RequestParam( value = "from" ) Integer from,
			@RequestParam( value = "to" ) Integer to );
	
	@GetMapping( "/invoice" )
	@ResponseBody
	public InvoiceBean getInvoice(@RequestParam( value = "invoiceId" ) Integer invoiceId);
	
	@PostMapping( "/invoice" )
	@ResponseBody
	public ResponseEntity<InvoiceBean> postInvoice(@Valid @RequestBody InvoiceCreate invoiceCreate,
			BindingResult errors ) throws RuntimeException;
}
