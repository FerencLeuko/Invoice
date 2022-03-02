package com.example.invoice.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import com.example.invoice.controller.bean.InvoiceBean;
import com.example.invoice.controller.bean.InvoiceCreate;
import com.example.invoice.exception.ErrorResponse;
import com.example.invoice.exception.InvoiceNotFoundException;
import com.example.invoice.exception.InvoiceValidationException;
import com.example.invoice.exception.UnsupportedParameterException;
import com.example.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InvoiceController implements InvoiceApi
{
	private static final String PATH = "localhost:8081";
	
	public List<InvoiceBean> getAllInvoices( )
	{
		return _invoiceService.getAllInvoices();
	}
	
	public List<InvoiceBean> getInvoices(Integer from, Integer to )
	{
		return _invoiceService.getInvoices( from, to );
	}
	
	public InvoiceBean getInvoice( Integer invoiceId )
	{
		return _invoiceService.getInvoice( invoiceId );
	}
	
	public ResponseEntity<InvoiceBean> postInvoice( InvoiceCreate invoiceCreate, BindingResult errors ) throws RuntimeException
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
