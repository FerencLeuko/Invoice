package com.example.invoice;

import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InvoiceApplication
{

	public static void main(String[] args)
	{
		_logger.info( "Application started : {}", Instant.now() );
		SpringApplication.run( InvoiceApplication.class, args);
	}
	
	private static Logger _logger = LogManager.getLogger();
}
