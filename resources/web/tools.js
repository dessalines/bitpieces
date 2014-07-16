function expireTimeString(seconds) {
	var now = new Date();
	var time = now.getTime();
	time += seconds * 1000;
	now.setTime(time);
	return now.toUTCString();
}

function getCookies() {
  var c = document.cookie, v = 0, cookies = {};
  if (document.cookie.match(/^\s*\$Version=(?:"1"|1);\s*(.*)/)) {
    c = RegExp.$1;
    v = 1;
  }
  if (v === 0) {
    c.split(/[,;]/).map(function(cookie) {
      var parts = cookie.split(/=/, 2),
      name = decodeURIComponent(parts[0].trimLeft()),
      value = parts.length > 1 ? decodeURIComponent(parts[1].trimRight()) : null;
      cookies[name] = value;
    });
  } else {
    c.match(/(?:^|\s+)([!#$%&'*+\-.0-9A-Z^`a-z|~]+)=([!#$%&'*+\-.0-9A-Z^`a-z|~]*|"(?:[\x20-\x7E\x80\xFF]|\\[\x00-\x7F])*")(?=\s*[,;]|$)/g).map(function($0, $1) {
      var name = $0,
      value = $1.charAt(0) === '"'
      ? $1.substr(1, -1).replace(/\\(.)/g, "$1")
      : $1;
      cookies[name] = value;
    });
  }
  return cookies;
}
function getCookie(name) {
  return getCookies()[name];
}
function delete_cookie( name ) {
  document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}

function showHideElementsLoggedIn() {
  sessionId = getCookie("authenticated_session_id");

  if (sessionId != null) {
    $("#dashboardhref").removeClass("hide");
    $("#loginhref").addClass("hide");
    $("#logouthref").removeClass("hide");

  } else {
    $("#dashboardhref").addClass("hide");
    $("#loginhref").removeClass("hide");
    $("#logouthref").addClass("hide");

  }
}

function fillUserHighChartStandardTemplate(url, id, yAxisLabel, symbol) {
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
            var jsonObj = jQuery.parseJSON(data);
            // var jsonObj = JSON.stringify(data);
            // console.log(jsonObj);
            standardTemplate(id, jsonObj, yAxisLabel, symbol);

          },
          error: function (request, status, error) {

            toastr.error(request.responseText);
          }
        });

     }

     function fillUserHighChartPieChartTemplate(url, id) {
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
            var jsonObj = jQuery.parseJSON(data);
            // var jsonObj = JSON.stringify(data);
            // console.log(jsonObj);
            pieChartTemplate(id, jsonObj);

          },
          error: function (request, status, error) {

            toastr.error(request.responseText);
          }
        });


     }

     function fillUserInfoMustache(url) {
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
            var jsonObj = jQuery.parseJSON(data);
            
            var template = $('#usernameTemplate').html();
            Mustache.parse(template);   // optional, speeds up future uses
            var rendered = Mustache.render(template,jsonObj);
            $('#userDropdown').html(rendered);

            console.log(jsonObj);              
            console.log(template);
            console.log(rendered);
            

          },
          error: function (request, status, error) {

            toastr.error(request.responseText);
          }
        });

     }

     function fillUserInfoMustacheFromCookie() {

            // var jsonObj = JSON.parse(data);
            // var jsonObj = jQuery.parseJSON(data);
            jsonObj = getCookie('username');
            
            var template = $('#usernameTemplate').html();
            Mustache.parse(template);   // optional, speeds up future uses
            var rendered = Mustache.render(template,jsonObj);
            $('#userDropdown').html(rendered);

            console.log(jsonObj);              
            console.log(template);
            console.log(rendered);
            

          } 



          function fillTableFromMustache(url, templateId, divId, tableId) {
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
            JSON.useDateParser();
            var jsonObj = JSON.parse(data);
            
            var template = $(templateId).html();
            Mustache.parse(template);   // optional, speeds up future uses
            var rendered = Mustache.render(template,jsonObj);
            $(divId).html(rendered);
            $(tableId).tablesorter({debug: true}); 
            console.log(jsonObj);              
            console.log(template);
            console.log(rendered);
            

          },
          error: function (request, status, error) {

           toastr.error(request.responseText);
         }
       });

     }

     function fillFieldFromMustache(url, templateId, divId, isMoney) {
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
            if (jsonObj > 0 && isMoney) {
              jsonObj = jsonObj.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
            }
            var template = $(templateId).html();
            Mustache.parse(template);   // optional, speeds up future uses
            var rendered = Mustache.render(template,jsonObj);
            $(divId).html(rendered);
            console.log(jsonObj);              
            console.log(template);
            console.log(rendered);
            

          },
          error: function (request, status, error) {

           toastr.error(request.responseText);
         }
       });

}

function setupCreatorSearch() {

  remoteURL = 'http://localhost:4567/creators_search/%QUERY';
  var creatorsList = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('username'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    // prefetch: '../data/films/post_1960.json',
    remote: remoteURL
  });

  creatorsList.initialize();

  $('#creator_search .typeahead').typeahead(null, {
    name: 'creators_list',
    displayKey: 'username',
    source: creatorsList.ttAdapter()
  });

  $( "#creator_search" ).submit(function( event ) {
   var formData = $("#creator_search").serializeArray();
   console.log(formData);
   var searchString = formData[0].value;

   console.log(searchString);
   var url = "http://localhost/creators_pages/" + searchString;
   window.location.replace(url);

   event.preventDefault();
 });
}

function setupMiniSubmenu() {
 $('#slide-submenu').on('click',function() {             
  $(this).closest('.list-group').fadeOut('slide',function(){
    $('.mini-submenu').fadeIn();  
  });
  $('#main_col').toggleClass('col-sm-offset-2 col-md-offset-2 col-md-10 col-md-12');
    // $('#side_col').toggleClass('span0 span3');

  });

 $('.mini-submenu').on('click',function(){
  $(this).next('.list-group').toggle('slide');
  $('.mini-submenu').hide();
  $('#main_col').toggleClass('col-sm-10 col-sm-offset-2 col-md-10 col-md-offset-2');
});
}

function setupLogout() {
  var url = "http://localhost:4567/" + sessionId + "/user_logout"
  $('#logouthref').click(function(){ 
   $.ajax({
    type: "POST",
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

            
            toastr.success(data);
            console.log('got here');
            setTimeout(
              function() 
              {
                var url = "http://localhost/carousel";
                window.location.replace(url);

              }, 2000);
            

          },
          error: function (request, status, error) {

           toastr.error(request.responseText);
         }
       });


   delete_cookie("authenticated_session_id");
    // showHideElementsLoggedIn();
    



  });
}

function setupSummerNote(url, id, sqlColName) {

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



                       
            var mainBody = jsonObj[sqlColName];
            console.log(jsonObj);   
            console.log(mainBody);

            $(id).summernote({
                height: 300,                 // set editor height

                minHeight: null,             // set minimum height of editor
                maxHeight: null,             // set maximum height of editor

                focus: true,                 // set focus to editable area after initializing summernote
              });
            $(id).code(mainBody);
            

          },
          error: function (request, status, error) {

           toastr.error(request.responseText);
         }
       });







}

function saveSummerNote(shortUrl, btn, id) {
  // !!!!!!They must have names unfortunately
  $( btn ).click(function( event ) {


    var sHTML = $(id).code();
    console.log(sHTML);

    var btn = $(this);
  // Loading
  btn.button('loading');

    var url = "http://localhost:4567/" + shortUrl ; // the script where you handle the form input.

    $.ajax({
      type: "POST",
      url: url,
      xhrFields: {
        withCredentials: true
      },
      data: sHTML, 
      success: function(data, status, xhr) {


        xhr.getResponseHeader('Set-Cookie');
                // document.cookie="authenticated_session_id=" + data + 
                // "; expires=" + expireTimeString(60*60); // 1 hour (field is in seconds)
                // Hide the modal, reset the form, show successful

                  // Loading
                  btn.button('reset');

                  toastr.success('Page saved')



                  console.log(document.cookie);

                },
                error: function (request, status, error) {

                  toastr.error(request.responseText);
                }
              });



    event.preventDefault();
  });
}