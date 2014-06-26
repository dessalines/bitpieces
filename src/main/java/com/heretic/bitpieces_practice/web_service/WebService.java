package com.heretic.bitpieces_practice.web_service;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.javalite.activejdbc.Base;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.heretic.bitpieces_practice.actions.Actions;
import com.heretic.bitpieces_practice.tools.Tools;

public class WebService {

	// How long to keep teh cookies
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
			
			json = "{\"list\": " + json + "}";

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
				String authSession = verifyLogin(userId, Tools.generateSecureRandom());
			
				// Set some cookies for that users login
				res.cookie("derp2", "k", COOKIE_EXPIRE_SECONDS, false);
				res.cookie("authenticated_session_id", authSession, COOKIE_EXPIRE_SECONDS, false);

				// TODO make sure that username doesn't already exist
				// make a unique index on the DB for usernames
				return "user registered";
			} else {
				return "user not created";
			}

		});

		post("/userlogin", (req, res) -> {
			res.header("Access-Control-Allow-Origin", "http://localhost");
			res.header("Access-Control-Allow-Credentials", "true");

			dbInit(prop);



			// log the user in
			String userId = Actions.userLogin(req.body());

			String authSession = verifyLogin(userId, Tools.generateSecureRandom());

			// Set some cookies for that users login
			res.cookie("derp2", "k", COOKIE_EXPIRE_SECONDS, false);
			res.cookie("authenticated_session_id", authSession, COOKIE_EXPIRE_SECONDS, false);


			dbClose();

			System.out.println(GSON.toJson(SESSION_TO_USER_MAP));

			return "user logged in";

		});


	}

	private static String verifyLogin(String userId, String authenticatedSession) {
		if (userId != null) {
			// Put the users ID in the session
			//				req.session().attribute("userId", userId); // put the user id in the session data

			// Store the users Id in a static map, give them a session id
			SESSION_TO_USER_MAP.put(authenticatedSession, userId);

			//			res.cookie("/", "auth2", authenticatedSession, 200000, false);
			//				

			return authenticatedSession;
		} else {
			return "Incorrect Username or password";
		}

	}

	private static void getPiecesOwned(String userId) {
		// TODO Auto-generated method stub

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
