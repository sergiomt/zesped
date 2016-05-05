
function depositInAccount(idDocumento,holder,account) {
    $.post("ValidateDepositAmount.htm",
    {
        "account": account,
        "holder": holder,
        "cleanGroupUserSessionInfo": true
    },
    function(response) {
        var resultado = eval(response);
        if (resultado.messageError != "") {    
            if (resultado.totalAmountInvalid){
                if (resultado.mayPostponeDeposits){
                    var resp = window.confirm(messages.maxAmountPerDepositer);
                    if(resp) { 
                        window.location = "Deposit.htm?idDoc=" + idDocumento;
                    }
                } else {
                    window.alert(messages.maxAmountPerDayNotPostpone);
                }
            } else {    
                window.alert(resultado.messageError);
            }
        } else {
            window.location = "Deposit.htm?idDoc=" + idDocumento;
        }          
    })
}

function convertRowsToLinks(xTableId){
    var rows = document.getElementById(xTableId).getElementsByTagName("tr");

    for(i=0;i<rows.length;i++){
        var link = rows[i].getElementsByTagName("a");
        if(link.length == 1){
            rows[i].onclick = new Function("document.location.href='" + link[0].href + "'");
        }
    }
}