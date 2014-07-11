$(document).ready(function(){


    sessionId = getCookie("authenticated_session_id");
    console.log(sessionId);


    var url = "http://localhost:4567/" + sessionId + "/get_prices_for_user"// the script where you handle the form input.
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
            var jsonObj = jQuery.parseJSON(data);
            // var jsonObj = JSON.stringify(data);
            console.log(jsonObj);
            standardTemplate("#highcharts-chart-area", jsonObj);

            },
            error: function (request, status, error) {
                
                toastr.error(request.responseText);
            }
        });





});