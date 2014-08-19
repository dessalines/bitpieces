$(document).ready(function() {
    var userType = getCookie('usertype');
    var userName = getCookie('username');
    // set up the correct dashboard if its a creator
    if (userType == 'Creator') {
        $("#dashboardhref").prop("href", "/creators/main/" + userName);
    } else if (userType == 'User') {
        $("#dashboardhref").prop("href", "/users/overview/" + userName);
    }


    setupCreatorSearch();
    fillUserInfoMustacheFromCookie();
    setupLogout();
    showHideElementsLoggedIn();
    
});