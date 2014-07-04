package com.heretic.bitpieces_practice.web_service;

import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import com.heretic.bitpieces_practice.actions.Actions;
import com.heretic.bitpieces_practice.tables.Tables.Creators_page_fields;
import com.heretic.bitpieces_practice.tools.Tools;

public class WebTools {

	public static String saveCreatorPage(String id, String reqBody) {
		
		Map<String, String> postMap = Tools.createMapFromAjaxPost(reqBody);
		
		Creators_page_fields page = Creators_page_fields.findFirst("creators_id = ?",  id);
		
		// The first time filling the page fields
		if (page == null) {
		page = Creators_page_fields.createIt("creators_id", id,
				"main_body", postMap.get("main_body"));
		} else {
			page.set("main_body", postMap.get("main_body")).saveIt();
		}
		
		// Save the html page
		HTMLTools.saveCreatorHTMLPage(id, page);
		
		return "Successful";
		
	}

	

	public static String placeBid(String userId, String body) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);
		
		Actions.createBid(userId, 
				postMap.get("creatorid"), 
				Integer.valueOf(postMap.get("pieces")), 
				Double.valueOf(postMap.get("bid")), 
				postMap.get("validUntil"), 
				true);
				
		
		return body;
	}
	
	public static String placeAsk(String userId, String body) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);
		
		Actions.createAsk(userId, 
				postMap.get("creatorid"), 
				Integer.valueOf(postMap.get("pieces")), 
				Double.valueOf(postMap.get("ask")), 
				postMap.get("validUntil"), 
				true);
				
		
		return body;
	}


}
