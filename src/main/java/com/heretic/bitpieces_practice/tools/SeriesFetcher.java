package com.heretic.bitpieces_practice.tools;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
//			System.out.println(sf.btcRatesCache.get("USD"));
			
			String sampleJson = "[{\"creators_id\":1,\"reward_pct\":1.4,\"category_names\":\"Design,Visual Arts\","
					+ "\"number_of_backers\":4,\"worth_current\":117.15643434998,\"creators_name\":\"Leonardo_"
					+ "Davinci\"},{\"creators_id\":2,\"reward_pct\":5.0,\"category_names\":\"Music\",\"number_of_backers"
					+ "\":1,\"worth_current\":124445.52623523,\"creators_name\":\"Dusty_Springfield\"}]";
			
			System.out.println(convertPrecision(sampleJson));
			
			
			
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	public static String convertJson(String json) {
		// First, do the currency conversions
				
		// Now do the precision conversions
		
		return null;
	}
	
	public static String convertPrecision(String json) {
		String symbol = "\u00A5";
//		String symbol = "";
		Integer precision = 4;
		String dfPattern = symbol + "###,###.";
		for (int i = 0; i < precision; i++) {
			dfPattern +="#";
		}
		
		System.out.println(dfPattern);
		
		DecimalFormat df = new DecimalFormat(dfPattern);

		
		System.out.println("sample Json = " + json);
		
		List<String> monetaryColNames = Arrays.asList("worth_current", "reward_pct");
		String names = "";
		
		Iterator<String> it = monetaryColNames.iterator();
		while (it.hasNext()) {
			String cName = it.next();
			names += "\"" + cName + "\"";
			if (it.hasNext()) {
				names += "|";
			}
		}
		
		String regex = "(" + names + "):[-+]?[0-9]*\\.?[0-9]+";
		
		System.out.println(regex);
		

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(json);
		
		while (matcher.find()) {
		      System.out.print("Start index: " + matcher.start());
		      System.out.print(" End index: " + matcher.end() + "|");
		      System.out.println(matcher.group());
		      
		      String regex2 = "[-+]?[0-9]*\\.?[0-9]+";
		      
				Pattern pattern2 = Pattern.compile(regex2);

				Matcher matcher2= pattern2.matcher(matcher.group());
				
				while (matcher2.find()) {
				      System.out.print("Start index: " + matcher2.start());
				      System.out.print(" End index: " + matcher2.end() + "|");
				      System.out.println(matcher2.group());
				      
				      // FORMAT THE NUMBER
				      Double numberBefore = Double.valueOf(matcher2.group());
				      String formattedNumber = "\"" + df.format(numberBefore) + "\"";
				      
				
				      String name = matcher.group().split(":")[0];
				      
				   
				      
				      String formattedReplace = name + ":" + formattedNumber;
				      System.out.println(matcher.group());
				      System.out.println(formattedReplace);

				      json = json.replaceAll(matcher.group(), formattedReplace);
				}
		      
		      
		    }
		
		System.out.println(json);
		
		return json;
	}




}
