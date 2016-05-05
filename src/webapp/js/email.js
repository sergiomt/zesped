
// ----------------------------------------------------------------------------

function check_email(email) {
  var ok = "1234567890qwertyuiop[]asdfghjklzxcvbnm.@-_QWERTYUIOPASDFGHJKLZXCVBNM";
  var re_one;
  var re_two;
  var elen = email.length;
      
  for (var i=0; i<elen; i++)
    if (ok.indexOf(email.charAt(i))<0)
      return (false);
  
  if (document.images) {
    re_one = /(@.*@)|(\.\.)|(^\.)|(^@)|(@$)|(\.$)|(@\.)/;
    re_two = /^.+\@(\[?)[a-zA-Z0-9\-\.]+\.([a-zA-Z]{2}|[0-9]{1,3}|aero|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel)(\]?)$/;
      
    if (!email.match(re_one) && email.match(re_two))
      return (true);		
  } // fi()
  
  return (false);
} // check_email

//----------------------------------------------------------------------------

  String.prototype.trim = function() { return (this.replace(/^[\s\xA0]+/, "").replace(/[\s\xA0]+$/, "")); };