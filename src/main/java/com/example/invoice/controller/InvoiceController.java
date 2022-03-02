package com.example.invoice.controller;

import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.ValidationException;

import com.example.invoice.controller.bean.InvoiceBean;
import com.example.invoice.controller.bean.InvoiceCreate;
import com.example.invoice.exception.ErrorResponse;
import com.example.invoice.exception.InvoiceNotFoundException;
import com.example.invoice.exception.InvoiceValidationException;
import com.example.invoice.exception.UnsupportedParameterException;
import com.example.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Controller
@RequiredArgsConstructor
public class InvoiceController
{
	private static final String PATH = "localhost:8081";
	
	@GetMapping( "/allInvoices" )
	@ResponseBody
	public List<InvoiceBean> getAllInvoices( )
	{
		return _invoiceService.getAllInvoices();
	}
	
	@GetMapping( "/invoices" )
	@ResponseBody
	public List<InvoiceBean> getInvoices(@RequestParam( value = "from" ) Integer from,
			@RequestParam( value = "to" ) Integer to )
	{
		return _invoiceService.getInvoices( from, to );
	}
	
	@GetMapping( "/invoice" )
	@ResponseBody
	public InvoiceBean getInvoice(@RequestParam( value = "invoiceId" ) Integer invoiceId)
	{
		return _invoiceService.getInvoice( invoiceId );
	}
	
	@PostMapping( "/invoice" )
	@ResponseBody
	public ResponseEntity<InvoiceBean> postInvoice(@Valid @RequestBody InvoiceCreate invoiceCreate, BindingResult errors ) throws RuntimeException
	{
		if (errors.hasErrors()) {
			throw new ValidationException( String.valueOf( errors.getAllErrors()
					.stream()
					.map( InvoiceValidationException::getValidationMessage)
					.collect( Collectors.toList())));
		}
		
		InvoiceBean invoice = _invoiceService.createInvoice( invoiceCreate );
		URI location = URI.create( PATH + "/invoice/?invoiceId=" + invoice.getId() );
		
		return ResponseEntity.created( location )
				.body( invoice );
	}
	
	@ExceptionHandler(  { UnsupportedParameterException.class } )
	public ResponseEntity<ErrorResponse> handleArgumentExceptions( UnsupportedParameterException e )
	{
		return new ResponseEntity<ErrorResponse>( new ErrorResponse( e.getMessage() ), HttpStatus.BAD_REQUEST );
	}
	
	@ExceptionHandler(  { InvoiceNotFoundException.class } )
	public ResponseEntity<ErrorResponse> handleArgumentExceptions( InvoiceNotFoundException e )
	{
		return new ResponseEntity<ErrorResponse>( new ErrorResponse( e.getMessage() ), HttpStatus.BAD_REQUEST );
	}
	
	@ExceptionHandler(  { ValidationException.class } )
	public ResponseEntity<ErrorResponse> handleArgumentExceptions( ValidationException e )
	{
		return new ResponseEntity<ErrorResponse>( new ErrorResponse( e.getMessage() ), HttpStatus.BAD_REQUEST );
	}
	
	@ExceptionHandler(  { RuntimeException.class } )
	public ResponseEntity<ErrorResponse> handleRuntimeExceptions( RuntimeException e )
	{
		return new ResponseEntity<ErrorResponse>( new ErrorResponse( e.getMessage() ), HttpStatus.INTERNAL_SERVER_ERROR );
	}
	
	private final InvoiceService _invoiceService;
}
