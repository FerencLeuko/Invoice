package com.example.invoice.service;

import java.util.List;

import com.example.invoice.controller.bean.InvoiceBean;
import com.example.invoice.controller.bean.InvoiceCreate;

public interface InvoiceService
{
	List<InvoiceBean> getAllInvoices();
	
	List<InvoiceBean> getInvoices( Integer from, Integer to);
	
	InvoiceBean getInvoice( Integer id );
	
	InvoiceBean createInvoice( InvoiceCreate invoiceCreate );
}
