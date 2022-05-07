package com.example.invoice.service;

import java.util.stream.Collectors;

import com.example.invoice.controller.bean.InvoiceResponse;
import com.example.invoice.controller.bean.InvoiceRequest;
import com.example.invoice.controller.bean.ItemResponse;
import com.example.invoice.controller.bean.ItemRequest;
import com.example.invoice.persistance.entity.Invoice;
import com.example.invoice.persistance.entity.Item;
import org.springframework.stereotype.Service;

@Service
public class InvoiceMapper
{
	public Invoice invoiceCreateToInvoice( InvoiceRequest invoiceRequest )
	{
		return Invoice.builder()
				.customerName( invoiceRequest.getCustomerName())
				.comment( invoiceRequest.getComment() )
				.issueDate( invoiceRequest.getIssueDate() )
				.dueDate( invoiceRequest.getDueDate() )
				.items( invoiceRequest.getItems().stream().map( i -> itemCreateToItem(i) ).collect( Collectors.toList()) )
				.build();
	}
	
	public InvoiceResponse invoiceToInvoiceBean( Invoice invoice )
	{
		return InvoiceResponse.builder()
				.id( invoice.getId() )
				.customerName( invoice.getCustomerName())
				.comment( invoice.getComment() )
				.issueDate( invoice.getIssueDate() )
				.dueDate( invoice.getDueDate() )
				.totalPrice( invoice.getTotalPrice() )
				.totalEuroPrice( invoice.getTotalEuroPrice() )
				.items( invoice.getItems().stream().map( i -> itemToItemBean(i) ).collect( Collectors.toList()) )
				.build();
	}
	
	private Item itemCreateToItem( ItemRequest itemRequest )
	{
		return Item.builder()
				.productName( itemRequest.getProductName())
				.unitPrice( itemRequest.getUnitPrice() )
				.quantity( itemRequest.getQuantity() )
				.build();
	}
	
	private ItemResponse itemToItemBean( Item item )
	{
		return ItemResponse.builder()
				.productName( item.getProductName())
				.unitPrice( item.getUnitPrice() )
				.quantity( item.getQuantity() )
				.itemTotalPrice( item.getItemTotalPrice() )
				.itemTotalEuroPrice( item.getItemTotalEuroPrice() )
				.build();
	}
}
