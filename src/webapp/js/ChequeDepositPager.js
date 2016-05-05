var cheque_actual;
var cheques;

function setCheques(list) {
    cheques = list;
    chequeProcess();
}

function chequeProcess() {
    for(cheque_actual=0; cheque_actual<cheques.length; cheque_actual++){
        blinkText();
    }
}

function blinkText(){
    if(cheques[cheque_actual].isDeleted){
        $('#' + cheques[cheque_actual].id).blink();
    }
}  