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


  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-54159960-1', 'auto');
  ga('send', 'pageview');

    
});