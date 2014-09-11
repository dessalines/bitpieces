$(document).ready(function() {

    sessionId = getCookie("authenticated_session_id");
    userType = getCookie("usertype");

    // fillUserInfoMustache('get_user_data');

    var userName = window.location.pathname.split('/').pop();
    $('#page_title').text(userName);
     document.title = userName + ' - Bitpieces';

    setupMiniSubmenu();
    navigateWithParams();




});