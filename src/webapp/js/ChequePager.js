var cheques;
var cheque_actual;

function setCheques(list) {
    cheques = list;
}

function setChequeActual(value) {
    cheque_actual = value;
    $("#currentPage").html((cheque_actual + 1)+"/"+cheques.length);
    showCheque();
}

function setCurrentCheque(idCheque) {
    var ind;
    for(ind=0; ind<cheques.length; ind++) {
        if (cheques[ind].id == idCheque)
         break;
    }
    setChequeActual((ind < cheques.length)? ind : -1);
}

function goToPreviousCheque() {
    if (cheque_actual > 0) {
        setChequeActual(cheque_actual - 1);
    }
}

function goToNextCheque() {
    if (cheque_actual < cheques.length - 1) {
        setChequeActual(cheque_actual + 1);
    }
}

function goToFirstCheque() {
    if (cheque_actual > 0) {
        setChequeActual(0);
    }
}

function goToLastCheque() {
    if (cheque_actual < cheques.length - 1) {
        setChequeActual(cheques.length - 1);
    }
}

function showCheque() {
    updateObverse();
    updateReverse();
    updateAttributes();
    chekButtonState();

}

function updateObverse() {
    $("#obverseImage").attr("src", "ViewSide.htm?id=" + cheques[cheque_actual].obverse);
}
function updateReverse() {
    $("#reverseImage").attr("src", "ViewSide.htm?id=" + cheques[cheque_actual].reverse);
}
function updateAttributes() {
    $("#codelineValue").html(cheques[cheque_actual].codeline);
    $("#causeOfIncidentDescription").html(cheques[cheque_actual].incidentCause);
    $("#backofficeStatusValue").html(cheques[cheque_actual].localizableBackofficeStatus);
    if (cheques[cheque_actual].observations != '') {
        $("#backofficeObservationsLiteral").show();
        $("#backofficeObservationsValue").show();
    } else {
        $("#backofficeObservationsLiteral").hide();
        $("#backofficeObservationsValue").hide();
    }
    if (cheques[cheque_actual].amount == "0,00" || cheques[cheque_actual].amount == "0.00") {
        $("#amountValue").hide();
    } else {
        $("#amountValue").html(cheques[cheque_actual].amount);
        $("#amountValue").show();
    }
    if(cheques[cheque_actual].onIncident) {
        $("#causeOfIncidentLiteral").show();
        $("#causeOfIncidentDescription").addClass("incidentCause2");
        $("#causeOfIncidentDescription").show();
        $("#backofficeStatusValue").addClass("incidentCause2") ;
    } else {
        $("#causeOfIncidentDescription").hide();
        $("#causeOfIncidentLiteral").hide();
        $("#backofficeStatusValue").removeClass("incidentCause2") ;
    }
    blinkText();
}

function chekButtonState(){
    disableButton("#previousButton",(cheque_actual <= 0));
    disableButton("#nextButton",(cheque_actual >= cheques.length - 1));
    disableButton("#firstButton",(cheque_actual <= 0));
    disableButton("#lastButton",(cheque_actual >= cheques.length - 1));

}

function disableButton(buttonName,isDisabled) {
    if (isDisabled) {
        $(buttonName).attr("disabled","disabled");
    } else {
        $(buttonName).attr("disabled","");
    }

}

function blinkText(){
    if(cheques[cheque_actual].isDeleted){
        $('#backofficeStatusValue').blink();
    }else{
        $('#backofficeStatusValue').unblink();
    }    
}  