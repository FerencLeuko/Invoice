package com.example.invoice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.example.invoice.controller.bean.InvoiceBean;
import com.example.invoice.controller.bean.InvoiceCreate;
import com.example.invoice.exception.InvoiceNotFoundException;
import com.example.invoice.exception.UnsupportedParameterException;
import com.example.invoice.persistance.entity.Invoice;
import com.example.invoice.persistance.entity.Item;
import com.example.invoice.persistance.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService
{
	@Override
	public List<Invoice> getAllInvoices()
	{
		List<Invoice> invoices = _invoiceRepository.findAll();
		return invoices;
	}
	
	@Override
	public List<Invoice> getInvoices( Integer from, Integer to )
	{
		validateParamsFromTo( from, to );
		List<Invoice> invoices = new ArrayList<>();
		for( ; from <= to; from++ )
		{
			try
			{
				Invoice invoice = getInvoice( from );
				invoices.add( invoice );
			}
			catch( IllegalArgumentException e )
			{
			}
		}
		return invoices;
	}
	
	@Override
	public Invoice getInvoice( Integer id )
	{
		try
		{
			Invoice invoice = _invoiceRepository.findById( id ).get();
			return invoice;
		}
		catch( NoSuchElementException e )
		{
			_logger.debug( "Invoice was not found with id {}", id );
			throw new InvoiceNotFoundException( "This id does not exist: " + id );
		}
	}
	
	@Override
	public Invoice createInvoice( Invoice invoice )
	{
		setInvoiceIdsInItems( invoice );
		setTotalPrices( invoice );
		_invoiceRepository.save( invoice );
		
		return invoice;
	}
	
	private void setInvoiceIdsInItems( Invoice invoice )
	{
		invoice.getItems().stream().forEach( item -> item.setInvoice( invoice ) );
	}
	
	private void setTotalPrices( Invoice invoice )
	{
		double eurHufRate = _eurHufRateService.getEurHufRate();
		double totalPrice = 0;
		for( Item item : invoice.getItems() )
		{
			double itemTotalPrice = item.getUnitPrice() * item.getQuantity();
			item.setItemTotalPrice( roundToThreeDigits( itemTotalPrice ));
			totalPrice += itemTotalPrice;
			item.setItemTotalEuroPrice( roundToThreeDigits( convertHufToEuro( itemTotalPrice, eurHufRate )));
		}
		invoice.setTotalPrice( roundToThreeDigits( totalPrice ));
		invoice.setTotalEuroPrice( roundToThreeDigits( convertHufToEuro( totalPrice, eurHufRate )));
	}
	
	private void validateParamsFromTo( Integer from, Integer to )
	{
		if ( from <0 || to <0 || from > to || to - from > MAX_ENVOICES_RETURN_FROM_TO )
		{
			_logger.debug( "Unsupported call for from: {} to: {}. Max amount is: {}", from, to, MAX_ENVOICES_RETURN_FROM_TO );
			throw new UnsupportedParameterException( "Unsupported call for from: "+ from +" to: "+ to +". To must " +
					"be higher than from. Max amount is: " + MAX_ENVOICES_RETURN_FROM_TO );
		}
	}
	
	private double roundToThreeDigits( double number )
	{
		return Math.floor( number * 1000 ) / 1000;
	}
	
	private double convertHufToEuro( double huf, double eurHufRate )
	{
		return huf / eurHufRate;
	}
	
	private static final int MAX_ENVOICES_RETURN_FROM_TO = 20;
	private static Logger _logger = LogManager.getLogger();
	
	private final InvoiceRepository _invoiceRepository;
	private final EurHufRateApiService _eurHufRateService;
}
