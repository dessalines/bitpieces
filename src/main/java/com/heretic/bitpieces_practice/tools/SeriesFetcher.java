package com.heretic.bitpieces_practice.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sun.corba.se.impl.encoding.CodeSetConversion.BTCConverter;


public class SeriesFetcher {
	
	


	public static String currencyQuery(String ISO) {
		return "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20"
		+ "yahoo.finance.xchange%20where%20pair%20%3D%20%22BTC" + ISO + 
		"%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	}
	public static final String regex = "(?<=Rate\":\")[^\"]*";
//	public static final String regex2 = "[^\"]";
	
	
	// The map of toCurrency, and the given currency service
	private final LoadingCache<String, Double> btcConversionCache = CacheBuilder.newBuilder()
		       .expireAfterWrite(15, TimeUnit.MINUTES)
		       .build(
		           new CacheLoader<String, Double>() {
		             public Double load(String ISO) {
		            	 
		         		String res = Tools.httpGet(currencyQuery(ISO));
		         		Double val = extractCurrencyFromYahooResponse(res);
		         		System.out.println("Recaching BTC -> " + ISO + " = " + val);
		        		return val;
		             }
		           });
	
	public static final Double extractCurrencyFromYahooResponse(String res) {
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(res);

		Double val = null;
		if (m.find()) {
			String curStr = m.group(0);
		    val = Double.parseDouble(curStr);
		}
		
	
		return val;
	}
	
	public static void main(String[] args) {
	
		SeriesFetcher sf = new SeriesFetcher();
		
		try {
			sf.btcConversionCache.get("USD");
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
