package com.example.invoice.service;

import java.util.stream.Collectors;

import com.example.invoice.controller.bean.InvoiceBean;
import com.example.invoice.controller.bean.InvoiceCreate;
import com.example.invoice.controller.bean.ItemBean;
import com.example.invoice.controller.bean.ItemCreate;
import com.example.invoice.persistance.entity.Invoice;
import com.example.invoice.persistance.entity.Item;
import org.springframework.stereotype.Service;

@Service
public class InvoiceMapper
{
	public Invoice invoiceCreateToInvoice( InvoiceCreate invoiceCreate )
	{
		return Invoice.builder()
				.customerName( invoiceCreate.getCustomerName())
				.comment( invoiceCreate.getComment() )
				.issueDate( invoiceCreate.getIssueDate() )
				.dueDate( invoiceCreate.getDueDate() )
				.items( invoiceCreate.getItems().stream().map( i -> itemCreateToItem(i) ).collect( Collectors.toList()) )
				.build();
	}
	
	public InvoiceBean invoiceToInvoiceBean( Invoice invoice )
	{
		return InvoiceBean.builder()
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
	
	private Item itemCreateToItem( ItemCreate itemCreate )
	{
		return Item.builder()
				.productName( itemCreate.getProductName())
				.unitPrice( itemCreate.getUnitPrice() )
				.quantity( itemCreate.getQuantity() )
				.build();
	}
	
	private ItemBean itemToItemBean( Item item )
	{
		return ItemBean.builder()
				.productName( item.getProductName())
				.unitPrice( item.getUnitPrice() )
				.quantity( item.getQuantity() )
				.itemTotalPrice( item.getItemTotalPrice() )
				.itemTotalEuroPrice( item.getItemTotalEuroPrice() )
				.build();
	}
}
