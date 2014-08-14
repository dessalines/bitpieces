package com.bitpieces.shared.tools;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;

import com.bitpieces.shared.DataSources;
import com.google.common.cache.Cache;

public class WebCommon {

	// How long to keep the cookies
	public static final Integer COOKIE_EXPIRE_SECONDS = cookieExpiration(1440);

	static final Logger log = LoggerFactory.getLogger(WebCommon.class);
	
	
	
	
	/**
	 * This needs the cache, to get the correct user, a properties file for making the 
	 * correct db connections, and a unit converter to convert everything correctly
	 * @param cache
	 * @param prop
	 * @param sf
	 */
	public static void commonGets(Cache<String, UID> cache, 
			Properties prop, 
			UnitConverter sf,
			String cacheFile,
			String cookiePath) {

		get("/hello", (req, res) -> {
			allowResponseHeaders(req, res);
			log.debug("okay wrote that");
			res.cookie("testypoo", "test");
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


		get("/get_users_settings", (req, res) -> {
			String json = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				//				verifyUser(uid);

				// get currency if one exists
				json = WebTools.getUsersSettingsJson(uid);

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});

		get("/:user/get_pieces_owned_value_accum", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String userName = req.params(":user");
		UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {

				dbInit(prop);
				// get currency if one exists

				json = WebTools.getPiecesOwnedValueAccumSeriesJson(userName, uid, sf);


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});

		get("/:user/get_pieces_owned_value_current", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String userName = req.params(":user");
		UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {


				dbInit(prop);
				json = WebTools.getPiecesOwnedValueCurrentSeriesJson(userName, uid, sf);


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});

		get("/:user/:creator/get_pieces_owned_value_current", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			try {
				String userName = req.params(":user");
				String creatorName = req.params(":creator");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);

				dbInit(prop);
				json = WebTools.getPiecesOwnedValueCurrentSeriesJson(userName, creatorName, uid, sf);


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});

		get("/:user/:creator/get_pieces_owned_current", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;

			try {
				String userName = req.params(":user");
				String creatorName = req.params(":creator");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);

				dbInit(prop);
				json = WebTools.getPiecesOwnedCurrentSeriesJson(userName, creatorName, uid, sf);


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});



		get("/:user/get_prices_for_user", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			try {
				String userName = req.params(":user");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);


				dbInit(prop);
				json = WebTools.getPricesForUserSeriesJson(userName, uid, sf);

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});

		get("/:user/get_rewards_earned_accum", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			try {
				String userName = req.params(":user");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);


				dbInit(prop);
				json = WebTools.getRewardsEarnedAccumSeriesJson(userName, uid, sf);


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});

		get("/:user/get_rewards_earned_total", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			try {
				String userName = req.params(":user");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				dbInit(prop);
				json = WebTools.getRewardsEarnedTotalJson(userName, uid, sf);

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});

		get("/:user/get_pieces_owned_accum", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			try {
				String userName = req.params(":user");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				dbInit(prop);
				json = WebTools.getPiecesOwnedAccumSeriesJson(userName, uid, sf);


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});
		get("/:user/get_users_funds_accum", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			try {
				String userName = req.params(":user");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				dbInit(prop);
				json = WebTools.getUsersFundsAccumSeriesJson(userName, uid, sf);


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});


		get("/:user/get_users_transactions/:page_num", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			try {
				String userName = req.params(":user");
				Integer pageNum = Integer.parseInt(req.params(":page_num"));
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				dbInit(prop);
				json = WebTools.getUsersTransactionsJson(userName, uid, sf, pageNum);


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});

		get("/:user/get_users_activity/:page_num", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			try {
				String userName = req.params(":user");
				Integer pageNum = Integer.parseInt(req.params(":page_num"));
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				dbInit(prop);

				// get currency if one exists
				json = WebTools.getUsersActivityJson(userName, uid, sf, pageNum);


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});

		get("/:user/get_users_funds_current", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			try {
				String userName = req.params(":user");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				dbInit(prop);
				json = WebTools.getUsersFundsCurrentJson(userName, uid, sf);

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});

		get("/:creator/get_creators_funds_current", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			try {
				String creatorName = req.params(":creator");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				dbInit(prop);
				json = WebTools.getCreatorsFundsCurrentJson(creatorName, uid, sf);

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});

		get("/:user/get_rewards_earned_total_by_user", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			try {
				String userName = req.params(":user");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				dbInit(prop);
				json = WebTools.getRewardsEarnedTotalByUserJson(userName, uid, sf);

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});

		get("/:user/get_pieces_value_current_by_owner", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			try {
				String userName = req.params(":user");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				dbInit(prop);
				json = WebTools.getPiecesValueCurrentByOwnerJson(userName, uid, sf);

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});

		get("/:user/get_users_reputation", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			try {
				String userName = req.params(":user");

				dbInit(prop);
				json = WebTools.getUsersReputationJson(userName);

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});

		get("/:user/get_users_bids_asks_current", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			try {
				String userName = req.params(":user");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				dbInit(prop);
				json = WebTools.getUsersBidsAsksCurrentJson(userName, uid, sf);

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});

		get("/creators_search/:query", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			dbInit(prop);

			String query = req.params(":query");

			String json = WebTools.creatorsSearchJson(query);

			dbClose();

			System.out.println(json);
			return json;


		});

		get("/get_categories", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			dbInit(prop);

			String json = WebTools.getCategoriesJson(req.body());

			dbClose();

			System.out.println(json);
			return json;


		});

		get("/get_currencies", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			dbInit(prop);

			String json = WebTools.getCurrenciesJson(req.body());

			dbClose();

			System.out.println(json);
			return json;


		});

		get("/getcreatorpage", (req, res) -> {
			String json = null;
			try {			
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID cid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				cid.verifyCreator();

				// get the creator id from the token		
				json = WebTools.getCreatorPageJson(cid.getId(), req.body());

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_pieces_owned_value_current_by_creator", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {			

				dbInit(prop);

				// get the creator id from the token		
				json = WebTools.getPiecesOwnedValueCurrentByCreatorJson(creator, uid, sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_funds_raised", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {			

				dbInit(prop);

				// get the creator id from the token		
				json = WebTools.getFundsRaisedJson(creator, uid, sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_safety_current", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			try {			

				dbInit(prop);

				// get the creator id from the token		
				json = WebTools.getSafetyCurrentJson(creator);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_verified", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			try {			

				dbInit(prop);

				// get the creator id from the token		
				json = WebTools.getVerifiedJson(creator);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_price_per_piece_current", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getPricePerPieceCurrentJson(creator, uid, sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_rewards_owed", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getRewardsOwedJson(creator, uid, sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_pieces_owned_value_current_creator", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getPiecesOwnedValueCurrentCreatorSeriesJson(creator, uid, sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_backers_current_count", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");

			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getBackersCurrentCountJson(creator);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_main_body", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getMainBodyJson(creator, req.body());

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_pricing", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getPricesJson(creator, uid, sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_safety", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getSafetyJson(creator, uid, sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});


		get("/:creator/get_worth", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getWorthJson(creator, uid, sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_bids_asks_current/:page_num", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			Integer pageNum = Integer.parseInt(req.params(":page_num"));

			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {			

				dbInit(prop);

				// get the creator id from the token	

				json = WebTools.getBidsAsksCurrentJson(creator, uid, sf, pageNum);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});



		get("/:creator/get_rewards/:page_num", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			Integer pageNum = Integer.parseInt(req.params(":page_num"));
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getRewardsJson(creator, uid, sf, pageNum);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_rewards_current", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");

			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getRewardsCurrentJson(creator, uid, sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_rewards_owed_to_user/:page_num", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			Integer pageNum = Integer.parseInt(req.params(":page_num"));
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getRewardsOwedToUserJson(creator,uid, sf, pageNum);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_pieces_issued/:page_num", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			Integer pageNum = Integer.parseInt(req.params(":page_num"));
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getPiecesIssuedJson(creator, uid, sf, pageNum);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_backers_current/:page_num", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			Integer pageNum = Integer.parseInt(req.params(":page_num"));
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getBackersCurrentJson(creator, uid, sf, pageNum);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_creators_reputation", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");

			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getCreatorsReputationJson(creator);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_pieces_available", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getPiecesAvailableJson(creator);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_pieces_available_total", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getPiecesAvailableTotalJson(creator);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_pieces_owned_total", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getPiecesOwnedTotalJson(creator);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_creators_activity/:page_num", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			Integer pageNum = Integer.parseInt(req.params(":page_num"));
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getCreatorsActivityJson(creator, uid, sf, pageNum);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_creators_transactions/:page_num", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			Integer pageNum = Integer.parseInt(req.params(":page_num"));
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getCreatorsTransactionsJson(creator, uid, sf, pageNum);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_creators_funds_accum", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getCreatorsFundsAccumJson(creator,uid, sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_pieces_issued_most_recent_price", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getPiecesIssuedMostRecentPriceJson(creator, uid, sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});
	}

	public static void commonPosts(Cache<String, UID> cache, 
			Properties prop, 
			UnitConverter sf,
			String cacheFile, 
			String cookiePath) {
		
		post("/userlogin", (req, res) -> {
			System.out.println(req.headers("Origin"));
			WebCommon.allowResponseHeaders(req, res);

			dbInit(prop);

			// log the user in
			UID uid = DBActions.userLogin(req.body());

			dbClose();

			String message = WebCommon.verifyLoginAndSetCookies(uid, req, res, cache, 
					cacheFile, cookiePath);

			return message;

		});

		post("/creatorlogin", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);

			dbInit(prop);

			// log the user in
			UID uid = DBActions.creatorLogin(req.body());

			dbClose();


			String message = WebCommon.verifyLoginAndSetCookies(uid, req, res, cache, 
					cacheFile, cookiePath);

			return message;

		});

		post("/save_settings", (req, res) -> {
			String json = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);

				// get currency if one exists
				json = WebTools.saveSettings(uid, req.body());

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});

		post("/save_creators_categories", (req, res) -> {
			String json = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID cid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				cid.verifyCreator();

				// get currency if one exists
				json = WebTools.saveCreatorsCategories(cid, req.body());

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});




		post("/discover", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			dbInit(prop);
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			String json = WebTools.getDiscoverJson(req.body(), uid, sf);

			dbClose();

			System.out.println(json);
			return json;


		});

		post("/user_logout", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			String auth = req.cookie("authenticated_session_id" + "_" + cookiePath);


			// remove the key, and save the map
			cache.invalidate(auth);
			writeCacheToFile(cache, cacheFile);



			return "Logged out";

		});

		post("/change_password", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			try {
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);


				String message = WebTools.verifyAndChangePassword(uid, req.body());

				dbClose();

				return message;
			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}

		});








		




		post("/savecreatorpage", (req, res) -> {
			String message = null;
			try {			
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID cid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				cid.verifyCreator();



				// get the creator id from the token		
				message = WebTools.saveCreatorPage(cid.getId(), req.body());

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}


			return message;

		});
		
		

		post("/registeruser", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			dbInit(prop);
			try {
				// Verify the recaptcha
				WebTools.recaptcha(req.url(), req.body());

				// Create the user
				UID uid = DBActions.createUserRealFromAjax(req.body());

				dbClose();

				// Its null if it couldn't create the user, usually cause of constraints
				if (uid != null) {
					String message = WebCommon.verifyLoginAndSetCookies(uid, req, res, cache, 
							cacheFile, cookiePath);

					return "user registered";
				} else {

					res.status(666);
					return "User already exists";
				}

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}

		});

		post("/registercreator", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			dbInit(prop);
			try {
				// Verify the recaptcha
				WebTools.recaptcha(req.url(), req.body());

				// Create the user
				UID uid = DBActions.createCreatorRealFromAjax(req.body());

				dbClose();

				// Its null if it couldn't create the user, usually cause of constraints
				if (uid != null) {
							String message = WebCommon.verifyLoginAndSetCookies(uid, req, res, cache, 
							cacheFile, cookiePath);

					return "creator registered";
				} else {

					res.status(666);
					return "Creator already exists";
				}
			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}

		});

		post("/placebid", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);

				uid.verifyUser();

				message = WebTools.placeBid(uid, req.body(), sf);

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});

		post("/placeask", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);

			dbInit(prop);

			// get the creator id from the token
			UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
			String message = null;
			try {
				message = WebTools.placeAsk(uid, req.body(), sf);
			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}

			dbClose();


			return message;

		});



		post("/placebuy", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				uid.verifyUser();

				message = WebTools.placeBuy(uid, req.body());

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});

		post("/issue_pieces", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				uid.verifyCreator();

				message = WebTools.issuePieces(uid, req.body(), sf);

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});

		post("/new_reward", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				uid.verifyCreator();

				message = WebTools.newReward(uid, req.body(), sf);

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});

		post("/raise_funds", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				uid.verifyCreator();

				message = WebTools.raiseFunds(uid, req.body(), sf);

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});

		post("/delete_bid_ask", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, cache, cookiePath);
				uid.verifyUser();

				message = WebTools.deleteBidAsk(uid, req.body());

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});
		
		
		
		
		
		
	}


	public static void allowResponseHeaders(Request req, Response res) {
		String origin = req.headers("Origin");
		res.header("Access-Control-Allow-Credentials", "true");
		//		System.out.println(origin);
		if (DataSources.ALLOW_ACCESS_ADDRESSES.contains(req.headers("Origin"))) {
			res.header("Access-Control-Allow-Origin", origin);
		}

	}

	public static UID getUserFromCookie(Request req, Cache<String, UID> cache, String path) {
		String authId = req.cookie("authenticated_session_id" + "_" + path);

		UID uid = null;
		try {
			uid = cache.getIfPresent(authId);
		} catch(NullPointerException e) {
			System.err.println("No such user logged in");
			return null;
		}

		return uid;
	}

	public static final void dbInit(Properties prop) {
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

	public static final void dbClose() {
		Base.close();
	}


	private static void writeCacheToFile(Cache<String, UID> cache, String file) {
		Map<String, UID> serializableMap = new HashMap<String, UID>(cache.asMap());
		Tools.writeObjectToFile(serializableMap, file);
	}

	public static String verifyLoginAndSetCookies(UID uid, Request req, Response res, 
			Cache<String, UID> cache, String cacheFile, String path) {
		if (uid != null) {
			Integer expireSeconds = Tools.getExpireTime(req.body());
			String authenticatedSession = Tools.generateSecureRandom();
			// Put the users ID in the session
			//				req.session().attribute("userId", userId); // put the user id in the session data

			// Store the users Id in a static map, give them a session id
			cache.put(authenticatedSession, uid);
			writeCacheToFile(cache, cacheFile);


			// Set some cookies for that users login
			res.cookie("authenticated_session_id_" + path, authenticatedSession, expireSeconds);
			res.cookie("username_" + path, uid.getUsername(), expireSeconds);
			res.cookie("usertype_" + path, uid.getType().toString(), expireSeconds);
			String json = Tools.GSON2.toJson(cache);
			System.out.println(json);
			res.cookie("test", "testaa");


			return authenticatedSession;
		} else {
			res.status(666);
			return "Incorrect Username or password";
		}

	}


	public static Integer cookieExpiration(Integer minutes) {
		return minutes*60;
	}

}
