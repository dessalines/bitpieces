$(document).ready(function() {


	$( "#helloBtn" ).click(function() {
		var btn = $(this)
		btn.button('loading')
	// serializes the form's elements.
	console.log("clicked hello button");


    var url = "http://localhost:4567/session"; // the script where you handle the form input.

    $.ajax({
    	type: "GET",
    	url: url,
    	success: function(data, status, xhr) {
    		
    		
               alert(data); // show response from the php script.
               btn.button('reset');
           }
       });
    event.preventDefault();
});


	$( "#piecesownedtotalBtn" ).click(function() {
		var btn = $(this)
		btn.button('loading')
	// serializes the form's elements.
	sessionId = getCookie("authenticated_session_id");
	console.log(sessionId);


    var url = "http://localhost:4567/" + sessionId + "/getpiecesownedtotal"// the script where you handle the form input.

    $.ajax({
    	type: "GET",
    	url: url,
    	success: function(data, status, xhr) {
    		
    		
               alert(data); // show response from the php script.
               btn.button('reset');
           }
       });
    event.preventDefault();
});

});