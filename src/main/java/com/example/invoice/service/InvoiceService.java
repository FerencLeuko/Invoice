package com.example.invoice.service;

import java.util.List;

import com.example.invoice.controller.bean.InvoiceBean;
import com.example.invoice.controller.bean.InvoiceCreate;
import com.example.invoice.persistance.entity.Invoice;

public interface InvoiceService
{
	List<Invoice> getAllInvoices();
	
	List<Invoice> getInvoices( Integer from, Integer to);
	
	Invoice getInvoice( Integer id );
	
	Invoice createInvoice( Invoice invoice );
}
