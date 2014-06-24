package com.heretic.bitpieces_practice.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jasypt.util.password.StrongPasswordEncryptor;

public class Tools {

	public static final StrongPasswordEncryptor PASS_ENCRYPT = new StrongPasswordEncryptor();

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

		System.out.println(reqBody);
		
		Map<String, String> postMap = new HashMap<String, String>();
		String[] split = reqBody.split("&");
		for (int i = 0; i < split.length; i++) {
			String[] keyValue = split[i].split("=");
			postMap.put(keyValue[0], keyValue[1]);
		}
		
		return postMap;

	}
}
