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
import com.example.invoice.persistance.entity.Invoice;
import com.example.invoice.service.InvoiceMapper;
import com.example.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://127.0.0.1:5500/" , "http://127.0.0.1:4200/", "http://127.0.0.1:4201/"} )
public class InvoiceController implements InvoiceApi
{
	private static final String PATH = "localhost:8081";
	
	@Override
	public ResponseEntity<List<InvoiceBean>> getAllInvoices( )
	{
		List<Invoice> allInvoices = _invoiceService.getAllInvoices();
		return ResponseEntity.ok(
				allInvoices.stream()
				.map( i -> _invoiceMapper.invoiceToInvoiceBean( i ) )
				.collect( Collectors.toList()) );
	}
	
	@Override
	public ResponseEntity<List<InvoiceBean>> getInvoices(Integer from, Integer to )
	{
		List<Invoice> invoices = _invoiceService.getInvoices( from, to );
		return ResponseEntity.ok(
				invoices.stream()
						.map( i -> _invoiceMapper.invoiceToInvoiceBean( i ) )
						.collect( Collectors.toList() ) );
	}
	
	@Override
	public ResponseEntity<InvoiceBean> getInvoice( Integer invoiceId )
	{
		Invoice invoice = _invoiceService.getInvoice( invoiceId );
		return ResponseEntity.ok( _invoiceMapper.invoiceToInvoiceBean( invoice ) );
	}
	
	@Override
	public ResponseEntity<InvoiceBean> postInvoice( InvoiceCreate invoiceCreate, BindingResult errors ) throws RuntimeException
	{
		if (errors.hasErrors()) {
			throw new ValidationException( String.valueOf( errors.getAllErrors()
					.stream()
					.map( InvoiceValidationException::getValidationMessage)
					.collect( Collectors.toList())));
		}
		
		Invoice invoiceSource = _invoiceMapper.invoiceCreateToInvoice( invoiceCreate );
		
		Invoice invoiceCreated = _invoiceService.createInvoice( invoiceSource );
		URI location = URI.create( PATH + "/invoice/?invoiceId=" + invoiceSource.getId() );
		
		return ResponseEntity.created( location )
				.body( _invoiceMapper.invoiceToInvoiceBean( invoiceCreated ));
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
	
	protected static InvoiceMapper getInvoiceMapper()
	{
		return new InvoiceMapper();
	}
	
	protected static InvoiceMapper _invoiceMapper = getInvoiceMapper();
	private final InvoiceService _invoiceService;
	
}
