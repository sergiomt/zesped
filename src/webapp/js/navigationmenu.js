    YUI({ filter: 'raw' }).use("transition", "node", function (Y) {
      var setBgImg = function (id, src) {
    	  document.getElementById(id).style.backgroundImage="url(css/"+src+".gif)";    	  
      };
      var highlightCurrent = function () {
    	  var lpn = location.pathname;
          if (lpn.indexOf("Capture")>=0)
        	  setBgImg("workflow_capture","digitalizar_current");
          else if (lpn.indexOf("ListNew")>=0)
        	  setBgImg("workflow_enterdata","entrardatos_current");
          else if (lpn.indexOf("ListPending")>=0)
        	  setBgImg("workflow_enterdata","aprobar_current");
          else if (lpn.indexOf("ListApproved")>=0)
        	  setBgImg("workflow_approve","gestionar_current");
      };
      var highlightCapture = function (e) {
    	  setBgImg("workflow_capture","digitalizar_green");
    	  setBgImg("workflow_enterdata","entrardatos_gray");
    	  setBgImg("workflow_approve","aprobar_gray");
    	  setBgImg("workflow_manage","gestionar_gray");
      };
      var lowlightCapture = function (e) {
    	  if (location.pathname.indexOf("Capture")<0)
    		  setBgImg("workflow_capture","digitalizar_"+(location.pathname.indexOf("Capture")>=0 ? "current" : "gray"));
          highlightCurrent();
      };
      var highlightEnterData = function (e) {
    	  setBgImg("workflow_enterdata","entrardatos_green");
    	  setBgImg("workflow_capture","digitalizar_gray");
    	  setBgImg("workflow_approve","aprobar_gray");
    	  setBgImg("workflow_manage","gestionar_gray");
      };
      var lowlightEnterData = function (e) {
          if (location.pathname.indexOf("ListNew")<0)
        	  setBgImg("workflow_enterdata","entrardatos_"+(location.pathname.indexOf("ListNew")>=0 ? "current" : "gray"));
          highlightCurrent();
      };
      var highlightApprove = function (e) {
    	  setBgImg("workflow_approve","aprobar_green");
    	  setBgImg("workflow_capture","digitalizar_gray");
    	  setBgImg("workflow_enterdata","entrardatos_gray");
    	  setBgImg("workflow_manage","gestionar_gray");
      };
      var lowlightApprove = function (e) {
    	  if (location.pathname.indexOf("ListPending")<0)
    		  setBgImg("workflow_approve","aprobar_"+(location.pathname.indexOf("ListPending")>=0 ? "current" : "gray"));
          highlightCurrent();
      };
      var highlightManage = function (e) {
    	  setBgImg("workflow_manage","gestionar_green");
    	  setBgImg("workflow_capture","digitalizar_gray");
    	  setBgImg("workflow_enterdata","entrardatos_gray");
    	  setBgImg("workflow_approve","aprobar_gray");
      };
      var lowlightManage = function (e) {
    	  if (location.pathname.indexOf("ListApproved")<0)
    		  setBgImg("workflow_manage","gestionar_"+(location.pathname.indexOf("ListApproved")>=0 ? "current" : "gray"));
          highlightCurrent();
      };
      Y.on("mouseover", highlightCapture, "#wf_capture");
      Y.on("mouseover", highlightCapture, "#workflow_capture");
      Y.on("mouseout" , lowlightCapture, "#workflow_capture"); 
      Y.on("mouseover", highlightEnterData, "#wf_enterdata");
      Y.on("mouseover", highlightEnterData, "#workflow_enterdata"); 
      Y.on("mouseout" , lowlightEnterData, "#workflow_enterdata"); 
      Y.on("mouseover", highlightApprove, "#wf_approve");
      Y.on("mouseover", highlightApprove, "#workflow_approve"); 
      Y.on("mouseout" , lowlightApprove , "#workflow_approve"); 
      Y.on("mouseover", highlightManage, "#wf_manage");
      Y.on("mouseover", highlightManage, "#workflow_manage"); 
      Y.on("mouseout" , lowlightManage , "#workflow_manage");
      highlightCurrent();
    });
