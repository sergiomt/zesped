    var stre = new Array("Muy d&eacute;bil", "Muy d&eacute;bil", "D&eacute;bil", "Moderada", "Moderada",  "Fuerte", "Fuerte", "Fuerte", "Muy fuerte", "Muy fuerte", "Muy fuerte");

    var streCSS = 	new Array("#FF3300", "#FF3300", "#FFA400", "#FFB700", "#FCE200",  "#CBF400", "#8DFC00", "#78FE00", "#5BFF00", "#4CFF00", "#1CFF00");

    function easeInOut(minValue,maxValue,totalSteps,actualStep,powr) { 
	    var delta = maxValue - minValue; 
	    var stepp = minValue+(Math.pow(((1 / totalSteps) * actualStep), powr) * delta); 
	    return Math.ceil(stepp);
    } 
    
    function doWidthChangeMem(elem,startWidth,endWidth,steps,intervals,powr) { 
    	if (elem.widthChangeMemInt)
    		window.clearInterval(elem.widthChangeMemInt);
    	var actStep = 0;
    	elem.widthChangeMemInt = window.setInterval(
    		function() { 
    		  elem.currentWidth = easeInOut(startWidth,endWidth,steps,actStep,powr);
    		  elem.style.width = elem.currentWidth + "px"; 
    		  actStep++;
    		  if (actStep > steps) window.clearInterval(elem.widthChangeMemInt);
    		} 
    	,intervals);
    }

    /**
     * Calculates the strength of a password
     * @param {Object} p The password that needs to be calculated
     * @return {int} intScore The strength score of the password
     */
    function calcStrength(p) {
     	var intScore = 0;
     
     	// PASSWORD LENGTH
     	intScore += p.length;
     
     	if(p.length > 0 && p.length <= 4) {                    // length 4 or less
     		intScore += p.length;
     	}
     	else if (p.length >= 5 && p.length <= 7) {	// length between 5 and 7
     		intScore += 6;
     	}
     	else if (p.length >= 8 && p.length <= 15) {	// length between 8 and 15
     		intScore += 12;
     	}
     	else if (p.length >= 16) {               // length 16 or more
     		intScore += 18;
     	}
     
     	// LETTERS (Not exactly implemented as dictacted above because of my limited understanding of Regex)
     	if (p.match(/[a-z]/)) {              // [verified] at least one lower case letter
     		intScore += 1;
     	}
     	if (p.match(/[A-Z]/)) {              // [verified] at least one upper case letter
     		intScore += 5;
     	}
     	// NUMBERS
     	if (p.match(/\d/)) {             	// [verified] at least one number
     		intScore += 5;
     	}
     	if (p.match(/.*\d.*\d.*\d/)) {            // [verified] at least three numbers
     		intScore += 5;
     	}
     
     	// SPECIAL CHAR
     	if (p.match(/[!,@,#,$,%,^,&,_]/)) {           // [verified] at least one special character
     		intScore += 5;
     	}
     	// [verified] at least two special characters
     	if (p.match(/.*[!,@,#,$,%,^,&,_].*[!,@,#,$,%,^,&,_]/)) {
     		intScore += 5;
     	}
     
     	// COMBOS
     	if (p.match(/(?=.*[a-z])(?=.*[A-Z])/)) {        // [verified] both upper and lower case
     		intScore += 2;
     	}
     	if (p.match(/(?=.*\d)(?=.*[a-z])(?=.*[A-Z])/)) { // [verified] both letters and numbers
     		intScore += 2;
     	}
     	// [verified] letters, numbers, and special characters
     	if (p.match(/(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!,@,#,$,%,^,&,_])/)) {
     		intScore += 2;
     	}
     
     	return intScore;     
    }
     
    YUI().use('event', function (Y) {
    	var passw = Y.one("#passw");
    	passw.on('keyup', function (e) {
        	var score = 0; 
        	var maxWidth = document.getElementById("strengthMeter").offsetWidth-2;
        	var nScore = calcStrength(this.get("value"));
        
        	// Set new width
        	var nRound = Math.round(nScore * 2);

        	if (nRound > 100) nRound = 100;
        
        	var scoreWidth = (maxWidth / 100) * nRound;		
        	
        	var elem = document.getElementById("scoreBar");
        	var startWidth = elem.offsetWidth;
        	var endWidth= scoreWidth;
        	doWidthChangeMem(elem, elem.offsetWidth, scoreWidth,  10, 10, 0.5 );
        	
        	document.getElementById("fuerza").innerHTML = stre[Math.round(nRound/10)];
    	});    	
    });    