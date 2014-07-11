package com.heretic.bitpieces_practice.tools;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.javalite.activejdbc.Model;
import org.joda.time.DateTime;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;


public class SeriesFetcher {

	public static String bitcoinAverageCurrQuery(String ISO) {
		return "https://api.bitcoinaverage.com/history/" + ISO + "/per_day_all_time_history.csv";
	}

	public static final String regex = "(?<=Rate\":\")[^\"]*";
	//	public static final String regex2 = "[^\"]";


	// The map of toCurrency, and the given currency service
	private final LoadingCache<String, Map<DateTime, Double>> btcRatesCache = CacheBuilder.newBuilder()
			.expireAfterWrite(15, TimeUnit.MINUTES)
			.build(
					new CacheLoader<String, Map<DateTime, Double>>() {
						public Map<DateTime, Double> load(String ISO) {

							String res = Tools.httpGet(bitcoinAverageCurrQuery(ISO));
							Map<DateTime, Double> rates = btcSpotRatesFromBtcAverageResponse(res);
							System.out.println("Recaching BTC -> " + ISO);
							return rates;
						}
					});


	public static final Map<DateTime, Double> btcSpotRatesFromBtcAverageResponse(String res) {
		Map<DateTime, Double> rates = new LinkedHashMap<DateTime, Double>();
		System.out.println(res);

		String cvsSplit = ",";
		String lines[] = res.split("\\r?\\n");

		for (int i = 1; i < lines.length; i++) {
			// Starting at line #2, put the two values into a map
			String cLine[] = lines[i].split(cvsSplit);

			try {
				rates.put(Tools.DTF.parseDateTime(cLine[0]), Double.parseDouble(cLine[3]));
			} catch (IllegalArgumentException e) {
				// TODO it finds a bunch of these for some reason
				
			}

		}

		//		System.out.println(Tools.GSON2.toJson(rates));


		return rates;

	}

	Map<DateTime, Double> getDateValueMapFromTable(List<Model> list, String dateColName, String valColName) {

		Map<DateTime, Double> dv = new LinkedHashMap<DateTime, Double>();

		for (Model item : list) {
			String dateStr = item.getString(dateColName);
				DateTime date = Tools.DTF.parseDateTime(dateStr);
				Double val = item.getDouble(valColName);
				
				dv.put(date, val);
				
	
		}
		
		return dv;


	}

	public static void main(String[] args) {

		SeriesFetcher sf = new SeriesFetcher();


		try {
			sf.btcRatesCache.get("EUR");
			System.out.println(sf.btcRatesCache.get("USD"));
			
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}




}
