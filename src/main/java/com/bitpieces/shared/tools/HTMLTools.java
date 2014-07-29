package com.bitpieces.shared.tools;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.bitpieces.shared.Tables.Creators_page_fields;

public class HTMLTools {
	public static void saveCreatorHTMLPage(String username, Creators_page_fields page) {
		
		try {
			String path = Tools.ROOT_DIR + "resources/web/creators_pages/" + username + ".html";

			String pageTemplateFileLoc = Tools.ROOT_DIR + "resources/web/creatorpage_template.html";
			File input = new File(pageTemplateFileLoc);
			Document doc;

			doc = Jsoup.parse(input, "UTF-8");


			Element title = doc.getElementById("page_title").text(username);

			String mainBodySQL = page.getString("main_body");
			Element mainBody = doc.getElementById("main_body").text(mainBodySQL);



			Tools.writeFile(path, StringEscapeUtils.unescapeHtml4(doc.html()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
