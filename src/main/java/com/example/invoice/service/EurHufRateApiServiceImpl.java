package com.example.invoice.service;

import java.io.InputStream;
import java.time.Instant;
import java.util.Properties;

import com.example.invoice.service.bean.Currency;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EurHufRateApiServiceImpl implements EurHufRateApiService
{
	@Override
	public double getEurHufRate()
	{
		if( cachedEuroRate == 0d || refreshTime == null || refreshTime.plusSeconds( REFRESH_PERIOD ).isBefore( Instant.now() ) )
		{
			refreshEuroRate();
		}
		return cachedEuroRate;
	}
	
	private void refreshEuroRate()
	{
		try
		{
			cachedEuroRate = readEurHufRateFromApi();
			refreshTime = Instant.now();
		}
		catch( Exception e )
		{
			if ( cachedEuroRate == 0d )
			{
				try
				{
					InputStream in = getClass().getClassLoader().getResourceAsStream( EURO_RATE_PROPERTIES_FILE );
					Properties prop = new Properties();
					prop.load( in );
					cachedEuroRate = Double.parseDouble( prop.getProperty( "EuroRate" ) );
				}
				catch( Exception ex )
				{
					cachedEuroRate = DEFAULT_EUR_RATE;
					_logger.error( "This file was not readable: {} , rate is set to: ", EURO_RATE_PROPERTIES_FILE, cachedEuroRate );
				}
			}
		}
	}
	
	private double readEurHufRateFromApi()
	{
		String apiUrl = getApiUrlEurHuf();
		try
		{
			Currency currency = restTemplate.getForObject( apiUrl, Currency.class );
			return currency.getQuotes().getUSDHUF() / currency.getQuotes().getUSDEUR();
		}
		catch( Exception e )
		{
			_logger.error( "Error reading rate from api: {}, error: {}" , apiUrl, e );
			throw e;
		}
	}
	
	private String getApiUrlEurHuf()
	{
		if ( apiUrl == null )
		{
			try
			{
				InputStream in = getClass().getClassLoader().getResourceAsStream( EURO_RATE_PROPERTIES_FILE );
				Properties prop = new Properties();
				prop.load( in );
				apiUrl = prop.getProperty( "ApiCallEurHuf" );
			}
			catch( Exception ex )
			{
				apiUrl = DEFAULT_API_CALL_EUR_HUF;
				_logger.error( "This file was not readable: {}, url is set to: ", EURO_RATE_PROPERTIES_FILE, apiUrl );
			}
		}
		return apiUrl;
	}
	
	private static final String DEFAULT_BASE_URL = "http://api.currencylayer.com/live";
	private static final String DEFAULT_ACCESS_KEY = "a0b184a9b7556b362369915577efa807";
	private static final String DEFAULT_API_CALL_EUR_HUF = DEFAULT_BASE_URL + "?access_key=" + DEFAULT_ACCESS_KEY + "&currencies=EUR,HUF";
	private static final String EURO_RATE_PROPERTIES_FILE = "EuroRate.properties";
	private static final int REFRESH_PERIOD = 3600;
	private static final double DEFAULT_EUR_RATE = 350;
	
	private static Logger _logger = LogManager.getLogger();
	
	private final RestTemplate restTemplate = new RestTemplate();
	private String apiUrl;
	private double cachedEuroRate;
	private Instant refreshTime;
}
