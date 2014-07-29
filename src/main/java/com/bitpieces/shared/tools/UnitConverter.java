package com.bitpieces.shared.tools;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javalite.activejdbc.Model;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;


public class UnitConverter {

	public static final List<String> MONEY_COL_NAMES = Arrays.asList("price_per_piece", "worth_current", 
			"funds", "funds_accum", "value_accum", "value_total", "worth", "value_total_current",
			"total_owed", "total_current", "total", "reward_accum", "reward_per_piece_per_year", "reward_earned",
			"reward_earned_total", "price_current", "reward_current");

	public static final List<String> TIME_COL_NAMES = Arrays.asList("time_", "price_time_", "start_time_");

	public static final List<String> CURRENT_MONEY_COL_NAMES = Arrays.asList("worth_current", "value_total", 
			"value_total_current", "total_current", "price_current", "reward_current");

	public static String bitcoinAverageHistoricalCurrQuery(String ISO) {
		return "https://api.bitcoinaverage.com/history/" + ISO + "/per_day_all_time_history.csv";
	}
	public static String bitcoinCurrentCurrQuery(String ISO) {
		return "https://api.bitcoinaverage.com/history/" + ISO + "/per_minute_24h_sliding_window.csv";
	}

	//	public static final String regex = "(?<=Rate\":\")[^\"]*";
	//	public static final String regex2 = "[^\"]";


	// The map of toCurrency, and the given currency service
	private final LoadingCache<String, Map<DateTime, Double>> btcRatesCache = CacheBuilder.newBuilder()
			.expireAfterWrite(15, TimeUnit.MINUTES)
			.build(
					new CacheLoader<String, Map<DateTime, Double>>() {
						public Map<DateTime, Double> load(String ISO) {


							// Put historical rates
							String historyRes = Tools.httpGet(bitcoinAverageHistoricalCurrQuery(ISO));
							Map<DateTime, Double> rates = btcSpotRatesFromBtcAverageResponse(historyRes);

							// Grab the most recent rate for today, and add it
							String currentRes = Tools.httpGet(bitcoinCurrentCurrQuery(ISO));
							Entry<DateTime, Double> recentRate = getMostRecentConversionRateForToday(currentRes);

							rates.put(recentRate.getKey(), recentRate.getValue());

							System.out.println("Recaching BTC -> " + ISO);
							return rates;
						}
					});

	public Double getSpotRate(String iso) {
		DateTime dt = getStartOfDay(new DateTime());
		try {
			return getBtcRatesCache().get(iso).get(dt);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	public static void main(String[] args) {

		UnitConverter sf = new UnitConverter();


		try {
			sf.btcRatesCache.get("EUR");
			//			System.out.println(sf.btcRatesCache.get("USD"));

			String sampleJson = "[{\"creators_id\":1,\"reward_pct\":1.4,\"category_names\":\"Design,Visual Arts\","
					+ "\"number_of_backers\":4,\"worth_current\":117.15643434998,\"creators_name\":\"Leonardo_"
					+ "Davinci\"},{\"creators_id\":2,\"reward_pct\":5.0,\"category_names\":\"Music\",\"number_of_backers"
					+ "\":1,\"worth_current\":124445.52623523,\"creators_name\":\"Dusty_Springfield\"}]";

			String sampleJson2 = "[{\"owners_id\":3,\"price_time_\":\"2014-07-22T15:05:01\",\"creators_id\":1,\"pieces_accum\":5,"
					+ "\"price_per_piece\":1.2245235,\"value_accum\":6.0,\"creators_username\":\"Leonardo_Davinci\",\"price_end_time_\":\""
					+ "2014-07-22T15:05:08\",\"end_time_\":\"2014-07-22T15:05:11\",\"start_time_\":\"2014-07-22T15:05:01\",\""
					+ "timediff_seconds\":7},{\"owners_id\":3,\"price_time_\":\"2014-07-22T15:05:08\",\"creators_id\":1,\"pieces_accum\""
					+ ":5,\"price_per_piece\":21.551232,\"value_accum\":7.5,\"creators_username\":\"Leonardo_Davinci\",\"price_end_time_\":\""
					+ "2014-07-22T15:05:11\",\"end_time_\":\"2014-07-22T15:05:11\",\"start_time_\":\"2014-07-22T15:05:01\",\"timediff_seconds\":3}]";

			//			System.out.println(convertPrecision(sampleJson));
			//			System.out.println(sf.convertAndFormatMoney(sampleJson));

			//			new ListOfMapsPOJO(sampleJson);
			List<Map<String, String>> lom = Tools.ListOfMapsPOJO(sampleJson2);
			DecimalFormat df = setupDecimalFormat("mBTC", 5);
			sf.convertAndFormatMoney(lom, false, "mBTC", df);

		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public static DateTime getStartOfDay(DateTime dt) {
		LocalDate localDate = dt.toLocalDate();
		DateTime startOfDay = localDate.toDateTimeAtStartOfDay(dt.getZone());

		return startOfDay;
	}



	public static DecimalFormat setupDecimalFormat(String isoForSymbol, Integer precision) {

		// Use the iso to symbol map
		//		String symbol = "m\u0E3F";
		String symbol = Tools.CURRENCY_UNICODES.get(isoForSymbol);
		//		String symbol = "";
		//		Integer precision = 4;
		String dfPattern = symbol + "###,###.";
		for (int i = 0; i < precision; i++) {
			dfPattern +="#";
		}

		System.out.println(dfPattern);

		DecimalFormat df = new DecimalFormat(dfPattern);

		return df;
	}

	public String convertAndFormatMoneyJson(String json, 			
			Boolean convertTimeToMillis, 
			String iso, 
			DecimalFormat df) {
		List<Map<String, String>> lom = Tools.ListOfMapsPOJO(json);

		List<Map<String, String>> lom2 = convertAndFormatMoney(lom, convertTimeToMillis, iso, df);

		return Tools.GSON.toJson(lom2);

	}

	public String convertSingleValueCurrentJson(String val, String iso, Integer precision) {

		if (iso== null) {
			iso = "BTC";
		}
		if (precision == null) {
			precision = 4;
		}

		DecimalFormat df = setupDecimalFormat(iso, precision);
		Double number = Double.parseDouble(val);
		String retVal = null;
		try {
			// Get todays conversion rate
			Double todayRate = null;
			if (iso != null && !iso.contains("BTC")) {
				DateTime now = new DateTime();
				DateTime startOfToday = getStartOfDay(now);
				todayRate = getBtcRatesCache().get(iso).get(startOfToday);



				// Convert it
				Double afterConversion = number*todayRate;


				// Format it
				retVal = df.format(afterConversion);

			} else if (iso.equals("BTC")) {
				return df.format(number);
			} else if (iso.equals("mBTC")) {
				return df.format(number*1000);
			}

		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return retVal;

	}

	public List<Map<String, String>> convertAndFormatMoney(List<Map<String, String>>listOfMaps,
			Boolean convertTimeToMillis, 
			DecimalFormat df) {
		return convertAndFormatMoney(listOfMaps,
				convertTimeToMillis, 
				null,
				df) ;
	}

	public List<Map<String, String>> convertAndFormatMoney(List<Map<String, String>>listOfMaps,
			Boolean convertTimeToMillis, 
			String iso, 
			DecimalFormat df) {



		try {

			// Formattable columns
			List<String> allColumns = Tools.getColumnsFromListOfMaps(listOfMaps);

			List<String> currentMoneyCols = new ArrayList<>(allColumns);
			List<String> historicalMoneyCols = new ArrayList<>(allColumns);
			List<String> timeCols = new ArrayList<>(allColumns);

			// Get todays conversion rate
			Map<DateTime, Double> rates = null;
			Double todayRate = null;
			if (iso != null && !iso.contains("BTC")) {
				rates = getBtcRatesCache().get(iso);
				DateTime now = new DateTime();
				DateTime startOfToday = getStartOfDay(now);
				todayRate = rates.get(startOfToday);
			}


			// Start removing columns, based on column names
			currentMoneyCols.retainAll(CURRENT_MONEY_COL_NAMES);

			// only edit the columns that have the special names
			allColumns.retainAll(MONEY_COL_NAMES);

			// Time columns to millis
			timeCols.retainAll(TIME_COL_NAMES);


			historicalMoneyCols.retainAll(MONEY_COL_NAMES);
			historicalMoneyCols.removeAll(CURRENT_MONEY_COL_NAMES);
			//			System.out.println(historicalMoneyCols);


			for (Map<String, String> cMap : listOfMaps) {



				// This is the currency conversion

				// Get that main current time column
				if (timeCols.size() > 0) {
					// If the 'row' has a time - column, then get the exchange rate for that day
					String timeStr = cMap.get(timeCols.get(0));
					DateTime dt = Tools.DTF2.parseDateTime(timeStr);
					// Get that day
					DateTime dayStart = getStartOfDay(dt);

					if (iso != null && !iso.contains("BTC")) {
						// Get the rate on that day
						Double historicalRate = rates.get(dayStart);

						// Loop over all the historical money cols(IE money cols - current_money_cols)
						for (String cCol : historicalMoneyCols) {
							String prevValue = cMap.get(cCol);
							try {
								Double numberBefore = Double.valueOf(prevValue);



								String numberAfter = String.valueOf(numberBefore*historicalRate);
								cMap.put(cCol, numberAfter);
							} catch (NullPointerException | NumberFormatException e) {
								// This happens when an action occurs in the future (issuing pieces), and you don't know
								// what the conversion rate is at that point
								cMap.put(cCol, "");
								//								System.out.println("daystarts = " + dayStart + " | iso = " + iso + " | numbefore = " + numberBefore + " | rate = " + historicalRate);

							}



						}


						//						System.out.println(dt);

					} else if (iso.equals("mBTC")) {
						for (String cCol : historicalMoneyCols) {
							String prevValue = cMap.get(cCol);
							Double numberBefore = Double.valueOf(prevValue);
							String numberAfter = String.valueOf(numberBefore*1000);
							cMap.put(cCol, numberAfter);
						}
					}

					// Convert the time columns to the correct format
					for (String cCol : timeCols) {
						String prevValue = cMap.get(cCol);
						System.out.println(prevValue);
						//						System.out.println(dt);
						//						Date timeBefore = Tools.SDF2.get().parse(prevValue);
						DateTime timeAfter = Tools.DTF2.parseDateTime(prevValue);
						String timeAfterStr = timeAfter.toString();
						//						System.out.println(prevValue);
						//						System.out.println(timeBefore);
						//						System.out.println(timeAfter);

						if (!convertTimeToMillis) {
							cMap.put(cCol, timeAfterStr);
						} else {
							cMap.put(cCol, String.valueOf(timeAfter.getMillis()));
						}
					}
				}

				// These are the today columns
				if (iso != null && !iso.contains("BTC")) {
					for (String cCol : currentMoneyCols) {
						String prevValue = cMap.get(cCol);
						try {
							Double numberBefore = Double.valueOf(prevValue);
							String numberAfter = String.valueOf(numberBefore*todayRate);
							cMap.put(cCol, numberAfter);
						} catch (NullPointerException | NumberFormatException e) {
							// This happens when an action occurs in the future (issuing pieces), and you don't know
							// what the conversion rate is at that point
							cMap.put(cCol, "");

						}
					}
				} else if (iso.equals("mBTC")) {
					for (String cCol : currentMoneyCols) {
						String prevValue = cMap.get(cCol);
						Double numberBefore = Double.valueOf(prevValue);
						String numberAfter = String.valueOf(numberBefore*1000);
						cMap.put(cCol, numberAfter);
					}
				}



				// The precision
				// Format the necessary columns
				for (String cCol : allColumns) {
					String prevValue = cMap.get(cCol);
					try {
						Double numberBefore = Double.valueOf(prevValue);
						String formattedNumber = df.format(numberBefore);
						cMap.put(cCol, formattedNumber);
					} catch (NumberFormatException|NullPointerException e) {
						cMap.put(cCol, "");
					}
				}
			}
			System.out.println(listOfMaps);

		} catch (ExecutionException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listOfMaps;	

	}

	@Deprecated
	public static String convertJson(String json) {
		// First, do the currency conversions

		// Now do the precision conversions

		return null;
	}


	@Deprecated
	public String convertAndFormatMoney(String json) {

		String regex = createKeyColonValueRegex(CURRENT_MONEY_COL_NAMES);

		System.out.println(regex);

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(json);

		while (matcher.find()) {
			System.out.print("Start index: " + matcher.start());
			System.out.print(" End index: " + matcher.end() + "|");
			System.out.println(matcher.group());
		}

		return null;

	}

	@Deprecated
	public static String createKeyColonValueRegex(List<String> list) {
		String names = "";
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			String cName = it.next();
			names += "\"" + cName + "\"";
			if (it.hasNext()) {
				names += "|";
			}
		}

		String regex = "(" + names + "):[^,]*";

		return regex;
	}



	@Deprecated
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

		//		String names = createOrRegex(MONEY_COL_NAMES);		

		String regex = createKeyColonValueRegex(MONEY_COL_NAMES);

		System.out.println(regex);


		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(json);

		while (matcher.find()) {
			System.out.print("Start index: " + matcher.start());
			System.out.print(" End index: " + matcher.end() + "|");
			System.out.println(matcher.group());

			//			String regex2 = "[-+]?[0-9]*\\.?[0-9]+";
			//			String regex2 = "[:]'.*";
			//			String regex2 = "\\:(.*)";
			String regex2 = "(?<=:).*";


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
	public LoadingCache<String, Map<DateTime, Double>> getBtcRatesCache() {
		return btcRatesCache;
	}
	public static final Map<DateTime, Double> btcSpotRatesFromBtcAverageResponse(String res) {
		Map<DateTime, Double> rates = new LinkedHashMap<DateTime, Double>();
		//		System.out.println(res);

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
	public static final Map.Entry<DateTime, Double> getMostRecentConversionRateForToday(String res) {

		// Goto the last one
		String cvsSplit = ",";
		String lines[] = res.split("\\r?\\n");

		String lastLine[] = lines[lines.length-1].split(cvsSplit);

		DateTime time = Tools.DTF.parseDateTime(lastLine[0]);
		Double value = Double.parseDouble(lastLine[1]);

		// Normalize time to today
		LocalDate today = time.toLocalDate();
		DateTime startOfToday = today.toDateTimeAtStartOfDay(time.getZone());

		Map.Entry<DateTime, Double> entry = 
				new AbstractMap.SimpleEntry<DateTime, Double>(startOfToday, value);
		//		System.out.println(entry);
		return entry;
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






}
