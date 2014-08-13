// var sparkService = "http://localhost:4567/";
var sparkService = "http://68.56.177.238:4566/"
var cookie_path_name = "test";

var pageNumbers = {};
var extractData = function(node) {
    return $(node).text().replace(/[^0-9.]/g, '');
}

function expireTimeString(seconds) {
    var now = new Date();
    var time = now.getTime();
    time += seconds * 1000;
    now.setTime(time);
    return now.toUTCString();
}

function getCookies() {
    var c = document.cookie,
        v = 0,
        cookies = {};
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
                value = $1.charAt(0) === '"' ? $1.substr(1, -1).replace(/\\(.)/g, "$1") : $1;
            cookies[name] = value;
        });
    }
    return cookies;
}

function getCookie(name) {
    return getCookies()[name + "_" + cookie_path_name];
}

function delete_cookie(name) {
    document.cookie = name + "_" + cookie_path_name + '=; path=/;expires=Thu, 01 Jan 1970 00:00:01 GMT;';

}

function showHideElementsLoggedIn() {
    var sessionId = getCookie("authenticated_session_id");
    var userType = getCookie("usertype");



    if (sessionId != null) {
        $("#dashboardhref").removeClass("hide");
        $("#loginhref").addClass("hide");
        $("#logouthref").removeClass("hide");

        if (userType == 'Creator') {
            // Set up the top bar settings to go to a specific place
            var creatorName = getCookie("username");
            $('#settings').prop("href", "/creators/settings/" + creatorName);
        } else {

        }


    } else {
        $("#dashboardhref").addClass("hide");
        $("#loginhref").removeClass("hide");
        $("#logouthref").addClass("hide");

    }


}

function showHideBidAskBuyButtons() {


}

function fillUserHighChartStandardTemplate(url, id, yAxisLabel, symbol) {
    var url = sparkService + url // the script where you handle the form input.
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
        error: function(request, status, error) {

            toastr.error(request.responseText);
        }
    });

}

function fillUserHighChartPieChartTemplate(url, id) {
    var url = sparkService + url // the script where you handle the form input.
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
        error: function(request, status, error) {

            toastr.error(request.responseText);
        }
    });


}

function fillUserInfoMustache(url) {
    var url = sparkService + url
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
            Mustache.parse(template); // optional, speeds up future uses
            var rendered = Mustache.render(template, jsonObj);
            $('#userDropdown').html(rendered);

            console.log(jsonObj);
            console.log(template);
            console.log(rendered);


        },
        error: function(request, status, error) {

            toastr.error(request.responseText);
        }
    });

}

function fillUserInfoMustacheFromCookie() {

    // var jsonObj = JSON.parse(data);
    // var jsonObj = jQuery.parseJSON(data);
    jsonObj = getCookie('username');

    if (jsonObj != null) {
        var template = $('#usernameTemplate').html();
        Mustache.parse(template); // optional, speeds up future uses
        var rendered = Mustache.render(template, jsonObj);
        $('#userDropdown').html(rendered);


        $("#settings").attr("href", "/users/settings/" + jsonObj);
        console.log(jsonObj);
        console.log(template);
        console.log(rendered);

    } else {
        $('#userDropdown').html('');
    }

}



function fillTableFromMustache(url, templateHtml, divId, tableId) {
    //         $.tablesorter.addParser({ 
    //           id: 'my_date_column', 
    //           is: function(s) { 
    //       // return false so this parser is not auto detected 
    //       return false; 
    //     }, 
    //     format: function(s) { 
    //       console.log(s);
    //       var timeInMillis = new Date.parse(s);

    //       // var date = new Date(parseInt(s));
    //       return date;         
    //     }, 
    //   // set type, either numeric or text 
    //   type: 'numeric' 
    // });

    var url = sparkService + url // the script where you handle the form input.
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
            var jsonObj = JSON.parseWithDate(data);


            Mustache.parse(templateHtml); // optional, speeds up future uses
            var rendered = Mustache.render(templateHtml, jsonObj);
            $(divId).html(rendered);
            $(tableId).tablesorter({
                debug: true,
                textExtraction: extractData
                //     headers: { 
                //   0: {       // Change this to your column position
                //     sorter:'my_date_column' 
                //   } 
                // }
            });
            console.log(jsonObj);
            console.log(templateHtml);
            console.log(rendered);


        },
        error: function(request, status, error) {

            toastr.error(request.responseText);
        }
    });

}

function fillFieldFromMustache(url, templateId, divId, isMoney) {
    var url = sparkService + url // the script where you handle the form input.
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
            // var jsonObj = JSON.parse(data);
            // if (jsonObj > 0 && isMoney) {
            //   jsonObj = jsonObj.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
            // }
            var template = $(templateId).html();
            Mustache.parse(template); // optional, speeds up future uses
            var rendered = Mustache.render(template, data);
            $(divId).html(rendered);
            console.log(jsonObj);
            console.log(template);
            console.log(rendered);


        },
        error: function(request, status, error) {

            toastr.error(request.responseText);
        }
    });

}

function fillJSONFieldFromMustache(url, templateId, divId, isMoney) {
    var url = sparkService + url // the script where you handle the form input.
    return $.ajax({
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
            // if (jsonObj > 0 && isMoney) {
            //   jsonObj = jsonObj.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
            // }
            var template = $(templateId).html();
            Mustache.parse(template); // optional, speeds up future uses
            var rendered = Mustache.render(template, jsonObj);
            $(divId).html(rendered);
            console.log(jsonObj);
            console.log(template);
            console.log(rendered);


        },
        error: function(request, status, error) {

            toastr.error(request.responseText);
        }
    });

}

function setupCreatorSearch() {

    remoteURL = sparkService + 'creators_search/%QUERY';
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

    }).on('typeahead:selected', function(e, data) {
        $(this).submit();
    });

    $("#creator_search").submit(function(event) {
        var formData = $("#creator_search").serializeArray();
        console.log(formData);
        var searchString = formData[0].value;

        console.log(searchString);
        var url = "/creators/main/" + searchString;
        window.location.replace(url);

        event.preventDefault();
    });
}

function setupMiniSubmenu() {
    $('#slide-submenu').on('click', function() {
        $(this).closest('.list-group').fadeOut('slide', function() {
            $('.mini-submenu').fadeIn();
        });
        $('#main_col').toggleClass('col-sm-offset-2 col-md-offset-2 col-md-10 col-md-12');
        $('#othermain_col').toggleClass('col-md-offset-2 col-md-10 col-md-8');
        // $('#side_col').toggleClass('span0 span3');

    });

    $('.mini-submenu').on('click', function() {
        $(this).next('.list-group').toggle('slide');
        $('.mini-submenu').hide();
        $('#main_col').toggleClass('col-sm-10 col-sm-offset-2 col-md-10 col-md-offset-2');
        $('#othermain_col').toggleClass('col-md-offset-2 col-md-8 col-md-10');
    });
}

function setupLogout() {
    var sessionId = getCookie("authenticated_session_id");
    var url = sparkService + "/user_logout"
    $('#logouthref').click(function() {
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
                console.log(url);
                delete_cookie("authenticated_session_id");
                delete_cookie("username");
                delete_cookie("usertype");
                setTimeout(
                    function() {
                        var url = "/";
                        window.location.replace(url);

                    }, 1500);


            },
            error: function(request, status, error) {

                toastr.error(request.responseText);
            }
        });



        // showHideElementsLoggedIn();




    });
}

function setupSummerNote(url, id, sqlColName) {

    var url = sparkService + url // the script where you handle the form input.
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
                height: 300, // set editor height

                minHeight: null, // set minimum height of editor
                maxHeight: null, // set maximum height of editor

                focus: false, // set focus to editable area after initializing summernote
            });
            $(id).code(mainBody);


        },
        error: function(request, status, error) {

            toastr.error(request.responseText);
        }

    });



}

function saveSummerNote(shortUrl, btn, id) {
    // !!!!!!They must have names unfortunately
    $(btn).click(function(event) {


        var sHTML = $(id).code();
        console.log(sHTML);

        var btn = $(this);
        // Loading
        btn.button('loading');

        var url = sparkService + shortUrl; // the script where you handle the form input.

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
            error: function(request, status, error) {

                toastr.error(request.responseText);
            }
        });



        event.preventDefault();
    });
}

function fillSimpleText(url, divId) {
    var url = sparkService + url // the script where you handle the form input.
    $.ajax({
        type: "GET",
        url: url,
        xhrFields: {
            withCredentials: true
        },
        // data: seriesData, 
        success: function(data, status, xhr) {
            console.log(data);
            $(divId).html(data);


        },
        error: function(request, status, error) {

            toastr.error(request.responseText);
        }
    });
}

function simpleFetch(url) {
    var url = sparkService + url // the script where you handle the form input.
    return $.ajax({
        type: "GET",
        url: url,
        xhrFields: {
            withCredentials: true
        },
        // data: seriesData, 
        success: function(data, status, xhr) {
            console.log('data = ' + data);
            // return data;


        },
        error: function(request, status, error) {

            toastr.error(request.responseText);
        }
    });
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function navigateWithParams() {
    // all <a> tags containing a certain rel=""
    $("a[rel~='keep-params']").click(function(e) {
        e.preventDefault();

        // var params = window.location.search,
        // dest = $(this).attr('href') + params;
        var name = window.location.pathname.split('/').pop();
        var dest = $(this).attr('href') + '/' + name;

        // in my experience, a short timeout has helped overcome browser bugs
        window.setTimeout(function() {
            window.location.href = dest;
        }, 100);
    });
}

function setupDepositButton(shortUrl, btnId, formId, modalId) {
    var userName = getCookie("username");
    simpleFetch(userName + '/get_users_funds_current').done(function(result) {
        $('[name="deposit"]').attr('placeholder', 'Current funds:' + result);
    });
    getJson('/get_users_settings').done(function(e) {

        // this is handled in userdashboard_settings.js
        console.log("settings = " + e);

    });

    // !!!!!!They must have names unfortunately
    $(btnId).click(function(event) {
        var formData = $(formId).serializeArray();

        var btnId = $(this);
        // Loading
        btnId.button('loading');

        var url = sparkService + shortUrl; // the script where you handle the form input.

        $.ajax({
            type: "POST",
            url: url,
            xhrFields: {
                withCredentials: true
            },
            data: formData,
            success: function(data, status, xhr) {


                xhr.getResponseHeader('Set-Cookie');
                // document.cookie="authenticated_session_id=" + data + 
                // "; expires=" + expireTimeString(60*60); // 1 hour (field is in seconds)
                // Hide the modal, reset the form, show successful

                // Loading
                btnId.button('reset');

                toastr.success(data);
                $(modalId).modal('hide');

                $(formId)[0].reset();


                console.log(document.cookie);

            },
            error: function(request, status, error) {

                toastr.error(request.responseText);
            }
        });



        event.preventDefault();
    });
}

function getJson(shortUrl) {
    var url = sparkService + shortUrl // the script where you handle the form input.
    return $.ajax({
        type: "GET",
        url: url,
        xhrFields: {
            withCredentials: true
        },
        // data: seriesData, 
        success: function(data, status, xhr) {
            // console.log(data);
            // var jsonObj = JSON.parse(data);
            // JSON.useDateParser();
            // var jsonObj = jQuery.parseJSON(data);
            // JSON.useDateParser();
            // var jsonObj = JSON.parse(data);

        },
        error: function(request, status, error) {

            toastr.error(request.responseText);
        }
    });
}

function standardFormPost(shortUrl, formId, modalId) {
    // !!!!!!They must have names unfortunately
    // An optional arg
    modalId = (typeof modalId === "undefined") ? "defaultValue" : modalId;

    // serializes the form's elements.
    var formData = $(formId).serializeArray();
    console.log(formData);

    // Loading
    $(this).button('loading');

    var url = sparkService + shortUrl; // the script where you handle the form input.
    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        xhrFields: {
            withCredentials: true
        },
        data: formData,
        success: function(data, status, xhr) {

            console.log('posted the data');
            xhr.getResponseHeader('Set-Cookie');
            // document.cookie="authenticated_session_id=" + data + 
            // "; expires=" + expireTimeString(60*60); // 1 hour (field is in seconds)
            // Hide the modal, reset the form, show successful

            $(formId)[0].reset();
            $(modalId).modal('hide');
            console.log(modalId);
            toastr.success(data);




            console.log(document.cookie);
            return data;

        },
        error: function(request, status, error) {
            toastr.error(request.responseText);
        }
    });

    event.preventDefault();
    return false;



    // event.preventDefault();


}

function setupPagedTable(shortUrl, templateHtml, divId, tableId) {
    var pageNum = pageNumbers[tableId];

    var nextId = divId + "_pager_next";
    var prevId = divId + "_pager_prev";
    console.log(nextId);
    fillTableFromMustache(shortUrl + pageNum,
        templateHtml, divId, tableId);

    $(nextId).click(function(e) {
        pageNum++;
        $(prevId).removeClass('disabled');

        fillTableFromMustache(shortUrl + pageNum,
            templateHtml, divId, tableId);

    });
    $(prevId).click(function(e) {
        if (pageNum > 1) {
            pageNum--;

            fillTableFromMustache(shortUrl + pageNum,
                templateHtml, divId, tableId);
        }
        if (pageNum == 1) {
            $(this).addClass('disabled');
            return;
        }


    });
}

function showRecaptcha(element) {
    Recaptcha.create("6LfgKvcSAAAAAJGQDr6NtYgCqfKAshsFqZDDNJ-N", element, {
        theme: "blackglass",
        //callback: Recaptcha.focus_response_field
    });
}

function setupDisqus(creatorName) {



}