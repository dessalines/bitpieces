package com.heretic.bitpieces_practice.dev.tables;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.javalite.activejdbc.Base;

import com.heretic.bitpieces_practice.shared.tools.Tools;

public class InitializeLocations {
	public static void main(String[] args) {

		Properties prop = Tools.loadProperties("/home/tyler/db.properties");

		Base.open("com.mysql.jdbc.Driver", 
				prop.getProperty("dburl"), 
				prop.getProperty("dbuser"), 
				prop.getProperty("dbpassword"));

		System.out.println("Initializing cities...");
		
		
		String fileLoc = Tools.ROOT_DIR + "/resources/worldcitiespop.txt";
		BufferedReader br;
		
		try {
			br = new BufferedReader(new FileReader(fileLoc));

			String line;
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");
				String country = split[0];
				String city = split[2];
				String region = split[3];
				
//				Cities.createIt("country", country, "city", city, "region", region);
				
			}
			br.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
