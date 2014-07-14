$(document).ready(function() {

  sessionId = getCookie("authenticated_session_id");
  console.log(sessionId);

  // fillUserInfoMustache('get_user_data');

  fillUserInfoMustacheFromCookie();



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
    		
    		
               // arr = $.extend({}, data);
               // var obj = JSON.parse("arr: " + data);

               dataObject = jQuery.parseJSON(data);
                // $.mustache.parse(template);   // optional, speeds up future uses
                var template = $('#piecesownedTemplate').html();
                 Mustache.parse(template);   // optional, speeds up future uses
                 var rendered = Mustache.render(template,dataObject);
              // var rendered = Mustache.render(template, {name : "LUKE"});
              
              console.log(data);
              // console.log(obj);
              
              console.log(template);
              console.log(rendered);

              $('#piecesownedDiv').html(rendered);


              btn.button('reset');
            }
          });
    event.preventDefault();
  });

});

