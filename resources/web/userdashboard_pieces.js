
$(document).ready(function(){


	sessionId = getCookie("authenticated_session_id");
	console.log(sessionId);



	fillUserHighChartStandardTemplate(sessionId + '/get_pieces_owned_value_accum', "#pieces_owned_value", 'Value ($)', '$');
	fillUserHighChartStandardTemplate(sessionId + '/get_pieces_owned_accum', '#pieces_owned', '# of Pieces owned', '');
	fillUserHighChartPieChartTemplate(sessionId + '/get_pieces_owned_value_current', '#pieces_owned_value_current');
	fillUserHighChartStandardTemplate(sessionId + '/get_rewards_earned', '#rewards_earned', 'Reward ($)', '$');
	fillTableFromMustacheSpecial(sessionId + '/get_users_bids_asks_current', '#users_bids_asks_current_template', '#users_bids_asks_current', '#users_bids_asks_current_table',
		"#remove_button", sessionId);

	
});


function fillTableFromMustacheSpecial(url, templateId, divId, tableId, buttonName, sessionId) {
       var url = "http://localhost:4567/" + url// the script where you handle the form input.
       $.ajax({
       	type: "GET",
       	url: url,
       	xhrFields: {
       		withCredentials: true
       	},
      // data: seriesData, 
      success: function(data, status, xhr) {
      	console.log(data);
            // var jsonObj = JSON.parse(data);
            // JSON.useDateParser();
            // var jsonObj = jQuery.parseJSON(data);
            // JSON.useDateParser();
            var jsonObj = JSON.parse(data);
            
            var template = $(templateId).html();
            Mustache.parse(template);   // optional, speeds up future uses
            var rendered = Mustache.render(template,jsonObj);
            $(divId).html(rendered);
            $(tableId).tablesorter({debug: true}); 
            console.log(jsonObj);              
            console.log(template);
            console.log(rendered);
            $( buttonName ).click(function( event ) {
            	// var id2= $(this).closest("tr")
            	var row = $(this).closest("tr");        // Finds the closest row <tr> 
            	
            	var json = {};
            	json['time_'] = row.find("td:nth-child(1)").text();
    			json['creatorName'] = row.find("td:nth-child(2)").text(); // Finds the 2nd <td> element
    			json['type'] = row.find("td:nth-child(3)").text();


    			console.log(row );
    			console.log(json);

    			var url = "http://localhost:4567/" + sessionId + "/delete_bid_ask";
    			$.ajax({
    				type: "POST",
    				url: url,
    				xhrFields: {
    					withCredentials: true
    				},
    				data: json,
    				success: function(data, status, xhr) {

    					
    					xhr.getResponseHeader('Set-Cookie');


    					
    					toastr.success("Deleted the " + json['type']);
    					


    				},
    				error: function (request, status, error) {
    					toastr.error(request.responseText);
    				}



    			});

    		});

},
error: function (request, status, error) {

	toastr.error(request.responseText);
}
});

}


