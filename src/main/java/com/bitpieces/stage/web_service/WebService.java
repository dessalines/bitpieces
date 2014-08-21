package com.bitpieces.stage.web_service;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.setPort;

import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.SparkBase;

import com.bitpieces.dev.scheduled.ScheduledProcessing;
import com.bitpieces.shared.DataSources;
import com.bitpieces.shared.tools.CoinbaseTools;
import com.bitpieces.shared.tools.Tools;
import com.bitpieces.shared.tools.UID;
import com.bitpieces.shared.tools.UnitConverter;
import com.bitpieces.shared.tools.WebCommon;
import com.bitpieces.shared.tools.WebTools;
import com.coinbase.api.Coinbase;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

// 
// java -cp bitpieces_practice-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.bitpieces.dev.web_service.WebService
public class WebService {
	static final Logger log = LoggerFactory.getLogger(WebService.class);
	
	public static final String COOKIE_PATH = "prod";

	// Use an expiring map to store the authenticated sessions
	private static Cache<String, UID> SESSION_TO_USER_MAP = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.expireAfterAccess(WebCommon.COOKIE_EXPIRE_SECONDS, TimeUnit.SECONDS) // expire it after its been accessed
			.build();

	public static void main(String[] args) {

		
		
		// Set up coinbase for operations
		Coinbase cb = CoinbaseTools.setupCoinbase(DataSources.COINBASE_PROP);

		// Load the correct db connection
		Properties prop = Tools.loadProperties(DataSources.STAGE_DB_PROP);
		
		// Set up the secure keystore
		SparkBase.setSecure(DataSources.KEYSTORE, prop.getProperty("keystorepassword"),null,null);

		// Load the correct session cache
		SESSION_TO_USER_MAP.putAll(Tools.readObjectFromFile(DataSources.STAGE_SESSION_FILE));

		// Set the correct port
		setPort(DataSources.STAGE_WEB_PORT);

		// Get an instance of the currency/precision converter
		UnitConverter sf = new UnitConverter();

		// Setup all the common gets
		WebCommon.commonGets(SESSION_TO_USER_MAP, prop, sf, DataSources.STAGE_SESSION_FILE, COOKIE_PATH);

		// Setup all the common posts
		WebCommon.commonPosts(SESSION_TO_USER_MAP, prop, sf, DataSources.STAGE_SESSION_FILE, COOKIE_PATH);

		// Start the scheduler
		ScheduledProcessing.main(null);

		get("/hello3", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			log.info("okay wrote that");
			return "hi from the bitpieces web service";
		});
		

		get("/deposit_button", (req, res) -> {
			String code = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, SESSION_TO_USER_MAP, COOKIE_PATH);
				uid.verifyUser();

				code = CoinbaseTools.fetchOrCreateDepositButton(cb, uid);

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}

			return code;

		});

		post("/:user_id/coinbase_deposit_callback", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				System.out.println(req.body());
				dbInit(prop);
				String userId = req.params(":user_id");


				WebTools.makeDepositFromCoinbaseCallback(userId, req.body());
				dbClose();
			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});



		post("/user_withdraw", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				UID uid = WebCommon.getUserFromCookie(req, SESSION_TO_USER_MAP, COOKIE_PATH);
				uid.verifyUser();
				dbInit(prop);

				message = WebTools.makeUserWithdrawal(cb, uid, req.body(), sf);
				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			}
			return message;

		});

		post("/creator_withdraw", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				UID cid = WebCommon.getUserFromCookie(req, SESSION_TO_USER_MAP, COOKIE_PATH);
				cid.verifyCreator();

				dbInit(prop);

				message = WebTools.makeCreatorWithdrawal(cb, cid, req.body(), sf);
				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			}
			return message;

		});

	}



	private static final void dbInit(Properties prop) {
		try {
			Base.open("com.mysql.jdbc.Driver", 
					prop.getProperty("dburl"), 
					prop.getProperty("dbuser"), 
					prop.getProperty("dbpassword"));
		} catch (DBException e) {
			dbClose();
			dbInit(prop);
		}


	}

	private static final void dbClose() {
		Base.close();
	}






}
