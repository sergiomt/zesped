
function showAlertMessage(message) {
    $("#dialog-message").html(message);
    $("#dialog-message").dialog("open");
}

function setGoToDepositOnClick() {
    $("#goToDeposit").click(function() {
        var idDeposit = $("#idDeposit").val();
        if (idDeposit != null && idDeposit != ""){
            if (isNaN(idDeposit)) {
                showAlertMessage(messages.errorNotANumber)
            }else{
                $.post("ValidateDepositId.htm",
                {
                    "idDocument":$("#idDeposit").val()
                },
                    function(response){
                        isValidate = eval(response);
                        if (isValidate)  {
                            window.location="ViewDeposit.htm?depositId=" + idDeposit;
                        }else{
                            showAlertMessage(messages.errorDepositNotFound);
                        }
                    }
                )
            }
        } 
    })
}

function setSearchClick() {
    $("#search").click(function() {
        $(".error").html("");
        $.post("SearchDeposit.htm",
            {
                "dateFrom":$("#dateFrom").val(),
                "dateTo":$("#dateTo").val(),
                "maxResults":$("#maxResults").val(),
                "account":$("#accountsCombo").val(),
                "ascendingOrder":$("#ascendingOrder")[0].checked
            },
            function(response){
                if ((response.validationErrors == null) && response.deposits == null) {
                    showAlertMessage("Se ha producido un error en el servidor");
                } else {
                    if (response.validationErrors)  {
                        datos =response.validationErrors;
                        for (i = 0; i< datos.length; i++) {
                            $("#" + datos[i].key + "_error").html(datos[i].error);
                        }
                    }else{
                        var table = document.getElementById("depositsGrid")
                        clearRows(table)
                        var infoRows = response.deposits.depositList
                        addRows(table, infoRows)
                        setTotalDepositsMessage(response.deposits.totalSize, infoRows.length)
                    }
                }
            }
            )
    });
}

function crearGridDepositos(datos, totalResults) {

    var table = document.getElementById("depositsGrid")
    addRows(table, datos)
    setTotalDepositsMessage(totalResults, datos.length)

}

function addRows(table, datos){
    
    for(var i=0;i<datos.length;i++) {
        addRow(datos[i],table)
    }
}

function clearRows(table) {

    for(var i = table.rows.length; i > 1;i--)
    {
        document.getElementById("depositsGrid").deleteRow(i -1);
    }
}

function addRow(element, table){


    var row = table.insertRow(-1)
    row.setAttribute("class", "depositRow")
    row.onclick=function(){
        window.location="ViewDeposit.htm?depositId=" + element.idDms
    }

    var td1 = row.insertCell(0)
    td1.innerHTML=element.modificationDate

    var td2 = row.insertCell(1)
    td2.innerHTML=element.idDeposit

    var td3 = row.insertCell(2)
    td3.innerHTML=element.accountNumberToShow

    var td4 = row.insertCell(3)
    td4.innerHTML=element.numCheques

    var td5 = row.insertCell(4)
    td5.innerHTML=element.totalAmountWithCurrency

    var td6 = row.insertCell(5)
    td6.innerHTML=element.reference

    var td7 = row.insertCell(6)
    if(element.onIncident || element.isBackofficeRejected) {
        td7.className = "incidentCause";
        td7.setAttribute("id", "backofficeRejected");
        td7.setAttribute("title", element.issue);
    } else {
        td7.className = "";
    }
    td7.innerHTML = element.localizableBackofficeStatus;
}

function showDepositMessage(message) {
    $("#totalDepositsMessage").html(message)
}

function setTotalDepositsMessage(total, showed) {
    var message = "";
    if (total == 0) {
        message = messages.noDeposit
    } else if (total > showed) {
        message = messages.format(messages.totalDepositPattern, showed, total)
    }
    showDepositMessage(message)
}

function configDialogWindow() {
    $("#dialog-message").dialog({
                        modal:true,
			autoOpen: false,
                        buttons: {
				Ok: function() {
					$( this ).dialog( "close" )
				}
			}

		})
}