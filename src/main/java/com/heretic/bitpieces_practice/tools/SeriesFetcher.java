package com.heretic.bitpieces_practice.tools;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;


public class SeriesFetcher {
	
	


	@Deprecated
	public static String yahooCurrQuery(String ISO) {
		return "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20"
		+ "yahoo.finance.xchange%20where%20pair%20%3D%20%22BTC" + ISO + 
		"%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	}
	
	public static String bitcoinAverageCurrQuery(String ISO) {
		return "https://api.bitcoinaverage.com/history/" + ISO + "/per_day_all_time_history.csv";
	}
	
	public static final String regex = "(?<=Rate\":\")[^\"]*";
//	public static final String regex2 = "[^\"]";
	
	
	// The map of toCurrency, and the given currency service
	private final LoadingCache<String, Map<Date, Double>> btcRatesCache = CacheBuilder.newBuilder()
		       .expireAfterWrite(15, TimeUnit.MINUTES)
		       .build(
		           new CacheLoader<String, Map<Date, Double>>() {
		             public Map<Date, Double> load(String ISO) {
		   		       
		         		String res = Tools.httpGet(bitcoinAverageCurrQuery(ISO));
		        		Map<Date, Double> rates = btcSpotRatesFromBtcAverageResponse(res);
		         		System.out.println("Recaching BTC -> " + ISO);
		        		return rates;
		             }
		           });
	
	@Deprecated
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
	
	public static final Map<Date, Double> btcSpotRatesFromBtcAverageResponse(String res) {
		Map<Date, Double> rates = new LinkedHashMap<Date, Double>();
		System.out.println(res);
		
		String cvsSplit = ",";
		String lines[] = res.split("\\r?\\n");
		
		for (int i = 1; i < lines.length; i++) {
			// Starting at line #2, put the two values into a map
			String cLine[] = lines[i].split(cvsSplit);

			try {
				rates.put(Tools.SDF.parse(cLine[0]), Double.parseDouble(cLine[3]));
			} catch (NumberFormatException | ParseException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
			
		}
		
//		System.out.println(Tools.GSON2.toJson(rates));
		
		
		return rates;
		
	}
	
	public static void main(String[] args) {
	
		SeriesFetcher sf = new SeriesFetcher();
		
		
		try {
			sf.btcRatesCache.get("EUR");
			sf.btcRatesCache.get("USD");
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
