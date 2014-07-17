$(document).ready(function(){

   sessionId = getCookie("authenticated_session_id");
   setupMiniSubmenu();
   setupCreatorSearch();

   fillUserInfoMustacheFromCookie();
   setupLogout();


    // var creatorName = window.location.pathname.split('/').pop();
    var creatorName = getParameterByName('creator');

    // Showing or hiding the bid/ask/buy buttons
    simpleFetch(creatorName + "/get_pieces_available").done(function(result) {
        console.log('result = ' + result);
        if (result > 0) {
            $("#buyBtn").removeClass("hide");
        }
    });

    simpleFetch(sessionId + "/" + creatorName + "/get_pieces_owned_value_current").done(function(result) {
        console.log('result = ' + result);
        if (result > 0) {
            $("#askBtn").removeClass("hide");
        }
    });

    if (sessionId != null) {
        $("#bidBtn").removeClass("hide");
    }






    
    $('#page_title').text(creatorName);

    // all <a> tags containing a certain rel=""
    $("a[rel~='keep-params']").click(function(e) {
        e.preventDefault();

        var params = window.location.search,
        dest = $(this).attr('href') + params;

    // in my experience, a short timeout has helped overcome browser bugs
    window.setTimeout(function() {
        window.location.href = dest;
    }, 100);
});

    // fillFieldFromMustache(creatorName + '/get_main_body', 
    //     '#worth_current_template' , '#worth_current', true);



    // fillFieldFromMustache(creatorName + '/get_pieces_owned_value_current_by_creator', 
    //     '#worth_current_template' , '#worth_current', true);

    // fillFieldFromMustache(creatorName + '/get_main_body_by_creator', 
    //     '#worth_current_template' , '#worth_current', true);

$('#bidForm').bootstrapValidator({
  message: 'This value is not valid',
  excluded: [':disabled'],

});

	// The date picker
	$('.datepicker').pickadate({
		format:'yyyy-mm-dd'
	});

	
	console.log(creatorName);

 // Placing the bid
 $( "#placebidBtn" ).click(function( event ) {

	// serializes the form's elements.
	var formData = $("#bidForm").serializeArray();

	// Set the creator id
	var creator = {name:"creatorName", value:creatorName};
	formData.push(creator);

	console.log(formData);

	// Loading
	$(this).button('loading');

	var url = "http://localhost:4567/" + sessionId + "/placebid";


    // username = $('#userLoginDiv').find('input[name="username"]').val();
    // // = $("#inputUsername3").val();
    // document.cookie = "username=" + username ;


    $.ajax({
    	type: "POST",
    	url: url,
    	xhrFields: {
    		withCredentials: true
    	},
    	data: formData,
    	success: function(data, status, xhr) {

       //    console.log(xhr);
    			// console.log(asdf);
    			// console.log(xhr.getAllResponseHeaders()); 

               // alert(data); // show response from the php script.
               
               xhr.getResponseHeader('Set-Cookie');

                // old way
               	// document.cookie="authenticated_session_id=" + data + 
               	// "; expires=" + expireTimeString(60*60); // 1 hour (field is in seconds)
                // Hide the modal, reset the form, show successful
                $("#bidModal").modal('hide');
                $('#bidForm')[0].reset();
                
                toastr.success('Placed bid');
                


            },
            error: function (request, status, error) {
            	toastr.error(request.responseText);
            }



        });

    $("#placebidBtn").button('reset');
    event.preventDefault();
    return false;
});

// Placing the Ask
$( "#placeaskBtn" ).click(function( event ) {

	// serializes the form's elements.
	var formData = $("#askForm").serializeArray();

	// Set the creator id
	var creator = {name:"creatorName", value:creatorName};
	formData.push(creator);

	console.log(formData);

	// Loading
	$(this).button('loading');


	var url = "http://localhost:4567/" + sessionId + "/placeask";


    // username = $('#userLoginDiv').find('input[name="username"]').val();
    // // = $("#inputUsername3").val();
    // document.cookie = "username=" + username ;


    $.ajax({
    	type: "POST",
    	url: url,
    	xhrFields: {
    		withCredentials: true
    	},
    	data: formData,
    	success: function(data, status, xhr) {

       //    console.log(xhr);
    			// console.log(asdf);
    			// console.log(xhr.getAllResponseHeaders()); 

               // alert(data); // show response from the php script.
               
               xhr.getResponseHeader('Set-Cookie');

                // old way
               	// document.cookie="authenticated_session_id=" + data + 
               	// "; expires=" + expireTimeString(60*60); // 1 hour (field is in seconds)
                // Hide the modal, reset the form, show successful
                $("#askModal").modal('hide');
                $('#askForm')[0].reset();
                
                toastr.success('Placed ask');
                


            },
            error: function (request, status, error) {
            	toastr.error(request.responseText);
            }



        });

    $("#placeaskBtn").button('reset');
    event.preventDefault();
    return false;
});





});