package com.heretic.bitpieces_practice.web_service;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.javalite.activejdbc.Base;

import spark.Response;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.heretic.bitpieces_practice.actions.Actions;
import com.heretic.bitpieces_practice.tools.Tools;

public class WebService {

	// How long to keep the cookies
	public static final Integer COOKIE_EXPIRE_SECONDS = cookieExpiration(30);



	// Use an expiring map to store the authenticated sessions
	public static final Cache<String, String> SESSION_TO_USER_MAP = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.expireAfterWrite(COOKIE_EXPIRE_SECONDS, TimeUnit.SECONDS)
			.build();
	
	private static final Gson GSON = new Gson();
	private static Logger log = Logger.getLogger(WebService.class.getName());
	public static void main(String[] args) {

		Properties prop = Tools.loadProperties("/home/tyler/db.properties");



		get("/session", (req,res) -> {
			res.header("Access-Control-Allow-Origin", "http://localhost");

			// Give the session id
			return "derp";
		});

		get("/hello", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "http://localhost");
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
		get("/:auth/getpiecesownedtotal", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "http://localhost");
			dbInit(prop);
			
			String userId = SESSION_TO_USER_MAP.getIfPresent(req.params(":auth"));
			
			String json = Actions.getPiecesOwnedTotal(userId);
			

			dbClose();

			System.out.println(json);
			return json;


		});


		post("/registeruser", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "http://localhost");
			res.header("Access-Control-Allow-Credentials", "true");
			dbInit(prop);

			// Create the user
			String userId = Actions.createUserFromAjax(req.body());

			dbClose();

			// Its null if it couldn't create the user, usually cause of constraints
			if (userId != null) {
				verifyLoginAndSetCookies(userId, res);

				return "user registered";
			} else {
				
				res.status(666);
				return "User already exists";
			}

		});
		
		post("/registercreator", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "http://localhost");
			res.header("Access-Control-Allow-Credentials", "true");
			dbInit(prop);

			// Create the user
			String creatorId = Actions.createCreatorFromAjax(req.body());

			dbClose();

			// Its null if it couldn't create the user, usually cause of constraints
			if (creatorId != null) {
				verifyLoginAndSetCookies(creatorId, res);

				return "creator registered";
			} else {
				
				res.status(666);
				return "Creator already exists";
			}

		});

		post("/userlogin", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "http://localhost");
			res.header("Access-Control-Allow-Credentials", "true");

			dbInit(prop);

			// log the user in
			String userId = Actions.userLogin(req.body());
			
			dbClose();

			
			String message = verifyLoginAndSetCookies(userId, res);

			return message;

		});


	}

	private static String verifyLoginAndSetCookies(String userId, Response res) {
		if (userId != null) {
			String authenticatedSession = Tools.generateSecureRandom();
			// Put the users ID in the session
			//				req.session().attribute("userId", userId); // put the user id in the session data

			// Store the users Id in a static map, give them a session id
			SESSION_TO_USER_MAP.put(authenticatedSession, userId);

			
			// Set some cookies for that users login
			res.cookie("authenticated_session_id", authenticatedSession, COOKIE_EXPIRE_SECONDS, false);
			System.out.println(GSON.toJson(SESSION_TO_USER_MAP));

			return authenticatedSession;
		} else {
			res.status(666);
			return "Incorrect Username or password";
		}

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

	public static Integer cookieExpiration(Integer minutes) {
		return minutes*60;
	}

}
