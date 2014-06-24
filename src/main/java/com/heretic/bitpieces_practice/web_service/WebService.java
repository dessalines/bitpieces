package com.heretic.bitpieces_practice.web_service;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.javalite.activejdbc.Base;

import com.google.gson.Gson;
import com.heretic.bitpieces_practice.actions.Actions;
import com.heretic.bitpieces_practice.tables.Tables.User;
import com.heretic.bitpieces_practice.tools.Tools;

public class WebService {
	private static final Gson GSON = new Gson();
	private static Logger log = Logger.getLogger(WebService.class.getName());
	public static void main(String[] args) {

		Properties prop = Tools.loadProperties("/home/tyler/db.properties");


		
		
	
		get("/hello", (req, res) -> {
	
			return "hi from the bitpieces web service";
		});
        get("/help", (req, res) -> {
            res.redirect("/hello");
            return null;
         });
        get("/", (req, res) -> {
            res.redirect("/hello");
            return null;
         });
        
        post("/registeruser", (req, res) -> {
    		dbInit(prop);

        	res.header("Access-Control-Allow-Origin", "http://localhost");

        	// Create the user
        	Actions.createUserFromAjax(req.body());
        	        	
        	return "Hello World: " + req.body();
 
        });
        
        
	}
	private static final void dbInit(Properties prop) {
		Base.open("com.mysql.jdbc.Driver", 
				prop.getProperty("dburl"), 
				prop.getProperty("dbuser"), 
				prop.getProperty("dbpassword"));
	}
}
