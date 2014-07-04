package com.heretic.bitpieces_practice.web_service;

import org.apache.commons.lang3.StringEscapeUtils;

import com.heretic.bitpieces_practice.tables.Tables.Creators_page_fields;
import com.heretic.bitpieces_practice.tools.Tools;

public class HTMLTools {
	public static void saveCreatorHTMLPage(String id, Creators_page_fields page) {

		String mainBody = page.getString("main_body");

		String path = Tools.ROOT_DIR + "resources/web/creators_pages/" + id + ".html";

		String html = "&lt;!DOCTYPE html&gt;\n"+
				"&lt;html lang=&quot;en&quot;&gt;\n"+
				"&lt;head&gt;\n"+
				" &lt;meta charset=&quot;utf-8&quot;&gt;\n"+
				" &lt;meta http-equiv=&quot;X-UA-Compatible&quot; content=&quot;IE=edge&quot;&gt;\n"+
				" &lt;meta name=&quot;viewport&quot; content=&quot;width=device-width, initial-scale=1&quot;&gt;\n"+
				" &lt;meta name=&quot;description&quot; content=&quot;&quot;&gt;\n"+
				" &lt;meta name=&quot;author&quot; content=&quot;&quot;&gt;\n"+
				" &lt;link rel=&quot;icon&quot; href=&quot;../../favicon.ico&quot;&gt;\n"+
				"\n"+
				" &lt;title&gt;Starter Template for Bootstrap&lt;/title&gt;\n"+
				"\n"+
				" &lt;!-- Bootstrap core CSS --&gt;\n"+
				" &lt;link href=&quot;../darkly.bootstrap.min.css&quot; rel=&quot;stylesheet&quot;&gt;\n"+
				"\n"+
				" &lt;!-- Link to font awesome --&gt;\n"+
				" &lt;link rel=&quot;stylesheet&quot; href=&quot;../font-awesome/css/font-awesome.min.css&quot;&gt;\n"+
				"\n"+
				" &lt;!-- Bootstrap social css --&gt;\n"+
				" &lt;link href=&quot;../bootstrap-social-gh-pages/bootstrap-social.css&quot; rel=&quot;stylesheet&quot;&gt;\n"+
				"\n"+
				" &lt;!-- Bootstrap validator --&gt;\n"+
				" &lt;link rel=&quot;stylesheet&quot; href=&quot;../bootstrap-validator/dist/css/bootstrapValidator.min.css&quot;/&gt;\n"+
				"\n"+
				" &lt;!-- toastr css --&gt;\n"+
				" &lt;link href=&quot;../toastr/toastr.css&quot; rel=&quot;stylesheet&quot;/&gt;\n"+
				"\n"+
				"\t<!-- Pickadate -->\n"+
				"\t<link href=\"../pickadate/lib/themes/default.css\" rel=\"stylesheet\"/>\n"+
				"\t<link href=\"../pickadate/lib/themes/default.date.css\" rel=\"stylesheet\"/>"+
				"\n"+
				" &lt;!-- This main css --&gt;\n"+
				" &lt;link href=&quot;../creators.css&quot; rel=&quot;stylesheet&quot;&gt;\n"+
				"\n"+
				" &lt;!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries --&gt;\n"+
				" &lt;!--[if lt IE 9]&gt;\n"+
				" &lt;script src=&quot;https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;https://oss.maxcdn.com/respond/1.4.2/respond.min.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;![endif]--&gt;\n"+
				" &lt;/head&gt;\n"+
				"\n"+
				" &lt;body&gt;\n"+
				"\n"+
				" &lt;!-- NAVBAR\n"+
				" ================================================== --&gt;\n"+
				" &lt;div class=&quot;navbar-wrapper&quot;&gt;\n"+
				" &lt;div class=&quot;container&quot;&gt;\n"+
				"\n"+
				" &lt;div class=&quot;navbar navbar-default navbar-fixed-top&quot; role=&quot;navigation&quot;&gt;\n"+
				" &lt;div class=&quot;container&quot;&gt;\n"+
				" &lt;div class=&quot;navbar-header&quot;&gt;\n"+
				" &lt;button type=&quot;button&quot; class=&quot;navbar-toggle&quot; data-toggle=&quot;collapse&quot; data-target=&quot;.navbar-collapse&quot;&gt;\n"+
				" &lt;span class=&quot;sr-only&quot;&gt;Toggle navigation&lt;/span&gt;\n"+
				" &lt;span class=&quot;icon-bar&quot;&gt;&lt;/span&gt;\n"+
				" &lt;span class=&quot;icon-bar&quot;&gt;&lt;/span&gt;\n"+
				" &lt;span class=&quot;icon-bar&quot;&gt;&lt;/span&gt;\n"+
				" &lt;/button&gt;\n"+
				" &lt;a class=&quot;navbar-brand&quot; href=&quot;#&quot;&gt;BitPieces&lt;/a&gt;\n"+
				" &lt;/div&gt;\n"+
				" &lt;div class=&quot;navbar-collapse collapse&quot;&gt;\n"+
				" &lt;ul class=&quot;nav navbar-nav&quot;&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;/carousel&quot;&gt;Home&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;#discover&quot;&gt;Discover&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li class=&quot;active&quot;&gt;&lt;a href=&quot;/creators&quot;&gt;Creators&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;/userdashboard&quot; id=&quot;dashboardhref&quot; class=&quot;hide&quot;&gt;Dashboard&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;#login&quot; id=&quot;loginhref&quot; data-toggle=&quot;modal&quot; data-target=&quot;#userloginModal&quot;&gt;Login/Register&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;#logout&quot; id=&quot;logouthref&quot; class=&quot;hide&quot;&gt;Log Out&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li class=&quot;dropdown&quot;&gt;\n"+
				" &lt;a href=&quot;#&quot; class=&quot;dropdown-toggle&quot; data-toggle=&quot;dropdown&quot;&gt;Dropdown &lt;b class=&quot;caret&quot;&gt;&lt;/b&gt;&lt;/a&gt;\n"+
				" &lt;ul class=&quot;dropdown-menu&quot;&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;#&quot;&gt;Action&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;#&quot;&gt;Another action&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;#&quot;&gt;Something else here&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li class=&quot;divider&quot;&gt;&lt;/li&gt;\n"+
				" &lt;li class=&quot;dropdown-header&quot;&gt;Nav header&lt;/li&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;#&quot;&gt;Separated link&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;#&quot;&gt;One more separated link&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;/ul&gt;\n"+
				" &lt;/li&gt;\n"+
				" &lt;/ul&gt;\n"+
				" &lt;form class=&quot;navbar-form navbar-right&quot;&gt;\n"+
				" &lt;input type=&quot;text&quot; class=&quot;form-control&quot; placeholder=&quot;Search...&quot;&gt;\n"+
				" &lt;/form&gt;\n"+
				" &lt;/div&gt;\n"+
				" &lt;/div&gt;\n"+
				" &lt;/div&gt;\n"+
				"\n"+
				" &lt;/div&gt;\n"+
				" &lt;/div&gt;\n"+
				"\n"+
				"\n"+
				// Here's the container
				" &lt;div class=&quot;container&quot;&gt;\n"+
				"\n"+
				" &lt;div class=&quot;starter-template&quot;&gt;\n"+
				" &lt;h1&gt;Creator # " + id + " Page &lt;/h1&gt;\n"+
				" &lt;p class=&quot;lead&quot;&gt;" + mainBody + "&lt;/p&gt;\n"+
				" &lt;/div&gt;\n"+

	 			"&lt;div class=&quot;row&quot;&gt;\n"+
	 			"	&lt;div class=&quot;col-md-12&quot;&gt;\n"+
	 			"<button id=\"bidBtn\" type=\"button\" class=\"btn btn-primary\" data-toggle=\"modal\" data-target=\"#bidModal\">Bid</button>\n"+
	 			"<button id=\"askBtn\" type=\"button\" class=\"btn btn-primary\" data-toggle=\"modal\" data-target=\"#askModal\">Ask</button>\n"+
	 			"&lt;/div&gt;\n"+
	 			"&lt;/div&gt;\n" +
	 			"&lt;/div&gt;\n" + 
	 			" &lt;/div&gt;&lt;!-- /.container --&gt;\n"+

				// End of the container

				// Start of modal
				"<!-- Modals -->\n"+
				" \t<div id=\"bidModal\" class=\"modal fade bs-example-modal-sm\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"mySmallModalLabel\" aria-hidden=\"true\">\n"+
				" \t\t<div class=\"modal-dialog modal-sm\">\n"+
				"\n"+
				" \t\t\t<div class=\"modal-content\">\n"+
				" \t\t\t\t<div class=\"modal-header\">\n"+
				" \t\t\t\t\t<h4 class=\"modal-title\">Bid</h4>\n"+
				" \t\t\t\t</div>\n"+
				" \t\t\t\t<div class=\"modal-body\">\n"+
				" \t\t\t\t\t<form id=\"bidForm\" class=\"form-horizontal\" role=\"form\">\n"+
				" \t\t\t\t\t\t<div class=\"form-group form-group-lg\">\n"+
				" \t\t\t\t\t\t\t<label class=\"col-sm-2 control-label\" for=\"formGroupInputLarge\">Price</label>\n"+
				" \t\t\t\t\t\t\t<div class=\"col-sm-10\">\n"+
				" \t\t\t\t\t\t\t\t<input name=\"bid\" class=\"form-control\" type=\"text\" id=\"formGroupInputLarge\" placeholder=\"Last price\"\n"+
				" \t\t\t\t\t\t\t\tdata-bv-greaterthan=\"true\"\n"+
				" \t\t\t\t\t\t\t\tdata-bv-greaterthan-value=\".01\"\n"+
				" \t\t\t\t\t\t\t\tdata-bv-greaterthan-message=\"Must be > .01\"\n"+
				"\n"+
				" \t\t\t\t\t\t\t\t>\n"+
				" \t\t\t\t\t\t\t</div>\n"+
				" \t\t\t\t\t\t</div>\n"+
				"\n"+
				" \t\t\t\t\t\t<div class=\"form-group form-group-lg\">\n"+
				" \t\t\t\t\t\t\t<label class=\"col-sm-2 control-label\" for=\"formGroupInputLarge\">Pieces</label>\n"+
				" \t\t\t\t\t\t\t<div class=\"col-sm-10\">\n"+
				" \t\t\t\t\t\t\t\t<input name=\"pieces\" class=\"form-control\" type=\"text\" id=\"formGroupInputLarge\" placeholder=\"Pieces\"\n"+
				" \t\t\t\t\t\t\t\ttype=\"text\" \n"+
				"\t\t\t\t\t\t\t\tdata-bv-integer=\"true\"\n"+
				" \t\t\t\t\t\t\t\tdata-bv-integer-message=\"Must be a whole number\"\n"+
				"\n"+
				" \t\t\t\t\t\t\t\t>\n"+
				" \t\t\t\t\t\t\t</div>\n"+
				" \t\t\t\t\t\t</div>\n"+
				"\n"+
				" \t\t\t\t\t\t<div class=\"form-group form-group-lg\">\n"+
				" \t\t\t\t\t\t\t<label class=\"col-sm-2 control-label\" for=\"formGroupInputLarge\">Date</label>\n"+
				" \t\t\t\t\t\t\t<div class=\"col-sm-10\">\n"+
				" \t\t\t\t\t\t\t\t<input name=\"validUntil\" class=\"form-control datepicker\" type=\"text\" id=\"formGroupInputLarge\" placeholder=\"Valid Until...\">\n"+
				" \t\t\t\t\t\t\t</div>\n"+
				" \t\t\t\t\t\t</div>\n"+
				"\n"+
				" \t\t\t\t\t\t<button id=\"placebidBtn\" type=\"submit\" class=\"btn btn-primary\">Place Bid</button>\n"+
				" \t\t\t\t\t</form>\n"+
				"\n"+
				" \t\t\t\t</div>\n"+
				" \t\t\t\t\n"+
				" \t\t\t</div>\n"+
				" \t\t</div>\n"+
				" \t</div>" + 
				"\n"+
				" \t<div id=\"askModal\" class=\"modal fade bs-example-modal-sm\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"mySmallModalLabel\" aria-hidden=\"true\">\n"+
				" \t\t<div class=\"modal-dialog modal-sm\">\n"+
				"\n"+
				" \t\t\t<div class=\"modal-content\">\n"+
				" \t\t\t\t<div class=\"modal-header\">\n"+
				" \t\t\t\t\t<h4 class=\"modal-title\">Ask</h4>\n"+
				" \t\t\t\t</div>\n"+
				" \t\t\t\t<div class=\"modal-body\">\n"+
				" \t\t\t\t\t<form id=\"askForm\" class=\"form-horizontal\" role=\"form\">\n"+
				" \t\t\t\t\t\t<div class=\"form-group form-group-lg\">\n"+
				" \t\t\t\t\t\t\t<label class=\"col-sm-2 control-label\" for=\"formGroupInputLarge\">Price</label>\n"+
				" \t\t\t\t\t\t\t<div class=\"col-sm-10\">\n"+
				" \t\t\t\t\t\t\t\t<input name=\"ask\" class=\"form-control\" type=\"text\" id=\"formGroupInputLarge\" placeholder=\"Last price\"\n"+
				" \t\t\t\t\t\t\t\tdata-bv-greaterthan=\"true\"\n"+
				" \t\t\t\t\t\t\t\tdata-bv-greaterthan-value=\".01\"\n"+
				" \t\t\t\t\t\t\t\tdata-bv-greaterthan-message=\"Must be > .01\"\n"+
				"\n"+
				" \t\t\t\t\t\t\t\t>\n"+
				" \t\t\t\t\t\t\t</div>\n"+
				" \t\t\t\t\t\t</div>\n"+
				"\n"+
				" \t\t\t\t\t\t<div class=\"form-group form-group-lg\">\n"+
				" \t\t\t\t\t\t\t<label class=\"col-sm-2 control-label\" for=\"formGroupInputLarge\">Pieces</label>\n"+
				" \t\t\t\t\t\t\t<div class=\"col-sm-10\">\n"+
				" \t\t\t\t\t\t\t\t<input name=\"pieces\" class=\"form-control\" type=\"text\" id=\"formGroupInputLarge\" placeholder=\"Pieces\"\n"+
				" \t\t\t\t\t\t\t\ttype=\"text\" \n"+
				"\t\t\t\t\t\t\t\tdata-bv-integer=\"true\"\n"+
				" \t\t\t\t\t\t\t\tdata-bv-integer-message=\"Must be a whole number\"\n"+
				"\n"+
				" \t\t\t\t\t\t\t\t>\n"+
				" \t\t\t\t\t\t\t</div>\n"+
				" \t\t\t\t\t\t</div>\n"+
				"\n"+
				" \t\t\t\t\t\t<div class=\"form-group form-group-lg\">\n"+
				" \t\t\t\t\t\t\t<label class=\"col-sm-2 control-label\" for=\"formGroupInputLarge\">Date</label>\n"+
				" \t\t\t\t\t\t\t<div class=\"col-sm-10\">\n"+
				" \t\t\t\t\t\t\t\t<input name=\"validUntil\" class=\"form-control datepicker\" type=\"text\" id=\"formGroupInputLarge\" placeholder=\"Valid Until...\">\n"+
				" \t\t\t\t\t\t\t</div>\n"+
				" \t\t\t\t\t\t</div>\n"+
				"\n"+
				" \t\t\t\t\t\t<button id=\"placeaskBtn\" type=\"submit\" class=\"btn btn-primary\">Place Ask</button>\n"+
				" \t\t\t\t\t</form>\n"+
				"\n"+
				" \t\t\t\t</div>\n"+
				" \t\t\t\t\n"+
				" \t\t\t</div>\n"+
				" \t\t</div>\n"+
				" \t</div>\n"+


				"\n"+
				"\n"+
				" &lt;!-- Bootstrap core JavaScript\n"+
				" ================================================== --&gt;\n"+
				" &lt;!-- Placed at the end of the document so the pages load faster --&gt;\n"+
				" &lt;script src=&quot;https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;../bootstrap-dist/js/bootstrap.min.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;../../assets/js/docs.min.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script type=&quot;text/javascript&quot; src=&quot;../bootstrap-validator/dist/js/bootstrapValidator.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;../toastr/toastr.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;../mustache/mustache.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;../holder/holder.js&quot;&gt;&lt;/script&gt;\n"+
				" <script src=\"../pickadate/lib/picker.js\"></script>\n"+
				" <script src=\"../pickadate/lib/picker.date.js\"></script>\n"+
				"\n"+
				"\n"+
				" &lt;!-- my scripts --&gt;\n"+
				" &lt;script src=&quot;../tools.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;../login.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;../creators.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;creatorpage.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;/body&gt;\n"+
				" &lt;/html&gt;\n"+
				"";

		Tools.writeFile(path, StringEscapeUtils.unescapeHtml4(html));
	}
}
