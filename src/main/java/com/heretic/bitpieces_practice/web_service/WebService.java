package com.heretic.bitpieces_practice.web_service;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.javalite.activejdbc.Base;

import com.google.gson.Gson;
import com.heretic.bitpieces_practice.actions.Actions;
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
			res.header("Access-Control-Allow-Origin", "http://localhost");
			dbInit(prop);

			

			// Create the user
			Actions.createUserFromAjax(req.body());
			dbClose();
			return "Hello World: " + req.body();
		
		});
		
		post("/userlogin", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "http://localhost");
			dbInit(prop);

			

			// log the user in
			String userId = Actions.userLogin(req.body());
			
			dbClose();
			
			if (userId != null) {
				// Put the users ID in the session
				req.session().attribute("userId", userId); // put the user id in the session data

			
				System.out.println("The session user Id is " + req.session().attribute("userId"));
				return "Logged in";
			} else {
				return "Incorrect Username or password";
			}
			
	

			
		});
		
		

	}
	private static final void dbInit(Properties prop) {
		Base.open("com.mysql.jdbc.Driver", 
				prop.getProperty("dburl"), 
				prop.getProperty("dbuser"), 
				prop.getProperty("dbpassword"));
	}
	private static final void dbClose() {
		Base.close();
	}
	
}
