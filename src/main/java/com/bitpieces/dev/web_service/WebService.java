package com.bitpieces.dev.web_service;

import static spark.Spark.post;
import static spark.SparkBase.setPort;

import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;

import com.bitpieces.dev.scheduled.ScheduledProcessing;
import com.bitpieces.shared.DataSources;
import com.bitpieces.shared.tools.Tools;
import com.bitpieces.shared.tools.UID;
import com.bitpieces.shared.tools.UnitConverter;
import com.bitpieces.shared.tools.WebCommon;
import com.bitpieces.shared.tools.WebTools;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

// 
// java -cp bitpieces_practice-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.bitpieces.dev.web_service.WebService
public class WebService {
	public static final String COOKIE_PATH = "test";


	// Use an expiring map to store the authenticated sessions
	private static Cache<String, UID> SESSION_TO_USER_MAP = CacheBuilder.newBuilder()
			//			.maximumSize(10000)
			.expireAfterAccess(WebCommon.COOKIE_EXPIRE_SECONDS, TimeUnit.SECONDS) // expire it after its been accessed
			.build();

	// another
	public static void main(String[] args) {
		
		// Load the correct db connection
		Properties prop = Tools.loadProperties(DataSources.DEV_DB_PROP);

		// Load the correct session cache
		SESSION_TO_USER_MAP.putAll(Tools.readObjectFromFile(DataSources.DEV_SESSION_FILE));

		// Set the correct port
		setPort(DataSources.DEV_WEB_PORT);

		// Get an instance of the currency/precision converter
		UnitConverter sf = new UnitConverter();

		// Setup all the common gets
		WebCommon.commonGets(SESSION_TO_USER_MAP, prop, sf, DataSources.DEV_SESSION_FILE, COOKIE_PATH);

		// Setup all the common posts
		WebCommon.commonPosts(SESSION_TO_USER_MAP, prop, sf, DataSources.DEV_SESSION_FILE, COOKIE_PATH);

		// Start the scheduler
		ScheduledProcessing.main(null);

		post("/make_deposit_fake", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, SESSION_TO_USER_MAP, COOKIE_PATH);
				uid.verifyUser();

				message = WebTools.makeDepositFake(uid, req.body(), sf);

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});
		
		post("/make_withdrawal_fake", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, SESSION_TO_USER_MAP, COOKIE_PATH);
				uid.verifyUser();

				message = WebTools.makeUserWithdrawalFake(uid, req.body(), sf);

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});
		
		post("/creator_withdraw_fake", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				UID cid = WebCommon.getUserFromCookie(req, SESSION_TO_USER_MAP, COOKIE_PATH);
				cid.verifyCreator();

				dbInit(prop);

				message = WebTools.makeCreatorWithdrawalFake(cid, req.body(), sf);
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
