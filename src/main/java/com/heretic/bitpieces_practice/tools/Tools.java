package com.heretic.bitpieces_practice.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jasypt.util.password.StrongPasswordEncryptor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Tools {
	public static final String ROOT_DIR = "/home/tyler/git/bitpieces_practice/";

	public static final StrongPasswordEncryptor PASS_ENCRYPT = new StrongPasswordEncryptor();

	public static final Gson GSON = new Gson();
	public static final Gson GSON2 = new GsonBuilder().setPrettyPrinting().create();
	
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// Instead of using session ids, use a java secure random ID
	private static final SecureRandom RANDOM = new SecureRandom();

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

		Map<String, String> postMap = new HashMap<String, String>();
		String[] split = reqBody.split("&");
		for (int i = 0; i < split.length; i++) {
			String[] keyValue = split[i].split("=");
			try {
				postMap.put(keyValue[0], java.net.URLDecoder.decode(keyValue[1], "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		System.out.println(postMap);

		return postMap;

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

}
