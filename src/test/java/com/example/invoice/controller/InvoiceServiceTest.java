package com.example.invoice.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.example.invoice.controller.InvoiceController;
import com.example.invoice.controller.bean.InvoiceBean;
import com.example.invoice.controller.bean.InvoiceCreate;
import com.example.invoice.controller.bean.ItemBean;
import com.example.invoice.controller.bean.ItemCreate;
import com.example.invoice.persistance.entity.Invoice;
import com.example.invoice.persistance.entity.Item;
import com.example.invoice.persistance.repository.InvoiceRepository;
import com.example.invoice.service.EurHufRateApiService;
import com.example.invoice.service.EurHufRateApiServiceImpl;
import com.example.invoice.service.InvoiceMapper;
import com.example.invoice.service.InvoiceService;
import com.example.invoice.service.InvoiceServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith( SpringRunner.class)
@SpringBootTest
public class InvoiceServiceTest
{
	/* Database details for Spring are configured in test/application.properties for the test
	Warning : if USE_MySQL, all entries are deleted from test db specified in test/application.properties */
	private static final boolean USE_MYSQL = false;    // flag to test with mysql on local db or test with h2 file
	private static final String DATABASE_FILE = "invoice_test"; // file
	private static final String DB_NAME = "invoice_test"; // db name if use_mysql
	private static final String USER_NAME = "root"; // db user for mysql
	private static final String PASSWORD = "admin"; // db password for mysql
	
	@Test
	public void testInvoiceMapper() throws SQLException
	{
		InvoiceMapper invoiceMapper = InvoiceController.getInvoiceMapper();
		List<InvoiceCreate> source = getInvoicesToTest();
		List<Invoice> destination = source.stream().map( i -> invoiceMapper.invoiceCreateToInvoice( i ) ).collect( Collectors.toList());
		
		int invoiceCount = 0;
		for( InvoiceCreate invoiceCreate : source )
		{
			Invoice invoice = destination.get( invoiceCount );
			assertEquals( invoiceCreate.getCustomerName(), invoice.getCustomerName() );
			assertFalse( invoiceCreate.getCustomerName().equals( invoice.getCustomerName() + "foo" ) );
			assertEquals( invoiceCreate.getComment(), invoice.getComment() );
			assertEquals( invoiceCreate.getIssueDate(), invoice.getIssueDate() );
			assertEquals( invoiceCreate.getDueDate(), invoice.getDueDate() );
			assertEquals( invoiceCreate.getItems().size(), invoice.getItems().size() );
			
			List<Item> destinationItems = invoice.getItems();
			int itemCount = 0;
			for( ItemCreate itemCreate : invoiceCreate.getItems() )
			{
				Item item = destinationItems.get( itemCount );
				assertEquals( itemCreate.getProductName(), item.getProductName() );
				assertEquals( itemCreate.getQuantity(), item.getQuantity() );
				assertEquals( itemCreate.getUnitPrice(), item.getUnitPrice() );
				itemCount++;
			}
			invoiceCount++;
		}
		
		List<InvoiceBean> destinationBeans =
				destination.stream().map( i -> invoiceMapper.invoiceToInvoiceBean( i ) ).collect( Collectors.toList());
		invoiceCount = 0;
		for( InvoiceCreate invoiceCreate : source )
		{
			InvoiceBean invoiceBean = destinationBeans.get( invoiceCount );
			assertEquals( invoiceCreate.getCustomerName(), invoiceBean.getCustomerName() );
			assertFalse( invoiceCreate.getCustomerName().equals( invoiceBean.getCustomerName() + "foo" ) );
			assertEquals( invoiceCreate.getComment(), invoiceBean.getComment() );
			assertEquals( invoiceCreate.getIssueDate(), invoiceBean.getIssueDate() );
			assertEquals( invoiceCreate.getDueDate(), invoiceBean.getDueDate() );
			assertEquals( invoiceCreate.getItems().size(), invoiceBean.getItems().size() );
			
			List<ItemBean> itemBeans = invoiceBean.getItems();
			int itemCount = 0;
			for( ItemCreate itemCreate : invoiceCreate.getItems() )
			{
				ItemBean itemBean = itemBeans.get( itemCount );
				assertEquals( itemCreate.getProductName(), itemBean.getProductName() );
				assertEquals( itemCreate.getQuantity(), itemBean.getQuantity() );
				assertEquals( itemCreate.getUnitPrice(), itemBean.getUnitPrice() );
				itemCount++;
			}
			invoiceCount++;
		}
		_logger.info( "Test passed: testInvoiceMapper." );
	}
	
	@Test
	public void testInvoiceService() throws SQLException
	{
		InvoiceMapper invoiceMapper = InvoiceController.getInvoiceMapper();
		EurHufRateApiService _eruoService = new EurHufRateApiServiceImpl();
		InvoiceService _invoiceService = new InvoiceServiceImpl( _repo, _eruoService );
		_repo.deleteAll();
		
		List<InvoiceCreate> sourceList = getInvoicesToTest();
		for( InvoiceCreate invoiceCreate : sourceList )
		{
			_invoiceService.createInvoice( invoiceMapper.invoiceCreateToInvoice( invoiceCreate ) );
		}
		List<Invoice> destinationList = _invoiceService.getAllInvoices();
		assertEquals( sourceList.size(), destinationList.size() );
		
		int invoiceCount = 0;
		for( InvoiceCreate source : sourceList )
		{
			Invoice destination = destinationList.get( invoiceCount );
			assertEquals( source.getCustomerName(), destination.getCustomerName() );
			assertFalse( source.getCustomerName().equals( destination.getCustomerName()+"foo" ));
			assertEquals( source.getComment(), destination.getComment() );
			assertEquals( source.getIssueDate(), destination.getIssueDate() );
			assertEquals( source.getDueDate(), destination.getDueDate() );
			assertEquals( source.getItems().size(), destination.getItems().size() );
			assertTrue( Math.abs( source.getItems().stream().mapToDouble( d -> d.getUnitPrice() * d.getQuantity() ).sum()
			- destination.getTotalPrice()) < 1 );
			assertTrue( Math.abs(destination.getItems().stream().mapToDouble( d -> d.getItemTotalPrice() ).sum()
					- destination.getTotalPrice()) < 1  );
			assertTrue( Math.abs(destination.getItems().stream().mapToDouble( d -> d.getItemTotalEuroPrice() ).sum()
					- destination.getTotalEuroPrice()) < 1  );
			
			List<Item> destinationItems = destination.getItems();
			int itemCount = 0;
			for ( ItemCreate sourceItem : source.getItems() )
			{
				Item destinationItem = destinationItems.get( itemCount );
				assertEquals( sourceItem.getProductName(), destinationItem.getProductName());
				assertEquals( sourceItem.getQuantity(), destinationItem.getQuantity());
				assertEquals( sourceItem.getUnitPrice(), destinationItem.getUnitPrice());
				assertTrue( Math.abs( sourceItem.getUnitPrice() * sourceItem.getQuantity() - destinationItem.getItemTotalPrice() ) < 1 );
				itemCount++;
			}
			invoiceCount++;
		}
		_logger.info( "Test passed: testInvoiceService." );
	}
	
	@BeforeClass
	public static void setupTest() throws SQLException
	{
		try( Connection connection = getConnection() )
		{
			String createInvoiceTable = "CREATE TABLE IF NOT EXISTS `invoice` ("
					+ "`id` int(11) NOT NULL,"
					+ "`created` datetime DEFAULT NULL,"
					+ "`modified` datetime DEFAULT NULL,"
					+ "`comment` varchar(255) DEFAULT NULL,"
					+ "`customer_name` varchar(255) DEFAULT NULL,"
					+ "`due_date` date DEFAULT NULL,"
					+ "`issue_date` date DEFAULT NULL,"
					+ "`total_euro_price` double DEFAULT NULL,"
					+ "`total_price` double DEFAULT NULL,"
					+ "PRIMARY KEY (`id`)"
					+ ") ENGINE=InnoDB";
			
			String createInvoiceSeqTable = "CREATE TABLE IF NOT EXISTS `invoice_seq` ("
					+ "`next_val` bigint(20) DEFAULT NULL"
					+ ") ENGINE=InnoDB";
			
			String createItemTable = "CREATE TABLE IF NOT EXISTS `item` ("
					+ "`id` int(11) NOT NULL,"
					+ "`created` datetime DEFAULT NULL,"
					+ "`modified` datetime DEFAULT NULL,"
					+ "`total_euro_price` double DEFAULT NULL,"
					+ "`total_item_price` double DEFAULT NULL,"
					+ "`product_name` varchar(255) DEFAULT NULL,"
					+ "`quantity` int(11) DEFAULT NULL,"
					+ "`unit_price` double DEFAULT NULL,"
					+ "`invoice_id` int(11) DEFAULT NULL,"
					+ "PRIMARY KEY (`id`),"
					+ "KEY `FK1` (`invoice_id`),"
					+ "CONSTRAINT `FK1` FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`id`)"
					+ ") ENGINE=InnoDB";
			
			String createItemSeqTable = "CREATE TABLE IF NOT EXISTS `item_seq` ("
					+ "  `next_val` bigint(20) DEFAULT NULL"
					+ ") ENGINE=InnoDB";
			
			connection.prepareStatement( createInvoiceTable ).execute();
			connection.prepareStatement( createInvoiceSeqTable ).execute();
			connection.prepareStatement( createItemTable ).execute();
			connection.prepareStatement( createItemSeqTable ).execute();
			connection.createStatement().execute( "INSERT INTO invoice_seq (next_val) VALUES (1);" );
			connection.createStatement().execute( "INSERT INTO item_seq (next_val) VALUES (1);" );
			
			_logger.info( "Database created or updated for test." );
		}
	}
	
	@AfterClass//clean database
	public static void cleanup() throws IOException, SQLException
	{
		if( !USE_MYSQL )
		{
			getConnection().createStatement().execute( "SHUTDOWN" );
			Files.deleteIfExists( Paths.get( DATABASE_FILE + ".mv.db" ) );
			Files.deleteIfExists( Paths.get( DATABASE_FILE + ".trace.db" ) );
			_logger.info( "Test files deleted" );
		}
	}
	
	protected static Connection getConnection() throws SQLException
	{
		if( USE_MYSQL )
		{
			return DriverManager.getConnection(
					"jdbc:mysql://127.0.0.1:3306/" + DB_NAME,
					USER_NAME,
					PASSWORD
			);
		}
		else
		{
			return DriverManager.getConnection(
					"jdbc:h2:./" + DATABASE_FILE + ";MODE=MYSQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE", //in latest h2 version we can also add ;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE",
					"sa",
					""
			);
		}
	}
	
	private List<InvoiceCreate> getInvoicesToTest()
	{
		ItemCreate item1 = ItemCreate.builder().productName( "A" ).quantity( 1 ).unitPrice( 1d ).build();
		ItemCreate item2 = ItemCreate.builder().productName( "B" ).quantity( 4 ).unitPrice( 3000d ).build();
		ItemCreate item3 = ItemCreate.builder().productName( "C" ).quantity( 4000 ).unitPrice( 2d ).build();
		ItemCreate item4 = ItemCreate.builder().productName( "D" ).quantity( 5000 ).unitPrice( 3.9d ).build();
		ItemCreate item5 = ItemCreate.builder().productName( "E" ).quantity( 12 ).unitPrice( 32.3d ).build();
		
		List<InvoiceCreate> invoices = new ArrayList<>();
		
		InvoiceCreate invoice1 = InvoiceCreate.builder()
				.customerName( "C1" )
				.comment( "Invoice1" )
				.issueDate( LocalDate.of( 2020, 01, 01 ) )
				.dueDate( LocalDate.of( 2020, 01, 10 ) )
				.items( Arrays.asList( new ItemCreate[]{item1, item2} ) )
				.build();
		
		InvoiceCreate invoice2 = InvoiceCreate.builder()
				.customerName( "C2" )
				.comment( "Invoice2" )
				.issueDate( LocalDate.of( 2020, 02, 01 ) )
				.dueDate( LocalDate.of( 2020, 02, 10 ) )
				.items( Arrays.asList( new ItemCreate[]{item3, item4} ) )
				.build();
		
		InvoiceCreate invoice3 = InvoiceCreate.builder()
				.customerName( "C2" )
				.issueDate( LocalDate.of( 2020, 02, 01 ) )
				.dueDate( LocalDate.of( 2020, 02, 10 ) )
				.items( Arrays.asList( new ItemCreate[]{ item5 } ) )
				.build();
		
		invoices.add( invoice1 );
		invoices.add( invoice2 );
		invoices.add( invoice3 );
		
		return invoices;
	}
	
	private static final Logger _logger = LogManager.getLogger();
	@Autowired
	InvoiceRepository _repo;
}
	

