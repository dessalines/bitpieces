package com.bitpieces.shared.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bitpieces.shared.DataSources;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Tools {
	
	static final Logger log = LoggerFactory.getLogger(Tools.class);
	
	
	public static final String ROOT_DIR = "/home/tyler/git/bitpieces_practice/";

	public static StrongPasswordEncryptor PASS_ENCRYPT = new StrongPasswordEncryptor();

	public static final Gson GSON = new Gson();
	public static final Gson GSON2 = new GsonBuilder().setPrettyPrinting().create();

	public static final ObjectMapper JACKSON = new ObjectMapper();

	public static final ThreadLocal<SimpleDateFormat> SDF = new ThreadLocal<SimpleDateFormat>(){
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	public static final ThreadLocal<SimpleDateFormat> SDF2 = new ThreadLocal<SimpleDateFormat>(){
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		}
	};



	public static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	public static final DateTimeFormatter DTF2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

	// Instead of using session ids, use a java secure random ID
	private static final SecureRandom RANDOM = new SecureRandom();

	public static final List<String> CATEGORIES = Arrays.asList("Visual Arts", "Comics", "Design", "Dance", "Education", "Film and Video", 
			"Environment", "Music", "Fashion", "Tech", "Photography", "Theatre", "Food", "Health", "Writing and Lit", "Sports",
			"Small Business", "Gaming", "Crafts", "Journalism");

	public static final Map<String, String> CURRENCY_MAP = ImmutableMap.<String, String>builder()
			.put("BTC", "Bitcoin")
			//			.put("mBTC", "MilliBits")
			.put("AUD","Australian Dollar")
			.put( "BRL", "Brazilian Real")
			.put( "CAD", "Canadian Dollar")
			.put( "CHF", "Swiss Franc")
			.put( "CNY", "Chinese Yuan")
			.put( "EUR", "Euro")
			.put( "GBP", "British Pound Sterling")
			.put( "HKD", "Hong Kong Dollar")
			.put( "IDR", "Indonesian Rupiah")
			.put( "ILS", "Israeli New Sheqel")
			.put( "MXN", "Mexican Peso")
			.put( "NOK", "Norwegian Krone")
			.put( "NZD", "New Zealand Dollar")
			.put( "PLN", "Polish Zloty")
			.put( "RON", "Romanian Leu")
			.put( "RUB", "Russian Ruble")
			.put( "SEK", "Swedish Krona")
			.put( "SGD", "Singapore Dollar")
			.put( "TRY", "Turkish Lira")
			.put( "USD", "United States Dollar")
			.put( "ZAR", "South African Rand")
			.build();

	public static final Map<String, String> CURRENCY_UNICODES =  ImmutableMap.<String, String>builder()
			.put("BTC", "\u0E3F")
			//			.put("mBTC", "m\u0E3F")
			.put("AUD","\u0024")
			.put( "BRL", "R\u0024")
			.put( "CAD", "\u0024")
			.put( "CHF", "\u20A3")
			.put( "CNY", "\u5143")
			.put( "EUR", "\u20AC")
			.put( "GBP", "\u20A4")
			.put( "HKD", "\u0024")
			.put( "IDR", "\u20B9")
			.put( "ILS", "\u20AA")
			.put( "MXN", "\u20B1")
			.put( "NOK", "kr")
			.put( "NZD", "\u0024")
			.put( "PLN", "\u007A")
			.put( "RON", "leu")
			.put( "RUB", "\u20BD")
			.put( "SEK", "kr")
			.put( "SGD", "\u0024")
			.put( "TRY", "\u20BA")
			.put( "USD", "\u0024")
			.put( "ZAR", "R")
			.build();


	public static void writeFile(String path, String content) {

		try {
			File file = new File(path);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void Sleep(Long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Properties loadProperties(String propertiesFileLocation) {

		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(propertiesFileLocation);

			// load a properties file
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;

	}


	public static final Map<String, String> createMapFromAjaxPost(String reqBody) {
		//		System.out.println(reqBody);
		Map<String, String> postMap = new HashMap<String, String>();
		String[] split = reqBody.split("&");
		for (int i = 0; i < split.length; i++) {
			String[] keyValue = split[i].split("=");
			try {
				postMap.put(URLDecoder.decode(keyValue[0], "UTF-8"),URLDecoder.decode(keyValue[1], "UTF-8"));
			} catch (UnsupportedEncodingException |ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				throw new NoSuchElementException(e.getMessage());
			}
		}

		System.out.println(postMap);

		return postMap;

	}

	public static final Integer getExpireTime(String reqBody) {
		Map<String, String> map = createMapFromAjaxPost(reqBody);

		if (map.get("remember") != null) {
			return WebCommon.cookieExpiration(1440);
		} else {
			return WebCommon.cookieExpiration(20);
		}
	}// another

	public static final List<String> createArrayFromAjaxPostSelect(String reqBody) {
		//		System.out.println(reqBody);
		List<String> list = new ArrayList<>();
		System.out.println(reqBody);
		String[] split = reqBody.split("&");
		for (int i = 0; i < split.length; i++) {
			String[] keyValue = split[i].split("=");
			try {
				list.add(URLDecoder.decode(keyValue[1], "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return list;

	}

	public static enum UserType {
		User, Creator
	}

	public static String generateSecureRandom() {
		return new BigInteger(256, RANDOM).toString(32);
	}

	public static final String httpGet(String url) {
		String res = "";
		try {
			URL yahoo = new URL(url);

			URLConnection yc = yahoo.openConnection();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							yc.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) 
				res+="\n" + inputLine;
			in.close();

			return res;
		} catch(IOException e) {}
		return res;
	}

	public static final void writeObjectToFile(Object obj, String fileLoc) {
		FileOutputStream out;
		try {
			out = new FileOutputStream(fileLoc);

			ObjectOutputStream os = new ObjectOutputStream(out);

			os.writeObject(obj); 

			os.flush(); 

			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static final <X, Y> Map<X, Y> readObjectFromFile(String fileLoc) {
		// Load the session cache that's been persisted
		FileInputStream in;
		Map<X, Y> retObj = new HashMap<>();
		try {
			in = new FileInputStream(fileLoc);

			// Step 2. Create an object input stream 
			ObjectInputStream ins = new ObjectInputStream(in); 
			retObj = (Map) ins.readObject();


			in.close();
			ins.close();

		} catch (IOException | ClassNotFoundException e1) {
			e1.printStackTrace();
			File file = new File(fileLoc);
			try {
				file.delete();
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("Wrote a new file @ " + file.getAbsolutePath());


		}
		return retObj;
	}



	public static List<Map<String, String>> ListOfMapsPOJO(String json) {

		ObjectMapper mapper = new ObjectMapper();

		try {

			List<Map<String,String>> myObjects = mapper.readValue(json, 
					new TypeReference<ArrayList<HashMap<String,String>>>(){});

			return myObjects;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> getColumnsFromListOfMaps(List<Map<String, String>> lom) {
		Map<String, String> cMap = lom.get(0);
		List<String> cols = new ArrayList<>();
		for (Entry<String, String> e : cMap.entrySet()) {
			cols.add(e.getKey());
		}
		return cols;
	}

	public static String emailRecoveryPassword(String email, String newPass) {

		Properties props = Tools.loadProperties(DataSources.EMAIL_PROP);
		final String username = props.getProperty("username");
		log.info("user-email-name = " + username);
		final String password =  props.getProperty("password");

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("Noreply-bitpieces@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(email));
			message.setSubject("Bitpieces Recovery Password");

			String text = "Your new login information is:\n" +
					"Username : " + email + "\n" + 
					"Password: " + newPass + "\n\n" + 
					"You can change your password from your settings page.";
			message.setText(text);

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new NoSuchElementException(e.getMessage());
		}

		String message = "email sent to " + email;

		return message;

	}




}




