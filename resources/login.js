$(document).ready(function(){


    $('#registerForm').bootstrapValidator({
        message: 'This value is not valid',
        excluded: [':disabled'],
     
    });

	$('#registerBtn').button();

	// $('#registerForm').submit(function(){
	// 	$.each($('#registerForm').serializeArray(), function(i, field) {
 //   			 values[field.name] = field.value;
 //   			 console.log(values);
	// 	});


	// });

// They must have names unfortunately
$( "#registerForm" ).submit(function( event ) {

	// serializes the form's elements.
	var formData = $(this).serializeArray();
	console.log(formData);

	// Loading
	$(this).button('loading');

    var url = "http://localhost:4567/registeruser"; // the script where you handle the form input.

    $.ajax({
    	type: "POST",
    	url: url,
           data: formData, 
           success: function(data)
           {
               alert(data); // show response from the php script.
           }
       });



    event.preventDefault();
});

});







