package com.example.invoice.persistance.repository;

import com.example.invoice.persistance.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer>
{

}
