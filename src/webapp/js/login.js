    YUI({ filter: 'raw' }).use("transition", "node", function (Y) {
      var validateLoginRequest = function (e) {
        YUI().use("io-form", "transition", function(Y) {
          function loginComplete(id, o, args) {
            var data = o.responseText.split("`");
            if (data[0]=="OK") {
              document.location = "CaptureInvoice.action";
            } else {
              document.location = "enter.jsp?e="+data[1];
            }
          };
          var frm = document.forms["logfrm"];
          if (!check_email(frm.email.value)) {
            alert ("El e-mail es obligatorio");
            frm.email.focus();
          } else if (frm.passw.value.length==0) {
              alert ("La contraseña es obligatoria");
            frm.text.focus();
          } else {
        	document.getElementById("loginbutton").style.display="none";
        	document.getElementById("loading").style.display="block";
            Y.io("login.jsp", { method:'POST', form:{id:frm}, on: {'complete':loginComplete} });
            frm.reset();
          }
        });
      };
      Y.on("click", validateLoginRequest, "#loginbutton"); 
    });
