package com.heretic.bitpieces_practice.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

}
